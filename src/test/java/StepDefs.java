import helpers.parsing.ParsingStrategy;
import helpers.parsing.RecordParsingStrategyFactory;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import models.Category;
import utils.ExpenseLoader;
import utils.ExpenseWriter;

import java.io.File;
import java.nio.file.Path;
import java.time.Month;
import java.util.Map;

import static org.junit.Assert.*;

public class StepDefs {

    RecordParsingStrategyFactory factory = new RecordParsingStrategyFactory();
    ParsingStrategy parsingStrategy = factory.getParsingStrategy("chase");
    MockUserInteractor userInteractor = new MockUserInteractor();
    Path bankCsv = Path.of("src" + File.separator + "test" + File.separator + "java" + File.separator + "testBankCsv.csv");
    Path emptyBankCsv = Path.of("src" + File.separator + "" + File.separator + "test" + File.separator + "" + File.separator + "java" + File.separator + "" + File.separator + "testBankCsvEmpty.csv");
    Path monthlyExpenses = Path.of("C:" + File.separator + "" + File.separator + "Users" + File.separator + "" + File.separator + "shick" + File.separator + "" + File.separator + "dropbox" + File.separator + "" + File.separator + "tracking" + File.separator + "" + File.separator + "MonthlyExpenses2020test.xlsx");
    Map<Month, Map<Category, StringBuilder>> comments;

    ExpenseLoader loader;
    ExpenseWriter writer;

    @Given("call loader with empty csv")
    public void call_loader_with_empty_csv() {
        try
        {
            loader = new ExpenseLoader(parsingStrategy, emptyBankCsv, monthlyExpenses, userInteractor);
            writer = new ExpenseWriter("test.txt");
        } catch (Exception e)
        {
            assertTrue(false);
        }
    }

    @Given("call loader with data csv")
    public void call_loader_with_data_csv() {
        try
        {
            loader = new ExpenseLoader(parsingStrategy, bankCsv, monthlyExpenses, userInteractor);
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
            comments = loader.loadExpensesIntoFile();
            writer.writeExpensesToFile(comments);
        } catch (Exception e)
        {
            assertTrue(false);
        }
    }

    @Then("result should be empty")
    public void result_should_be_empty() {
        Path file = Path.of("text.txt");
        assertTrue(comments.isEmpty());
    }

    @Then("result should not be empty")
    public void result_should_not_be_empty() {
        assertFalse(comments.isEmpty());
    }
}
