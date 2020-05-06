package models;

import java.util.stream.Stream;

public enum Category {
    INCOME(1),
    PAY(2),
    FOOD(3),
    GAS(4),
    RENT(5),
    CONVIENENT_STORE(7),
    CLOTHES(8),
    OTHER(9);

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
