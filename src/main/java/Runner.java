import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Stream;

public class Runner {

    enum Category {
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

    public static void main(String[] args) {
        Properties properties = new Properties();
        InputStream resourceStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("app.properties");
        try { properties.load(resourceStream);}
        catch (IOException e) { e.printStackTrace(); }

        RecordParsingStrategyFactory factory = new RecordParsingStrategyFactory();
        ParsingStrategy parsingStrategy = factory.getParsingStrategy(properties.getProperty("strategy"));

        Path csv = Paths.get((String) properties.get("csvFile"));
        Path monthlyExpenses = Paths.get((String) properties.get("monthlyExpensesFile"));

        loadExpensesIntoFile(parsingStrategy, csv, monthlyExpenses);
    }

    public static void loadExpensesIntoFile(ParsingStrategy parsingBehavior, Path csv, Path expenses) {
        Map<Month, Map<Category, StringBuilder>> commentsToAdd = new HashMap<>();
        Scanner in = new Scanner(System.in);

        try (CSVParser parser = new CSVParser(Files.newBufferedReader(csv), CSVFormat.DEFAULT.withFirstRecordAsHeader());
             XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(expenses.toFile())))
        {
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (CSVRecord record : parser) {
                Item item = parsingBehavior.parse(record);
                int rowNumber = item.getTransactionDate().getMonthValue();
                XSSFRow row = sheet.getRow(rowNumber);

                int userCategoryNumber = promptUserForCategory(item, in);
                Category cat = Category.getCategoryFromCol(userCategoryNumber);
                item.setCategory(cat);
                item.setMemo(promptUserForMemo(in));
                addToMemo(commentsToAdd, item);

                String formula = getFormula(row, userCategoryNumber, item);
                row.getCell(userCategoryNumber).setCellFormula(formula);

                XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
                System.out.println(row.getCell(userCategoryNumber).getCellFormula());
            }

            XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            workbook.write(new FileOutputStream(expenses.toFile()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try(FileWriter writer = new FileWriter("test.txt"))
        {
            for (Map.Entry<Month, Map<Category, StringBuilder>> monthAndValues : commentsToAdd.entrySet())
            {
                writer.append(monthAndValues.getKey().toString());
                writer.append("\r\n");

                for (Map.Entry<Category, StringBuilder> catAndMemo : monthAndValues.getValue().entrySet())
                {
                    writer.append(Category.asString(catAndMemo.getKey()));
                    writer.append("\r\n");
                    writer.append(String.valueOf(catAndMemo.getValue()));
                    writer.append("\r\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addToMemo(Map<Month, Map<Category, StringBuilder>> commentsToAdd, Item item) {
        LocalDate transactionDate = item.getTransactionDate();

        if (item.getMemo().length() > 0) {
            String memoNote = String.format("%s %s %d/%d\r\n", item.getMemo(),
                    item.getAmount(), transactionDate.getMonthValue(),
                    transactionDate.getDayOfMonth());

            commentsToAdd.merge(transactionDate.getMonth(), new HashMap<>(Map.of(item.getCategory(), new StringBuilder(memoNote))), (m1,m2) -> {
                m1.merge(item.getCategory(), new StringBuilder(memoNote), (s1, s2) -> {
                    s1.append(memoNote);
                    return s1;
                });
                return m1;
            });
        }
    }

    private static int promptUserForCategory(Item item, Scanner in) {
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

    private static String promptUserForMemo(Scanner in) {
        System.out.println("What's the memo note? Type n for no memo");
        in.nextLine();
        String commentDescription = in.nextLine();
        if (commentDescription.equalsIgnoreCase("n"))
            return "";
        return commentDescription;
    }

    private static String getFormula(Row row, int userCategoryNumber, Item item) {
        String currentFormula = "";
        boolean newFormula = true;
        if (row.getCell(userCategoryNumber).getNumericCellValue() != 0) {
            currentFormula = row.getCell(userCategoryNumber).getCellFormula();
            newFormula = false;
        }
        currentFormula += (!newFormula ? " " : "") + item.getAmount();
        return currentFormula;
    }
}
