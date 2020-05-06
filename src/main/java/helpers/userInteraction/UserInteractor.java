package helpers.userInteraction;

import models.Item;

public interface UserInteractor {

    int promptUserForCategory(Item item);
    String promptUserForMemo();
}