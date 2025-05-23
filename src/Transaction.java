

import javafx.beans.property.*;
import java.time.LocalDate;

// Gelir veya gider işlemini temsil eden sınıf
public class Transaction extends BaseModel {

    // İşlem türü
    private final StringProperty type;
    // Kategori
    private final StringProperty category;
    // Tutar
    private final DoubleProperty amount;
    // Tarih
    private final ObjectProperty<LocalDate> date;
    // Açıklama
    private final StringProperty description;

    // Tam parametreli kurucu
    public Transaction(int id, String type, String category, double amount, LocalDate date, String description) {
        this.id = id;
        this.type = new SimpleStringProperty(type);
        this.category = new SimpleStringProperty(category);
        this.amount = new SimpleDoubleProperty(amount);
        this.date = new SimpleObjectProperty<>(date);
        this.description = new SimpleStringProperty(description);
    }

    // id'siz kurucu
    public Transaction(String type, String category, double amount, LocalDate date, String description) {
        this(0, type, category, amount, date, description);
    }

    // JavaFX bağlama metodları
    public StringProperty typeProperty() { return type; }
    public StringProperty categoryProperty() { return category; }
    public DoubleProperty amountProperty() { return amount; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty descriptionProperty() { return description; }

    // Getter-setter metodları
    public String getType() { return type.get(); }
    public void setType(String value) { type.set(value); }

    public String getCategory() { return category.get(); }
    public void setCategory(String value) { category.set(value); }

    public double getAmount() { return amount.get(); }
    public void setAmount(double value) { amount.set(value); }

    public LocalDate getDate() { return date.get(); }
    public void setDate(LocalDate value) { date.set(value); }

    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }

    // Listeleme ve loglama için string temsili
    public String toString() {
        return "[" + getType() + "] " + getCategory() + ": " + getAmount() + "₺ (" + getDate() + ") - " + getDescription();
    }
}
