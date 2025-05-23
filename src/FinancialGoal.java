import javafx.beans.property.*;
import java.time.LocalDate;

// Finansal hedefleri temsil eden sınıf
public class FinancialGoal extends BaseModel {

    private final StringProperty goalName;
    private final DoubleProperty targetAmount;
    private final DoubleProperty currentAmount;
    private final ObjectProperty<LocalDate> targetDate;
    private final BooleanProperty completed;

    // Hedef oluşturulurken tüm bilgiler girilir
    public FinancialGoal(String goalName, double targetAmount, double currentAmount, LocalDate targetDate) {
        this.goalName = new SimpleStringProperty(goalName);
        this.targetAmount = new SimpleDoubleProperty(targetAmount);
        this.currentAmount = new SimpleDoubleProperty(currentAmount);
        this.targetDate = new SimpleObjectProperty<>(targetDate);
        this.completed = new SimpleBooleanProperty();
        updateCompletionStatus();
    }

    // Hedef tamamlandı mı kontrol eder
    public void updateCompletionStatus() {
        this.completed.set(getCurrentAmount() >= getTargetAmount());
    }

    // JavaFX binding metodları
    public BooleanProperty completedProperty() { return completed; }
    public StringProperty goalNameProperty() { return goalName; }
    public DoubleProperty targetAmountProperty() { return targetAmount; }
    public DoubleProperty currentAmountProperty() { return currentAmount; }
    public ObjectProperty<LocalDate> targetDateProperty() { return targetDate; }

    // Getter/setter metodları
    public String getGoalName() { return goalName.get(); }
    public void setGoalName(String goalName) { this.goalName.set(goalName); }

    public double getTargetAmount() { return targetAmount.get(); }
    public void setTargetAmount(double targetAmount) {
        this.targetAmount.set(targetAmount);
        updateCompletionStatus();
    }

    public double getCurrentAmount() { return currentAmount.get(); }
    public void setCurrentAmount(double currentAmount) {
        this.currentAmount.set(currentAmount);
        updateCompletionStatus();
    }

    public LocalDate getTargetDate() { return targetDate.get(); }
    public void setTargetDate(LocalDate targetDate) { this.targetDate.set(targetDate); }

    // Hedefin ne kadarının tamamlandığını oran olarak verir (maksimum 1.0)
    public double getProgress() {
        return Math.min(currentAmount.get() / targetAmount.get(), 1.0);
    }

    // Hedef tamamlandı mı
    public boolean isCompleted() {
        return completed.get();
    }

    // Nesneyi string olarak özetler
    public String toString() {
        return getGoalName() + " → " + getCurrentAmount() + " / " + getTargetAmount() + "₺ (%" +
                String.format("%.0f", getProgress() * 100) + ") - Hedef Tarihi: " + getTargetDate();
    }
}
