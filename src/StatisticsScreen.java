import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.YearMonth;
import java.util.*;

// Tüm finansal istatistikleri aynı ekranda grafiksel olarak sunan sınıf
public class StatisticsScreen {

    // Ana istatistik ekranını döner
    public static VBox getView(IUser currentUser) {
        VBox root = new VBox(30);
        root.getStyleClass().add("root-container");

        Label title = new Label("📊 Finansal İstatistikler");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        FinanceManager fm = new FinanceManager((User) currentUser);
        List<YearMonth> lastSixMonths = getLastSixMonths();

        // 1. GELİR-GİDER ÇUBUK GRAFİĞİ
        Label incomeExpenseTitle = new Label("💰 Son 6 Ay Gelir-Gider Dağılımı");

        CategoryAxis xAxis1 = new CategoryAxis();
        NumberAxis yAxis1 = new NumberAxis();
        BarChart<String, Number> incomeExpenseChart = new BarChart<>(xAxis1, yAxis1);

        XYChart.Series<String, Number> gelirSeries = new XYChart.Series<>();
        gelirSeries.setName("Gelir");
        XYChart.Series<String, Number> giderSeries = new XYChart.Series<>();
        giderSeries.setName("Gider");

        for (YearMonth ym : lastSixMonths) {
            String label = ym.getMonth().toString().substring(0, 3) + " " + ym.getYear();
            gelirSeries.getData().add(new XYChart.Data<>(label, fm.getMonthlyIncome(ym)));
            giderSeries.getData().add(new XYChart.Data<>(label, fm.getMonthlyExpense(ym)));
        }

        incomeExpenseChart.getData().addAll(gelirSeries, giderSeries);
        VBox incomeExpenseBox = new VBox(5, incomeExpenseTitle, incomeExpenseChart);

        // 2. KATEGORİ PASTA GRAFİĞİ
        Label categoryPieTitle = new Label("📂 Kategori Bazlı Gider Dağılımı");
        PieChart categoryPie = new PieChart();
        categoryPie.setTitle("Giderlerin Dağılımı");

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Transaction t : fm.getAllTransactions()) {
            if (t.getType().equalsIgnoreCase("Gider")) {
                String category = (t.getCategory() == null || t.getCategory().isBlank()) ? "Bilinmeyen" : t.getCategory();
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + Math.abs(t.getAmount()));
            }
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
            pieData.add(data);
            // Pasta dilimi üzerine tooltip
            Tooltip.install(data.getNode(), new Tooltip(entry.getKey() + ": ₺" + entry.getValue()));
        }

        categoryPie.setData(pieData);
        VBox categoryPieBox = new VBox(5, categoryPieTitle, categoryPie);

        // 3. KATEGORİ ÇUBUK GRAFİĞİ
        Label categoryBarTitle = new Label("📊 Kategoriye Göre Toplam Gider");
        CategoryAxis xAxis2 = new CategoryAxis();
        NumberAxis yAxis2 = new NumberAxis();
        BarChart<String, Number> categoryBarChart = new BarChart<>(xAxis2, yAxis2);

        XYChart.Series<String, Number> categorySeries = new XYChart.Series<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            categorySeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        categoryBarChart.getData().add(categorySeries);
        VBox categoryBarBox = new VBox(5, categoryBarTitle, categoryBarChart);

        // 4. GENEL ÖZET
        Label summaryTitle = new Label("💡 Genel Özet");

        double totalIncome = lastSixMonths.stream().mapToDouble(fm::getMonthlyIncome).sum();
        double totalExpense = lastSixMonths.stream().mapToDouble(fm::getMonthlyExpense).sum();
        double net = totalIncome - totalExpense;

        Label summary = new Label("Toplam Gelir: ₺" + totalIncome +
                "\nToplam Gider: ₺" + totalExpense +
                "\nNet Bakiye: ₺" + net);
        VBox summaryBox = new VBox(5, summaryTitle, summary);

        // Tüm panelleri satırlara yerleştir
        HBox row1 = new HBox(30, incomeExpenseBox, categoryPieBox);
        HBox row2 = new HBox(30, categoryBarBox, summaryBox);

        root.getChildren().addAll(title, row1, row2);
        return root;
    }

    // Son 6 ayı döner
    private static List<YearMonth> getLastSixMonths() {
        List<YearMonth> list = new ArrayList<>();
        YearMonth current = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            list.add(current.minusMonths(i));
        }
        return list;
    }
}
