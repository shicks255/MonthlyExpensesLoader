import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class Runner {

    enum Category {
        INCOME(1),
        PAY(2),
        FOOD(3),
        GAS(4),
        RENT(5),
        CONVIENT_STORE(7),
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

        Path file = Paths.get("c:", "IdeaProjects", "MonthlyExpenses", "src", "main", "java", "test.csv");
        Path monthlyExpenses = Paths.get(
                "c:", "Users", "shick", "dropbox", "tracking", "Monthly Expenses 2020_test.xlsx"
        );

        Map<Month, Map<Category, StringBuilder>> commentsToAdd = new HashMap<>();

        try (
                CSVParser parser = new CSVParser(Files.newBufferedReader(file), CSVFormat.DEFAULT.withFirstRecordAsHeader());
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(monthlyExpenses.toFile()));
        ) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            XSSFSheet sheet = workbook.getSheetAt(0);

            Scanner in = new Scanner(System.in);

            for (CSVRecord record : parser) {
                System.out.println(record);

                LocalDate transactionDate = LocalDate.parse(record.get(0), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                String description = record.get(2);
                String getType = record.get(4);
                String getAmount = record.get(5);

                int rowNumber = transactionDate.getMonthValue();

                XSSFRow row = sheet.getRow(rowNumber);

                System.out.println("Choose a category for " + description + " " + transactionDate + " " + getAmount);
                Stream.of(Category.values())
                        .map(Category::asString)
                        .forEach(System.out::println);
                String userValue = in.next();
                try {
                    int col = Integer.parseInt(userValue);
                    Category cat = Category.getCategoryFromCol(col);
                    System.out.println("What's the memo note?");
                    String commentDescription = in.next();

                    Map<Category, StringBuilder> t = commentsToAdd.getOrDefault(transactionDate.getMonth(), new HashMap<>());
                    StringBuilder s = t.getOrDefault(cat, new StringBuilder(""));
                    s.append(commentDescription + " " + getAmount + " " + transactionDate.getMonthValue() + "/" + transactionDate.getDayOfMonth() + "\r\n");
                    t.put(cat, s);
                    commentsToAdd.put(transactionDate.getMonth(), t);

                    String currentFormula = "SUM()";
                    boolean newFormula = true;
                    if (row.getCell(col).getNumericCellValue() != 0) {
                           newFormula = false;
                        currentFormula = row.getCell(col).getCellFormula();
                    }
                    currentFormula =
                            currentFormula.substring(0,currentFormula.lastIndexOf(")")) + (newFormula ? "" : ",") + getAmount + ")";
                    row.getCell(col).setCellFormula(currentFormula);
                    evaluator.setDebugEvaluationOutputForNextEval(true);
                    evaluator.evaluateFormulaCell(row.getCell(col));
                } catch (NumberFormatException e) {
                    System.exit(0);
                }
            }

            evaluator.evaluateAll();
            workbook.write(new FileOutputStream(monthlyExpenses.toFile()));
        } catch (Exception e) {
            System.out.println(e);
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

        }
    }
}
