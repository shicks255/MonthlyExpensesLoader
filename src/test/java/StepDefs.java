import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.nio.file.Path;
import java.time.Month;
import java.util.Map;

import static org.junit.Assert.*;

public class StepDefs {

    RecordParsingStrategyFactory factory = new RecordParsingStrategyFactory();
    ParsingStrategy parsingStrategy = factory.getParsingStrategy("chase");
    Path bankCsv = Path.of("testBankCsv.csv");
    Path monthlyExpenses = Path.of("testMonthly.csv");

    ExpenseLoader loader;
    ExpenseWriter writer;

    @Given("call loader with empty csv")
    public void call_loader_with_empty_csv() {
        try
        {
            loader = new ExpenseLoader(parsingStrategy, bankCsv, monthlyExpenses);
            writer = new ExpenseWriter("test.txt");
        } catch (Exception e)
        {
            assertTrue(false);
        }
    }

    @When("we load expenses")
    public void we_load_expenses() {
        try
        {
            Map<Month, Map<Category, StringBuilder>> comments = loader.loadExpensesIntoFile();
            writer.writeExpensesToFile(comments);
        } catch (Exception e)
        {
            assertTrue(false);
        }
    }

    @Then("result should be empty")
    public void result_should_be_empty() {
        Path file = Path.of("text.txt");
        System.out.println(file);
    }
}
