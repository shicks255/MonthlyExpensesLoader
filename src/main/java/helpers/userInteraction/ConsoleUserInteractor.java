package helpers.userInteraction;

import models.Category;
import models.Item;

import java.util.Scanner;
import java.util.stream.Stream;

public class ConsoleUserInteractor implements UserInteractor {

    Scanner in = new Scanner(System.in);

    @Override
    public int promptUserForCategory(Item item) {
        boolean keepGoing = true;
        Integer x = null;

        do {
            System.out.println(item.getUserPrompt() + " or 'q' to quit");
            Stream.of(Category.values())
                    .map(Category::asString)
                    .forEach(System.out::println);

            String input = in.next();

            if (input.equalsIgnoreCase("q"))
                System.exit(0);

            try {
                x = Integer.parseInt(input);
                keepGoing = false;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number for the category");
            }

        } while (keepGoing);

        return x;
    }

    @Override
    public String promptUserForMemo() {
        System.out.println("What's the memo note? Type n for no memo");
        in.nextLine();
        String commentDescription = in.nextLine();
        if (commentDescription.equalsIgnoreCase("n"))
            return "";
        return commentDescription;
    }
}