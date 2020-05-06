import helpers.userInteraction.UserInteractor;
import models.Item;

import java.util.Random;

public class MockUserInteractor implements UserInteractor {

    private final Random random = new Random();

    @Override
    public int promptUserForCategory(Item item) {
        int number = random.nextInt(8) + 1;
        return number == 6 ? promptUserForCategory(item) : number;
    }

    @Override
    public String promptUserForMemo() {

        if (random.nextInt(10) > 6)
            return "This is my note";

        return "n";
    }
}