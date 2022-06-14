package stringstocsv.export.writer;

import com.opencsv.CSVWriter;
import stringstocsv.model.ResourceString;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringResourceCsvWriter {

    /**
     * @param path The path of the new file to be created.
     * @param defaultLanguageStrings Resources for the default language.
     * @param resourceStrings Localized resources to write to the file.
     */
    public void export(Path path, List<ResourceString> defaultLanguageStrings, List<ResourceString> resourceStrings) {

        try {
            Files.createDirectories(path.getParent());

            try( CSVWriter writer = new CSVWriter(new FileWriter(path.toFile()))) {

                writer.writeNext(new String[] {"key", "Default (Do not change)", "Localized (Translate here)"});

                Map<String, ResourceString> resourceStringsByName = resourceStrings.stream()
                        .collect(Collectors.toMap(ResourceString::getName, Function.identity()));

                for (ResourceString defaultLanguageString : defaultLanguageStrings) {

                    ResourceString localizedString = resourceStringsByName.get(defaultLanguageString.getName());
                    String localizedStringValue = localizedString != null ? localizedString.getValue() : "";

                    writer.writeNext(new String[] {
                            defaultLanguageString.getName(),
                            defaultLanguageString.getValue(),
                            localizedStringValue });
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
