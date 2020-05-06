package utils;

import helpers.parsing.ParsingStrategy;
import helpers.userInteraction.UserInteractor;
import models.Category;
import models.Item;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class ExpenseLoader {

    private ParsingStrategy m_parsingStrategy;
    private UserInteractor m_userInteractor;
    private Path bankCsv;
    private Path expenseCsv;

    public ExpenseLoader(ParsingStrategy parsingStrategy, Path csvPath, Path expsensePath, UserInteractor userInteractor) {
        this.m_parsingStrategy = parsingStrategy;
        this.m_userInteractor = userInteractor;
        this.bankCsv = csvPath;
        this.expenseCsv = expsensePath;
    }

    public Map<Month, Map<Category,StringBuilder>> loadExpensesIntoFile() throws IOException {
        Map<Month, Map<Category, StringBuilder>> commentsToAdd = new HashMap<>();

        try (CSVParser parser = new CSVParser(Files.newBufferedReader(bankCsv), CSVFormat.DEFAULT.withFirstRecordAsHeader());
             XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(expenseCsv.toFile())))
        {
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (CSVRecord record : parser) {
                Item item = m_parsingStrategy.parse(record);
                int rowNumber = item.getTransactionDate().getMonthValue();
                XSSFRow row = sheet.getRow(rowNumber);

                int userCategoryNumber = m_userInteractor.promptUserForCategory(item);
                Category cat = Category.getCategoryFromCol(userCategoryNumber);
                item.setCategory(cat);
                item.setMemo(m_userInteractor.promptUserForMemo());
                addToMemo(commentsToAdd, item);

                String formula = getFormula(row, userCategoryNumber, item);
                row.getCell(userCategoryNumber).setCellFormula(formula);

                XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            }

            XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
            workbook.write(new FileOutputStream(expenseCsv.toFile()));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return commentsToAdd;
    }

    private void addToMemo(Map<Month, Map<Category, StringBuilder>> commentsToAdd, Item item) {
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

    private String getFormula(Row row, int userCategoryNumber, Item item) {
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
