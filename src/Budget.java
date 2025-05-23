import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

// Aylık bütçe bilgilerini tutan sınıf
public class Budget extends BaseModel {

    private YearMonth month;
    private double totalIncome;
    private double totalExpense;
    private Map<String, Double> expenseDistributionByCategory;

    // Belirli bir ay için bütçe oluşturulur
    public Budget(YearMonth month) {
        this.month = month;
        this.totalIncome = 0.0;
        this.totalExpense = 0.0;
        this.expenseDistributionByCategory = new HashMap<>();
    }

    // Getter ve setter metodları
    public YearMonth getMonth() { return month; }
    public void setMonth(YearMonth month) { this.month = month; }

    public double getTotalIncome() { return totalIncome; }
    public double getTotalExpense() { return totalExpense; }

    public Map<String, Double> getExpenseDistributionByCategory() { return expenseDistributionByCategory; }

    // Gelir ekler
    public void addIncome(double amount) {
        if (amount > 0) totalIncome += amount;
    }

    // Gider ekler ve kategoriye göre dağılımı günceller
    public void addExpense(String category, double amount) {
        if (amount > 0) {
            totalExpense += amount;
            expenseDistributionByCategory.put(category,
                    expenseDistributionByCategory.getOrDefault(category, 0.0) + amount);
        }
    }

    // Kalan bakiye
    public double getRemainingBalance() {
        return totalIncome - totalExpense;
    }

    // Belirli bir kategorinin toplam gider içindeki yüzdesi
    public double getExpensePercentage(String category) {
        if (!expenseDistributionByCategory.containsKey(category) || totalExpense == 0) return 0.0;
        return (expenseDistributionByCategory.get(category) / totalExpense) * 100;
    }

    // Nesneyi yazdırmak için özet bilgi
    public String toString() {
        return "Bütçe: " + month + " | Gelir: " + totalIncome + "₺ | Gider: " + totalExpense + "₺ | Kalan: " + getRemainingBalance() + "₺";
    }
}
