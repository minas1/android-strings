package stringstocsv.model;

import java.util.Objects;

public final class ResourceString {

    private final String name;
    private final String value;

    public ResourceString(String name, String value) {
        this.name = Objects.requireNonNull(name);
        this.value = Objects.requireNonNull(value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceString that = (ResourceString) o;
        return name.equals(that.name) &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "ResourceString{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
