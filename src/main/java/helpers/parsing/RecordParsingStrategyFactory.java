package helpers.parsing;

import models.Item;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RecordParsingStrategyFactory {

    public ParsingStrategy getParsingStrategy(String fileOrigin) {
        ParsingStrategy toReturn = null;

        switch (fileOrigin) {
            case ("chase"):
                toReturn = getChaseParser();
                break;
            case ("affinity"):
                toReturn = getAffinityParser();
                break;
        }

        return toReturn;
    }

    private ParsingStrategy getChaseParser() {
        return (record -> {
            Item item = new Item();

            LocalDate transactionDate = LocalDate.parse(record.get(0), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String description = record.get(2);
            String type = record.get(4);
            String amount = record.get(5);

            item.setTransactionDate(transactionDate);
            item.setDescription(description);
            item.setType(type);
            item.setAmount(amount);

            return item;
        });
    }

    private ParsingStrategy getAffinityParser() {
        return (record -> {
            Item item = new Item();

            LocalDate transactionDate = LocalDate.parse(record.get(1), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String description = record.get(2);
            String type = record.get(3);
            String amount = record.get(4);

            // if no amount, in affinity, possible pay
            if (amount.equals("")) {
                amount = record.get(5);
            }

            item.setTransactionDate(transactionDate);
            item.setDescription(description);
            item.setType(type);
            item.setAmount(amount);

            return item;
        });
    }

}
