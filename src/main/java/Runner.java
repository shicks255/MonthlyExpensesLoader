import helpers.userInteraction.ConsoleUserInteractor;
import helpers.parsing.ParsingStrategy;
import helpers.parsing.RecordParsingStrategyFactory;
import helpers.userInteraction.UserInteractor;
import models.Category;
import utils.ExpenseLoader;
import utils.ExpenseWriter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.util.Map;
import java.util.Properties;

public class Runner {

    public static void main(String[] args) throws Exception {
        Properties properties = extractProps();

        RecordParsingStrategyFactory factory = new RecordParsingStrategyFactory();
        ParsingStrategy parsingStrategy = factory.getParsingStrategy(properties.getProperty("strategy"));
        UserInteractor userInteractor = new ConsoleUserInteractor();

        Path csv = Paths.get((String) properties.get("csvFile"));
        Path monthlyExpenses = Paths.get((String) properties.get("monthlyExpensesFile"));

        ExpenseLoader loader = new ExpenseLoader(parsingStrategy, csv, monthlyExpenses, userInteractor);
        Map<Month, Map<Category, StringBuilder>> notesToAdd = loader.loadExpensesIntoFile();

        ExpenseWriter writer = new ExpenseWriter("test.txt");
        writer.writeExpensesToFile(notesToAdd);

        System.out.println(csv.toString() + " has been loaded with expenses.");
    }

    private static Properties extractProps() {
        Properties properties = new Properties();
        InputStream resourceStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("app.properties");
        try { properties.load(resourceStream);}
        catch (IOException e) { e.printStackTrace(); }

        return properties;
    }
}
