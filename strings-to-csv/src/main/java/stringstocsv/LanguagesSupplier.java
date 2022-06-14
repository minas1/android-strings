package stringstocsv;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderHeaderAware;
import stringstocsv.model.Language;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LanguagesSupplier implements Supplier<List<Language>> {

    @Override
    public List<Language> get() {

        try (CSVReader reader = new CSVReaderHeaderAware(new InputStreamReader(getClass().getResourceAsStream("/language-codes.csv")))) {

            return reader.readAll().stream()
                    .map(columns -> new Language(columns[1], columns[0]))
                    .collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
