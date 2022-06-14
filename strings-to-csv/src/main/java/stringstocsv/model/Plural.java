package stringstocsv.model;

import java.util.List;

public final class Plural {

    private final String name;
    private final List<PluralItem> pluralItems;

    public Plural(String name, List<PluralItem> pluralItems) {

        this.name = name;
        this.pluralItems = pluralItems;
    }

    public String getName() {
        return name;
    }

    public List<PluralItem> getPluralItems() {
        return pluralItems;
    }
}