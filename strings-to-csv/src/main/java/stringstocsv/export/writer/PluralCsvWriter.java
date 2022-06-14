package stringstocsv.export.writer;

import com.opencsv.CSVWriter;
import stringstocsv.model.Plural;
import stringstocsv.model.PluralItem;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PluralCsvWriter {

    /**
     * @param path The path of the new file to be created.
     * @param defaultLanguagePlurals Resources for the default language.
     * @param plurals Localized resources to write to the file.
     */
    public void export(Path path, List<Plural> defaultLanguagePlurals, List<Plural> plurals) {

        try {
            Files.createDirectories(path.getParent());

            try (CSVWriter writer = new CSVWriter(new FileWriter(path.toFile()))) {

                writer.writeNext(new String[] {"key", "quantity", "Default (Do not change)", "Localized (Translate here)"});

                Map<String, Plural> pluralsByName = plurals.stream()
                        .collect(Collectors.toMap(Plural::getName, Function.identity()));

                for (Plural defaultLanguagePlural : defaultLanguagePlurals) {

                    Plural localizedPlural = pluralsByName.get(defaultLanguagePlural.getName());
                    writePlural(writer, defaultLanguagePlural, localizedPlural);
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writePlural(
            CSVWriter writer,
            Plural defaultLanguagePlural,
            Plural localizedPlural) {

        for (PluralItem defaultLanguagePluralItem : defaultLanguagePlural.getPluralItems()) {

            String localizedPluralItemValue;
            if (localizedPlural != null) {

                localizedPluralItemValue = getLocalizedValueForQuantity(defaultLanguagePluralItem, localizedPlural);
                if (localizedPluralItemValue == null) {
                    continue;
                }
            }
            else {
                localizedPluralItemValue = "";
            }

            writer.writeNext(new String[] {
                    defaultLanguagePlural.getName(),
                    defaultLanguagePluralItem.getQuantity().toString(),
                    defaultLanguagePluralItem.getValue(),
                    localizedPluralItemValue });
        }

        handleLocalizedQuantitiesThatAreNotInDefaultLanguage(writer, defaultLanguagePlural, localizedPlural);
    }

    private String getLocalizedValueForQuantity(
            PluralItem defaultLanguagePluralItem,
            Plural localizedPlural) {

        Optional<PluralItem> temp = localizedPlural.getPluralItems()
                .stream()
                .filter(p -> p.getQuantity() == defaultLanguagePluralItem.getQuantity())
                .findAny();

        // If the localized language does not have some quantities (some quantities do not apply for
        // some languages), skip it.
        if (!temp.isPresent()) {
            return null;
        }

        return temp
                .filter(p -> !defaultLanguagePluralItem.getValue().equals(p.getValue()))
                .map(PluralItem::getValue)
                .orElse("");
    }

    private void handleLocalizedQuantitiesThatAreNotInDefaultLanguage(
            CSVWriter writer,
            Plural defaultLanguagePlural,
            Plural localizedPlural) {

        if (localizedPlural != null) {

            List<PluralItem> additionalLocalizedItems = localizedPlural.getPluralItems()
                    .stream()
                    .filter(pluralItem -> defaultLanguagePlural.getPluralItems().stream().noneMatch(p -> pluralItem.getQuantity() == p.getQuantity()))
                    .collect(Collectors.toList());

            for (PluralItem pluralItem : additionalLocalizedItems) {

                writer.writeNext(new String[] {
                        defaultLanguagePlural.getName(),
                        pluralItem.getQuantity().toString(),
                        "",
                        pluralItem.getValue() });
            }
        }
    }
}
