package stringstocsv.model;

import java.util.Objects;

public final class Language {

    public static final Language DEFAULT = new Language("", "");

    private final String name;
    private final String code;

    public Language(String name, String code) {
        this.name = Objects.requireNonNull(name);
        this.code = Objects.requireNonNull(code);
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return name.equals(language.name) &&
                code.equals(language.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code);
    }

    @Override
    public String toString() {
        return "Language{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
