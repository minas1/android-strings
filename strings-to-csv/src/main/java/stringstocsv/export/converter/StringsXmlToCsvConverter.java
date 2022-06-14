package stringstocsv.export.converter;

import stringstocsv.model.ResourceString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringsXmlToCsvConverter {

    private static final Pattern RESOURCE_STRING_PATTERN = Pattern.compile(".*?<string name=\"(.*)\">(.*)</string>");

    public List<ResourceString> convert(Path path) {

        try {
            List<String> lines = Files.readAllLines(path);

            return lines
                    .stream()
                    .filter(line -> !line.contains("translatable=\"false\""))
                    .map(this::convert)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<ResourceString> convert(String line) {

        Matcher matcher = RESOURCE_STRING_PATTERN.matcher(line);

        String name = null;
        String value = null;
        while (matcher.find()) {
            name = matcher.group(1);
            value = matcher.group(2);
        }

        if (name == null || value == null) {
            return Optional.empty();
        }

        return Optional.of(new ResourceString(name, value));
    }
}
