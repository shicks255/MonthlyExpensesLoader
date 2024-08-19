package models;

import java.util.stream.Stream;

public enum Category {
    INCOME(1),
    PAY(2),
    FOOD(3),
    GAS(4),
    RENT(6),
    VERIZON(7),
    ELECTRIC(8),
    WATER(9),
    TRASH(10),
    SEWAGE(11),
    GASS(12),
    CONVIENENT_STORE(13),
    HOUSE(14),
    OTHER(15),
    SKIP(0);

    Category(int col) {
        this.col = col;
    }
    int col;

    public static Category getCategoryFromCol(int column) {
        return Stream.of(values())
                .filter(x -> x.col == column)
                .findFirst()
                .get();
    }

    public static int getColFromCategory(Category category) {
        return Stream.of(values())
                .filter(x -> x != category)
                .map(x -> x.col)
                .findFirst()
                .get();
    }

    public static String asString(Category category) {
        return category.col + " - " + category;
    }
}
