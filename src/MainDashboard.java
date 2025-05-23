import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class MainDashboard {

    private BorderPane root;
    private Scene scene;
    private IUser currentUser;

    // Kullanıcı bilgisi ile dashboard başlatılır
    public MainDashboard(IUser user) {
        this.currentUser = user;
    }

    // Ana pencereyi başlatır
    public void startDashboard(Stage stage) {
        root = new BorderPane();
        scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        root.setLeft(createSideMenu());       // Sol menüyü yükle
        setCenter(createDashboardView());     // Orta bölüme özet ekranı yerleştir

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/kuruskurus.png")));
        stage.setTitle("KuruşKuruş - " + currentUser.getUsername());
        stage.setScene(scene);
        stage.show();
    }

    // Sol menü (navigasyon) oluşturulur
    private VBox createSideMenu() {
        VBox menuBox = new VBox(10);
        menuBox.setPadding(new Insets(10));
        menuBox.setStyle("-fx-background-color: #f0f0f0;");
        menuBox.setPrefWidth(180);
        menuBox.setAlignment(Pos.CENTER);

        // Menü butonları
        Button anaPanelBtn = new Button("🏠 Ana Panel");
        Button finansBtn = new Button("💰 Gelir-Gider");
        Button hedefBtn = new Button("🎯 Hedefler");
        Button faturaBtn = new Button("🧾 Faturalar");
        Button istatistikBtn = new Button("📈 İstatistik");
        Button ayarBtn = new Button("⚙️ Ayarlar");
        Button cikisBtn = new Button("🔓 Çıkış");

        List<Button> buttons = List.of(anaPanelBtn, finansBtn, hedefBtn, faturaBtn, istatistikBtn, ayarBtn, cikisBtn);
        for (Button b : buttons) {
            b.setMaxWidth(Double.MAX_VALUE);
            b.setStyle("-fx-font-size: 14px;");
        }

        // Her butonun eylemi
        anaPanelBtn.setOnAction(e -> {
            if (currentUser instanceof User user) {
                user.setTransactions(new TransactionDAO().getAllByUserId(user.getId()));
            }
            setCenter(createDashboardView());
        });

        finansBtn.setOnAction(e -> {
            if (currentUser instanceof User user) {
                user.setTransactions(new TransactionDAO().getAllByUserId(user.getId()));
            }
            setCenter(TransactionScreen.getView((User) currentUser));
        });

        hedefBtn.setOnAction(e -> {
            if (currentUser instanceof User user) {
                user.setGoals(new FinancialGoalDAO().getAllByUserId(user.getId()));
            }
            setCenter(FinancialGoalsScreen.getView(currentUser));
        });

        faturaBtn.setOnAction(e -> {
            if (currentUser instanceof User user) {
                user.setBills(new BillDAO().getAllByUserId(user.getId()));
            }
            setCenter(BillTrackerScreen.getView(currentUser));
        });

        istatistikBtn.setOnAction(e -> {
            VBox secimEkrani = new VBox(15);
            secimEkrani.setPadding(new Insets(20));
            secimEkrani.setAlignment(Pos.CENTER_LEFT);

            Label baslik = new Label("📊 Görmek İstediğiniz Grafik:");
            baslik.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            baslik.setMaxWidth(Double.MAX_VALUE);
            baslik.setAlignment(Pos.CENTER);

            Button btn1 = new Button("📊 Son 6 Ay Gelir-Gider Dağılımı");
            btn1.setOnAction(ev -> setCenter(StatisticsForm.getIncomeExpenseChart(currentUser)));

            Button btn2 = new Button("🥧 Kategori Bazlı Gider Dağılımı");
            btn2.setOnAction(ev -> setCenter(StatisticsForm.getCategoryPieChart(currentUser)));

            Button btn3 = new Button("📶 Kategoriye Göre Toplam Gider");
            btn3.setOnAction(ev -> setCenter(StatisticsForm.getCategoryBarChart(currentUser)));

            Button btn4 = new Button("💡 Genel Özet");
            btn4.setOnAction(ev -> setCenter(StatisticsForm.getSummary(currentUser)));

            for (Button b : List.of(btn1, btn2, btn3, btn4)) {
                b.setMaxWidth(Double.MAX_VALUE);
                b.setStyle("-fx-font-size: 14px;");
            }

            secimEkrani.getChildren().addAll(baslik, btn1, btn2, btn3, btn4);
            setCenter(secimEkrani);
        });

        ayarBtn.setOnAction(e -> setCenter(SettingsScreen.getView()));

        // Oturumu kapatır
        cikisBtn.setOnAction(e -> {
            Session.logout();
            new LoginRegisterScreen().start(new Stage());
            ((Stage) root.getScene().getWindow()).close();
        });

        menuBox.getChildren().addAll(buttons);
        return menuBox;
    }

    // Ana panelde gösterilecek içerik ayarlanır
    private void setCenter(VBox content) {
        root.setCenter(content);
    }

    // Girişten sonra ana panelin içeriğini oluşturur
    private VBox createDashboardView() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Hoş geldin, " + currentUser.getUsername() + "!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #212529;");

        FinanceManager fm = new FinanceManager((User) currentUser);
        YearMonth currentMonth = YearMonth.now();

        double gelir = fm.getMonthlyIncome(currentMonth);
        double gider = fm.getMonthlyExpense(currentMonth);
        double fark = gelir - gider;

        Label incomeLabel = new Label("Toplam Gelir: " + gelir + "₺");
        incomeLabel.setStyle("-fx-text-fill: #198754; -fx-font-weight: bold;");

        Label expenseLabel = new Label("Toplam Gider: " + gider + "₺");
        expenseLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");

        Label netLabel = new Label("Aylık Kar-Zarar: " +
                (fark == 0 ? "0₺ (Denge)" : (fark > 0 ? "+" : "") + fark + "₺"));
        netLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #198754;");

        VBox infoBox = new VBox(10, incomeLabel, expenseLabel, netLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-background-color: #e9ecef; -fx-border-radius: 5;");

        // Harcamalara dair özet
        Map<String, Double> chartData = fm.getExpenseByCategory(currentMonth);
        String topCategory = chartData.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Veri yok");

        VBox highlightBox = new VBox(10);
        highlightBox.setPadding(new Insets(10));
        highlightBox.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffecb5; -fx-border-radius: 5;");

        Label topCategoryLabel = new Label("📌 En Fazla Harcama: " + topCategory);
        topCategoryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #856404;");

        Label remainingLabel = new Label("Kalan Bütçe: " + Math.max(0, fark) + "₺");
        remainingLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6c757d;");

        highlightBox.getChildren().addAll(topCategoryLabel, remainingLabel);

        // Harcama Pie Chart
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Harcama Dağılımı");
        pieChart.setPrefHeight(250);

        if (!chartData.isEmpty()) {
            chartData.forEach((kategori, miktar) ->
                    pieChart.getData().add(new PieChart.Data(kategori, miktar)));
        }

        dashboard.getChildren().addAll(welcomeLabel, infoBox, highlightBox, pieChart);
        return dashboard;
    }
}
