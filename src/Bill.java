import javafx.beans.property.*;
import java.time.LocalDate;

// Fatura model sınıfı
public class Bill extends BaseModel {

    private final StringProperty billName;
    private final DoubleProperty amount;
    private final ObjectProperty<LocalDate> dueDate;
    private final BooleanProperty isPaid;

    // Ödeme durumu belirtilmeden oluşturulan fatura varsayılan ödenmedi olarak atanıyor
    public Bill(String billName, double amount, LocalDate dueDate) {
        this(billName, amount, dueDate, false);
    }

    // Tüm alanları belirterek fatura oluşturma
    public Bill(String billName, double amount, LocalDate dueDate, boolean isPaid) {
        this.billName = new SimpleStringProperty(billName);
        this.amount = new SimpleDoubleProperty(amount);
        this.dueDate = new SimpleObjectProperty<>(dueDate);
        this.isPaid = new SimpleBooleanProperty(isPaid);
    }

    // JavaFX ile binding için property metodlarımız
    public StringProperty billNameProperty() { return billName; }
    public DoubleProperty amountProperty() { return amount; }
    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }
    public BooleanProperty isPaidProperty() { return isPaid; }

    // Getter ve setter metodlarımız
    public String getBillName() { return billName.get(); }
    public void setBillName(String billName) { this.billName.set(billName); }

    public double getAmount() { return amount.get(); }
    public void setAmount(double amount) { this.amount.set(amount); }

    public LocalDate getDueDate() { return dueDate.get(); }
    public void setDueDate(LocalDate dueDate) { this.dueDate.set(dueDate); }

    public boolean isPaid() { return isPaid.get(); }
    public void setPaid(boolean paid) { this.isPaid.set(paid); }

    // Fatura ödendi olarak işaretleme mantıksal operatörümüz
    public void payed() { this.isPaid.set(true); }

    // Faturayı metin olarak özetleme
    public String toString() {
        return getBillName() + " - " + getAmount() + "₺ - Son tarih: " + getDueDate() + " - " + (isPaid() ? "Ödendi" : "Bekliyor");
    }
}
