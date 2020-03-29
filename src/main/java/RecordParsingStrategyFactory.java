import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RecordParsingStrategyFactory {

    public ParsingStrategy getParsingStrategy(String fileOrigin) {
        return switch (fileOrigin) {
            case "chase" -> getChaseParser();
            default -> getChaseParser();
        };
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

}
