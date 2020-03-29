import java.time.LocalDate;

public class Item {

    private LocalDate transactionDate;
    private String description = "";
    private String type = "";
    private String amount = "";
    private Runner.Category m_category;
    private String memo;


    public String getUserPrompt() {
        return "Choose a category for " + description + " " + transactionDate + " " + amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Runner.Category getCategory() {
        return m_category;
    }

    public void setCategory(Runner.Category category) {
        m_category = category;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
