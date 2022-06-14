package stringstocsv;

import org.apache.commons.lang3.tuple.Pair;
import stringstocsv.model.Language;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LanguagesSupplier implements Supplier<List<Language>> {

    @Override
    public List<Language> get() {
        return Arrays.stream(Locale.getAvailableLocales())
                .filter(locale -> !locale.getLanguage().isEmpty())
                .map(locale -> Pair.of(locale.getDisplayLanguage(), locale.getLanguage()))
                .distinct()
                .map(pair -> new Language(pair.getLeft(), pair.getRight()))
                .collect(Collectors.toList());
    }
}
