import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

// Fatura takibi arayüzü
public class BillTrackerScreen {

    public static VBox getView(IUser currentUser) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("root-container");

        Label title = new Label("📄 Fatura Takibi");

        // Veritabanından kullanıcının faturaları çekilir
        ObservableList<Bill> bills = FXCollections.observableArrayList();
        BillDAO dao = new BillDAO();
        bills.setAll(dao.getAllByUserId(currentUser.getId()));

        // Fatura tablosu hazırlanır
        TableView<Bill> table = new TableView<>(bills);

        // Sütunlar tanımlanır
        TableColumn<Bill, String> nameCol = new TableColumn<>("Fatura Adı");
        nameCol.setCellValueFactory(cell -> cell.getValue().billNameProperty());

        TableColumn<Bill, Double> amountCol = new TableColumn<>("Tutar (₺)");
        amountCol.setCellValueFactory(cell -> cell.getValue().amountProperty().asObject());

        TableColumn<Bill, LocalDate> dateCol = new TableColumn<>("Son Ödeme");
        dateCol.setCellValueFactory(cell -> cell.getValue().dueDateProperty());

        TableColumn<Bill, Boolean> paidCol = new TableColumn<>("Ödendi");
        paidCol.setCellValueFactory(cell -> cell.getValue().isPaidProperty());
        paidCol.setCellFactory(CheckBoxTableCell.forTableColumn(paidCol));

        table.getColumns().addAll(nameCol, amountCol, dateCol, paidCol);

        // Giriş alanları ve butonlarımız
        TextField nameField = new TextField();
        TextField amountField = new TextField();
        DatePicker dueDatePicker = new DatePicker();
        CheckBox paidCheckBox = new CheckBox("Ödendi");

        Button addButton = new Button("➕ Ekle");
        Button updateButton = new Button("✏️ Güncelle");
        Button deleteButton = new Button("🗑️ Sil");

        // Üst giriş paneli
        HBox topRow = new HBox(10, nameField, amountField, dueDatePicker, paidCheckBox, addButton, updateButton, deleteButton);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setPadding(new Insets(10));

        root.getChildren().addAll(title, topRow, table);

        // Ekleme işlemi
        addButton.setOnAction(e -> BillForm.showForm((Stage) root.getScene().getWindow(), null, bill -> {
            if (dao.add(bill, currentUser.getId())) {
                bills.setAll(dao.getAllByUserId(currentUser.getId()));
            }
        }));

        // Güncelleme işlemi
        updateButton.setOnAction(e -> {
            Bill selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                BillForm.showForm((Stage) root.getScene().getWindow(), selected, bill -> {
                    dao.update(bill, currentUser.getId());
                    bills.setAll(dao.getAllByUserId(currentUser.getId()));
                    table.refresh();
                });
            } else {
                // Seçim yoksa bilgi ver
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Bilgi");
                alert.setHeaderText("ℹ️ Güncelleme İşlemi");
                alert.setContentText("Güncellemek için bir fatura seçin.");
                alert.showAndWait();
            }
        });

        // Silme işlemi
        deleteButton.setOnAction(e -> {
            Bill selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Onay");
                confirm.setHeaderText("❓ Fatura Silme İşlemi");
                confirm.setContentText("Bu faturayı silmek istediğinizden emin misiniz?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        dao.delete(selected.getBillName(), currentUser.getId());
                        bills.setAll(dao.getAllByUserId(currentUser.getId()));
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Bilgi");
                alert.setHeaderText("ℹ️ Silme İşlemi");
                alert.setContentText("Silmek için bir fatura seçin.");
                alert.showAndWait();
            }
        });

        // Yaklaşan fatura uyarılarını kontrol eden arka plan thread'i
        HashMap<String, LocalDateTime> alertTimestamps = new HashMap<>();
        new Thread(() -> {
            while (true) {
                try {
                    List<Bill> allBills = dao.getAllByUserId(currentUser.getId());
                    LocalDate now = LocalDate.now();
                    LocalDateTime currentTime = LocalDateTime.now();

                    StringBuilder alertBuilder = new StringBuilder();

                    for (Bill b : allBills) {
                        String key = b.getBillName();
                        LocalDate due = b.getDueDate();

                        // Son ödeme tarihi 3 gün içindeyse ve daha önce uyarı gösterilmemişse
                        if (!b.isPaid() && due.isBefore(now.plusDays(3)) && !due.isBefore(now)) {
                            boolean shouldAlert = !alertTimestamps.containsKey(key)
                                    || alertTimestamps.get(key).plusHours(1).isBefore(currentTime);

                            if (shouldAlert) {
                                alertBuilder.append("• ")
                                        .append(b.getBillName())
                                        .append(" → ")
                                        .append(due)
                                        .append("\n");

                                alertTimestamps.put(key, currentTime);
                            }
                        }
                    }

                    // Uyarı mesajını göster
                    String alertMessage = alertBuilder.toString();
                    if (!alertMessage.isEmpty()) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Yaklaşan Faturalar");
                            alert.setHeaderText("📢 Son ödeme tarihi yaklaşan faturalar:");
                            alert.setContentText(alertMessage);
                            alert.show();
                        });
                    }

                    Thread.sleep(10000); // 10 saniyede bir kontrol
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return root;
    }
}
