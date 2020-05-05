import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.util.Map;
import java.util.Properties;

public class Runner {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        InputStream resourceStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("app.properties");
        try { properties.load(resourceStream);}
        catch (IOException e) { e.printStackTrace(); }

        RecordParsingStrategyFactory factory = new RecordParsingStrategyFactory();
        ParsingStrategy parsingStrategy = factory.getParsingStrategy(properties.getProperty("strategy"));

        Path csv = Paths.get((String) properties.get("csvFile"));
        Path monthlyExpenses = Paths.get((String) properties.get("monthlyExpensesFile"));

        ExpenseLoader loader = new ExpenseLoader(parsingStrategy, csv, monthlyExpenses);
        ExpenseWriter writer = new ExpenseWriter("test.txt");

        Map<Month, Map<Category, StringBuilder>> notesToAdd = loader.loadExpensesIntoFile();
        writer.writeExpensesToFile(notesToAdd);
    }

}
