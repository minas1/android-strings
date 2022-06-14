package stringstocsv.export;

import stringstocsv.LanguagesSupplier;
import stringstocsv.export.converter.PluralsXmlToCsvConverter;
import stringstocsv.export.converter.StringsXmlToCsvConverter;
import stringstocsv.export.writer.PluralCsvWriter;
import stringstocsv.export.writer.StringResourceCsvWriter;
import stringstocsv.model.Language;
import stringstocsv.model.Plural;
import stringstocsv.model.ResourceString;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Exporter {

    private final Path rootPath;
    private final LanguagesSupplier languagesSupplier;
    private final ResourcesPathsReader resourcesPathsReader;
    private final StringsXmlToCsvConverter stringsXmlToCsvConverter;
    private final PluralsXmlToCsvConverter pluralsXmlToCsvConverter;
    private final StringResourceCsvWriter stringResourceCsvWriter;
    private final PluralCsvWriter pluralCsvWriter;

    public Exporter(
            Path rootPath,
            LanguagesSupplier languagesSupplier,
            ResourcesPathsReader resourcesPathsReader,
            StringsXmlToCsvConverter stringsXmlToCsvConverter,
            PluralsXmlToCsvConverter pluralsXmlToCsvConverter,
            StringResourceCsvWriter stringResourceCsvWriter,
            PluralCsvWriter pluralCsvWriter) {

        this.rootPath = rootPath;
        this.languagesSupplier = languagesSupplier;
        this.resourcesPathsReader = resourcesPathsReader;
        this.stringsXmlToCsvConverter = stringsXmlToCsvConverter;
        this.pluralsXmlToCsvConverter = pluralsXmlToCsvConverter;
        this.stringResourceCsvWriter = stringResourceCsvWriter;
        this.pluralCsvWriter = pluralCsvWriter;
    }

    public void readResourcesAndExportToCsv() {

        Map<Language, List<Path>> languageToPaths = resourcesPathsReader.read(rootPath, languagesSupplier.get());

        List<ResourceString> defaultResourceStrings = getDefaultResourceStrings(languageToPaths);
        List<Plural> defaultLanguagePlurals = getDefaultLanguagePlurals(languageToPaths);

        for (Map.Entry<Language, List<Path>> e : languageToPaths.entrySet()) {

            if (e.getKey().equals(Language.DEFAULT)) {
                continue; // skip the default language
            }

            for (Path path : e.getValue()) {

                String lastPart = path.getName(path.getNameCount() - 1).toString().replace(".xml", ".csv");
                Path destinationPath = Paths.get(".", "out", "csv", getLanguageName(e.getKey()), lastPart);

                if (path.endsWith("strings.xml")) {
                    List<ResourceString> resourceStrings = stringsXmlToCsvConverter.convert(path);
                    stringResourceCsvWriter.export(destinationPath, defaultResourceStrings, resourceStrings);
                }
                else if (path.endsWith("plurals.xml")) {
                    List<Plural> plurals = pluralsXmlToCsvConverter.convert(path);
                    pluralCsvWriter.export(destinationPath, defaultLanguagePlurals, plurals);
                }
                else {
                    throw new RuntimeException("Unhandled path " + path.toString());
                }
            }
        }
    }

    private List<ResourceString> getDefaultResourceStrings(Map<Language, List<Path>> languageToPaths) {

        List<Path> defaultLanguagePaths = languageToPaths.get(Language.DEFAULT);

        Path path = defaultLanguagePaths
                .stream()
                .filter(p -> p.endsWith("strings.xml"))
                .findAny()
                .orElse(null);

        if (path == null) {
            return Collections.emptyList();
        }

        return stringsXmlToCsvConverter.convert(path);
    }

    private List<Plural> getDefaultLanguagePlurals(Map<Language, List<Path>> languageToPaths) {

        List<Path> defaultLanguagePaths = languageToPaths.get(Language.DEFAULT);

        Path path = defaultLanguagePaths
                .stream()
                .filter(p -> p.endsWith("plurals.xml"))
                .findAny()
                .orElse(null);

        if (path == null) {
            return Collections.emptyList();
        }

        return pluralsXmlToCsvConverter.convert(path);
    }

    private static String getLanguageName(Language language) {

        if (language == Language.DEFAULT) {
            return "United States";
        }
        else {
            return language.getName();
        }
    }
}
