import org.apache.commons.csv.CSVRecord;

@FunctionalInterface
public interface ParsingStrategy {
    Item parse(CSVRecord record);
}