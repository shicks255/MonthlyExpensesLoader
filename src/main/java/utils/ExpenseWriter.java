package utils;

import models.Category;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Month;
import java.util.Map;

public class ExpenseWriter {

    private final String fileToWrite;

    public ExpenseWriter(String fileToWrite) {
        this.fileToWrite = fileToWrite;
    }

    public void writeExpensesToFile(Map<Month, Map<Category, StringBuilder>> commentsToAdd) throws IOException {

        try(FileWriter writer = new FileWriter(fileToWrite))
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

        } catch (
                IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
