package stringstocsv.export.converter;

import stringstocsv.model.Plural;
import stringstocsv.model.PluralItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluralsXmlToCsvConverter {

    private static final Pattern PLURALS_PATTERN = Pattern.compile("\\s.*<plurals name=\"(\\w*)\">");

    private static final Pattern ITEM_PATTERN = Pattern.compile("\\s.*<item quantity=\"(\\w*)\">(.*?)</item>");

    public List<Plural> convert(Path path) {

        try {
            List<String> lines = Files.readAllLines(path);

            List<Plural> plurals = new ArrayList<>();
            List<PluralItem> pluralItems = new ArrayList<>();

            String currentPlural = null;

            for (String line : lines) {

                String newPlural = getPluralName(line);
                PluralItem item = getItem(line);

                if (newPlural != null) {

                    if (currentPlural != null) {
                        plurals.add(new Plural(currentPlural,pluralItems));
                        pluralItems = new ArrayList<>();
                    }

                    currentPlural = newPlural;
                }
                else if (item != null) {
                    pluralItems.add(item);
                }
            }

            if (currentPlural != null) {
                plurals.add(new Plural(currentPlural,pluralItems));
            }

            return plurals;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPluralName(String line) {

        Matcher matcher = PLURALS_PATTERN.matcher(line);

        String name = null;
        while (matcher.find()) {
            name = matcher.group(1);
        }

        return name;
    }

    private PluralItem getItem(String line) {

        Matcher matcher = ITEM_PATTERN.matcher(line);

        String quantity = null;
        String value = null;
        while (matcher.find()) {
            quantity = matcher.group(1);
            value = matcher.group(2);
        }

        if (quantity == null || value == null) {
            return null;
        }

        return new PluralItem(
                PluralItem.Quantity.parse(quantity),
                value);
    }
}
