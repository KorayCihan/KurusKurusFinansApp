import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

// Kullanıcının finansal işlemlerini yöneten sınıf
public class FinanceManager {

    private User user;

    // Kullanıcı ile başlatılır
    public FinanceManager(User user) {
        this.user = user;
    }

    // Yeni işlem ekler
    public void addTransaction(Transaction transaction) {
        user.addTransaction(transaction);
    }

    // Yeni fatura ekler
    public void addBill(Bill bill) {
        user.addBill(bill);
    }

    // Yeni hedef ekler
    public void addGoal(FinancialGoal goal) {
        user.addGoal(goal);
    }

    // Tüm işlemleri getir
    public List<Transaction> getAllTransactions() {
        return user.getTransactions();
    }

    // Aylık toplam gelir hesapla
    public double getMonthlyIncome(YearMonth month) {
        return user.getTransactions().stream()
                .filter(t -> t.getType().equalsIgnoreCase("Gelir") &&
                        YearMonth.from(t.getDate()).equals(month))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Aylık toplam gider hesapla
    public double getMonthlyExpense(YearMonth month) {
        return user.getTransactions().stream()
                .filter(t -> t.getType().equalsIgnoreCase("Gider") &&
                        YearMonth.from(t.getDate()).equals(month))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Harcama kategorilerine göre gider dağılımı
    public Map<String, Double> getExpenseByCategory(YearMonth month) {
        Map<String, Double> data = new HashMap<>();
        for (Transaction t : user.getTransactions()) {
            if (t.getType().equalsIgnoreCase("Gider") && YearMonth.from(t.getDate()).equals(month)) {
                data.merge(t.getCategory(), t.getAmount(), Double::sum);
            }
        }
        return data;
    }

    // Ödenmemiş faturaları getir
    public List<Bill> getUnpaidBills() {
        List<Bill> unpaid = new ArrayList<>();
        for (Bill b : user.getBills()) {
            if (!b.isPaid()) {
                unpaid.add(b);
            }
        }
        return unpaid;
    }

    // Yaklaşan faturaları getir (belirli gün içinde)
    public List<Bill> getUpcomingBills(int daysAhead) {
        LocalDate now = LocalDate.now();
        LocalDate limit = now.plusDays(daysAhead);
        List<Bill> upcoming = new ArrayList<>();

        for (Bill b : user.getBills()) {
            if (!b.isPaid() && !b.getDueDate().isBefore(now) && !b.getDueDate().isAfter(limit)) {
                upcoming.add(b);
            }
        }

        return upcoming;
    }

    // Tüm hedeflerin özetlerini döner
    public List<String> getGoalProgressSummaries() {
        List<String> reports = new ArrayList<>();
        for (FinancialGoal goal : user.getGoals()) {
            reports.add(goal.toString());
        }
        return reports;
    }

    // Belirli ay için bütçe hesapla
    public Budget generateMonthlyBudget(YearMonth month) {
        Budget budget = new Budget(month);

        for (Transaction t : user.getTransactions()) {
            if (YearMonth.from(t.getDate()).equals(month)) {
                if (t.getType().equalsIgnoreCase("Gelir")) {
                    budget.addIncome(t.getAmount());
                } else {
                    budget.addExpense(t.getCategory(), t.getAmount());
                }
            }
        }

        return budget;
    }

    // En çok harcama yapılan kategori
    public String getMostSpentCategory(List<YearMonth> months) {
        Map<String, Double> totals = new HashMap<>();
        for (Transaction t : user.getTransactions()) {
            if (t.getType().equalsIgnoreCase("Gider") &&
                    months.contains(YearMonth.from(t.getDate()))) {
                String category = t.getCategory() == null || t.getCategory().isBlank() ? "Bilinmeyen" : t.getCategory();
                totals.put(category, totals.getOrDefault(category, 0.0) + Math.abs(t.getAmount()));
            }
        }
        return totals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Veri yok");
    }

    // En yüksek gelirli ay
    public String getHighestIncomeMonth(List<YearMonth> months) {
        YearMonth topMonth = null;
        double maxIncome = -1;

        for (YearMonth ym : months) {
            double income = getMonthlyIncome(ym);
            if (income > maxIncome) {
                maxIncome = income;
                topMonth = ym;
            }
        }

        return topMonth != null ? topMonth.getMonth().toString() + " " + topMonth.getYear() : "Veri yok";
    }

    // Tüm faturaları getir
    public List<Bill> getAllBills() {
        return user.getBills();
    }

    // Tüm hedefleri getir
    public List<FinancialGoal> getAllGoals() {
        return user.getGoals();
    }
}
