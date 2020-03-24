import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Runner {

    enum Category {
        INCOME,PAY,FOOD,GAS,RENT,CONVIENT_STORE,CLOTHES,OTHER
    }

    public static Map<Category, Integer> catToCellMap = new HashMap() {{
        put(Category.INCOME, 1);
        put(Category.PAY, 2);
        put(Category.FOOD, 3);
        put(Category.GAS, 4);
        put(Category.RENT, 5);
        put(Category.CONVIENT_STORE, 7);
        put(Category.CLOTHES, 8);
        put(Category.OTHER, 9);
    }};

    private static int getMonthRow(Month month) {
        return 01;
    }

    public static void main(String[] args) {

        Path file = Paths.get("c:", "IdeaProjects", "MonthlyExpenses", "src", "main", "java", "test.csv");

        List<String> lines = new ArrayList<>();

        try (CSVParser parser = new CSVParser(Files.newBufferedReader(file), CSVFormat.DEFAULT.withFirstRecordAsHeader()))
        {
            for (CSVRecord record : parser) {
                System.out.println(record);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        Path monthlyExpenses = Paths.get(
                "c:", "Users", "shick", "dropbox", "tracking", "Monthly Expenses 2020.xlsx"
        );

        try {
            Workbook workbook = new XSSFWorkbook(monthlyExpenses.toFile());
            Sheet sheet = workbook.getSheetAt(0);


        } catch (Exception e) {

        }



    }


}
