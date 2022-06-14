package stringstocsv.model;

public final class PluralItem {

    public enum Quantity {

        ZERO("zero"),
        ONE("one"),
        TWO("two"),
        FEW("few"),
        MANY("many"),
        OTHER("other");

        public static Quantity parse(String str) {

            for (Quantity quantity : values()) {

                if (quantity.id.equals(str)) {
                    return quantity;
                }
            }

            throw new RuntimeException("String \"" + str + "\" does not correspond to any quantity.");
        }

        Quantity(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return id;
        }

        private final String id;
    }

    private final Quantity quantity;
    private final String value;

    public PluralItem(Quantity quantity, String value) {
        this.quantity = quantity;
        this.value = value;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public String getValue() {
        return value;
    }
}
