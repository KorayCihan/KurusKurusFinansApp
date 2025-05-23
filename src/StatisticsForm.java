import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.time.YearMonth;
import java.util.*;

// İstatistiksel grafikler ve özet bilgileri oluşturan sınıf
public class StatisticsForm {

    // Son 6 ay için gelir-gider çubuğu grafiği
    public static VBox getIncomeExpenseChart(IUser currentUser) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        Label title = new Label("💰 Son 6 Ay Gelir-Gider Dağılımı");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(true);

        XYChart.Series<String, Number> gelirSeries = new XYChart.Series<>();
        gelirSeries.setName("Gelir");
        XYChart.Series<String, Number> giderSeries = new XYChart.Series<>();
        giderSeries.setName("Gider");

        FinanceManager fm = new FinanceManager((User) currentUser);
        for (YearMonth ym : getLastSixMonths()) {
            String label = ym.getMonth().toString().substring(0, 3) + " " + ym.getYear();
            gelirSeries.getData().add(new XYChart.Data<>(label, fm.getMonthlyIncome(ym)));
            giderSeries.getData().add(new XYChart.Data<>(label, fm.getMonthlyExpense(ym)));
        }

        chart.getData().addAll(gelirSeries, giderSeries);
        box.getChildren().addAll(title, chart);
        return box;
    }

    // Kategori bazlı gider dağılımı
    public static VBox getCategoryPieChart(IUser currentUser) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        Label title = new Label("📂 Kategori Bazlı Gider Dağılımı");

        PieChart pie = new PieChart();
        pie.setTitle("Giderlerin Dağılımı");

        FinanceManager fm = new FinanceManager((User) currentUser);
        Map<String, Double> totals = new HashMap<>();
        for (Transaction t : fm.getAllTransactions()) {
            if (t.getType().equalsIgnoreCase("Gider")) {
                String cat = (t.getCategory() == null || t.getCategory().isBlank()) ? "Bilinmeyen" : t.getCategory();
                totals.put(cat, totals.getOrDefault(cat, 0.0) + Math.abs(t.getAmount()));
            }
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        pie.setData(pieData);
        box.getChildren().addAll(title, pie);
        return box;
    }

    // Kategorilere göre toplam gider çubuğu grafiği
    public static VBox getCategoryBarChart(IUser currentUser) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        Label title = new Label("📊 Kategoriye Göre Toplam Gider");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        FinanceManager fm = new FinanceManager((User) currentUser);
        Map<String, Double> totals = new HashMap<>();

        for (Transaction t : fm.getAllTransactions()) {
            if (t.getType().equalsIgnoreCase("Gider")) {
                String cat = (t.getCategory() == null || t.getCategory().isBlank()) ? "Bilinmeyen" : t.getCategory();
                totals.put(cat, totals.getOrDefault(cat, 0.0) + Math.abs(t.getAmount()));
            }
        }

        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chart.getData().add(series);
        box.getChildren().addAll(title, chart);
        return box;
    }

    // Genel istatistik özeti
    public static VBox getSummary(IUser currentUser) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));

        Label title = new Label("💡 Genel Özet");

        FinanceManager fm = new FinanceManager((User) currentUser);
        List<YearMonth> months = getLastSixMonths();

        // Hesaplamalar
        double totalIncome = months.stream().mapToDouble(fm::getMonthlyIncome).sum();
        double totalExpense = months.stream().mapToDouble(fm::getMonthlyExpense).sum();
        double netBalance = totalIncome - totalExpense;
        double avgIncome = totalIncome / months.size();
        double avgExpense = totalExpense / months.size();
        double savingRate = totalIncome > 0 ? (netBalance / totalIncome) * 100 : 0;

        String topCategory = fm.getMostSpentCategory(months);
        String bestMonth = fm.getHighestIncomeMonth(months);

        // Fatura ve hedef bilgileri
        int totalBills = fm.getAllBills().size();
        long paidBills = fm.getAllBills().stream().filter(Bill::isPaid).count();
        long unpaidBills = totalBills - paidBills;

        int totalGoals = fm.getAllGoals().size();
        long completedGoals = fm.getAllGoals().stream().filter(FinancialGoal::isCompleted).count();
        long ongoingGoals = totalGoals - completedGoals;

        // Etiketler
        Label incomeLabel = new Label("📈 Toplam Gelir: ₺" + String.format("%,.2f", totalIncome));
        Label expenseLabel = new Label("📉 Toplam Gider: ₺" + String.format("%,.2f", totalExpense));
        Label netLabel = new Label((netBalance >= 0 ? "🟢 Net Kar: ₺" : "🔴 Net Zarar: ₺") + String.format("%,.2f", Math.abs(netBalance)));
        Label avgIncomeLabel = new Label("📊 Aylık Ortalama Gelir: ₺" + String.format("%,.2f", avgIncome));
        Label avgExpenseLabel = new Label("📉 Aylık Ortalama Gider: ₺" + String.format("%,.2f", avgExpense));
        Label categoryLabel = new Label("📌 En Fazla Harcama: " + topCategory);
        Label bestMonthLabel = new Label("🏆 En Yüksek Gelir Ayı: " + bestMonth);
        Label savingRateLabel = new Label("📈 Tasarruf Oranı: %" + String.format("%.1f", savingRate));
        Label billLabel = new Label("📄 Faturalar → Ödenen: " + paidBills + " | Bekleyen: " + unpaidBills + " | Toplam: " + totalBills);
        Label goalLabel = new Label("🎯 Hedefler → Tamamlanan: " + completedGoals + " | Devam Eden: " + ongoingGoals + " | Toplam: " + totalGoals);

        // Yorum
        String comment = netBalance > 0 ? "Tebrikler! Harcamalarınızı kontrol altında tutuyorsunuz." :
                netBalance < 0 ? "Dikkat! Giderler gelirleri aşıyor." :
                        "Harcamalarınız ve gelirleriniz dengede.";
        Label analysis = new Label("📝 Durum Analizi: " + comment);

        box.getChildren().addAll(title, incomeLabel, expenseLabel, netLabel, avgIncomeLabel, avgExpenseLabel,
                categoryLabel, bestMonthLabel, savingRateLabel, billLabel, goalLabel, analysis);

        return box;
    }

    // Son 6 ayı getirir
    private static List<YearMonth> getLastSixMonths() {
        List<YearMonth> list = new ArrayList<>();
        YearMonth current = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            list.add(current.minusMonths(i));
        }
        return list;
    }
}

