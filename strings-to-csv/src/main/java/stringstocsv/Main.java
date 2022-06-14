package stringstocsv;

import com.beust.jcommander.JCommander;
import stringstocsv.export.Exporter;
import stringstocsv.export.ResourcesPathsReader;
import stringstocsv.export.converter.PluralsXmlToCsvConverter;
import stringstocsv.export.converter.StringsXmlToCsvConverter;
import stringstocsv.export.writer.PluralCsvWriter;
import stringstocsv.export.writer.StringResourceCsvWriter;

import java.io.File;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        Args arguments = new Args();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        Path rootPath = new File(arguments.rootDirectory).toPath();

        new Exporter(
                rootPath,
                new LanguagesSupplier(),
                new ResourcesPathsReader(),
                new StringsXmlToCsvConverter(),
                new PluralsXmlToCsvConverter(),
                new StringResourceCsvWriter(),
                new PluralCsvWriter()).readResourcesAndExportToCsv();
    }
}
