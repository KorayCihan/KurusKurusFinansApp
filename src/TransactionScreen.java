import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;

// Kullanıcının gelir ve gider işlemlerini görüntüleyip yönetebileceği ekranı oluşturur
public class TransactionScreen {

    public static VBox getView(User currentUser) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.getStyleClass().add("root-container");

        Label title = new Label("📊 Gelir - Gider Yönetimi");
        title.getStyleClass().add("section-title");

        // Giriş alanları
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Gelir", "Gider");
        typeBox.setPromptText("Tür");

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Gıda", "Ulaşım", "Eğlence", "Fatura", "Diğer");
        categoryBox.setPromptText("Kategori");

        TextField amountField = new TextField();
        amountField.setPromptText("Tutar (₺)");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Açıklama");

        // Butonlar
        Button addButton = new Button("➕ Ekle");
        Button updateButton = new Button("🔄 Güncelle");
        Button deleteButton = new Button("🗑️ Sil");

        addButton.getStyleClass().add("btn-green");
        updateButton.getStyleClass().add("btn-blue");
        deleteButton.getStyleClass().add("btn-red");

        // Form satırı
        HBox inputBox = new HBox(10, typeBox, categoryBox, amountField, datePicker, descriptionField, addButton, updateButton, deleteButton);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(10));
        inputBox.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 8; -fx-background-radius: 8; -fx-border-color: #dee2e6;");

        // Tablo
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        TransactionDAO dao = new TransactionDAO();
        transactions.addAll(dao.getAllByUserId(currentUser.getId()));

        TableView<Transaction> table = new TableView<>(transactions);
        table.setPrefHeight(350);
        table.getStyleClass().add("styled-table");

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Tür");
        typeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Kategori");
        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Tutar");
        amountCol.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Tarih");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        TableColumn<Transaction, String> descCol = new TableColumn<>("Açıklama");
        descCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        table.getColumns().addAll(typeCol, categoryCol, amountCol, dateCol, descCol);

        // Ekleme işlemi
        addButton.setOnAction(e -> {
            TransactionForm.showForm((Stage) root.getScene().getWindow(), null, tx -> {
                dao.add(tx, currentUser.getId());
                transactions.setAll(dao.getAllByUserId(currentUser.getId()));
            });
        });

        // Güncelleme işlemi
        updateButton.setOnAction(e -> {
            Transaction selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TransactionForm.showForm((Stage) root.getScene().getWindow(), selected, tx -> {
                    dao.update(tx, currentUser.getId());
                    transactions.setAll(dao.getAllByUserId(currentUser.getId()));
                });
            } else {
                showInfo("Güncellemek için bir işlem seçin.");
            }
        });

        // Silme işlemi
        deleteButton.setOnAction(e -> {
            Transaction selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bu işlemi silmek istediğinizden emin misiniz?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        dao.delete(String.valueOf(selected.getId()), currentUser.getId());
                        transactions.setAll(dao.getAllByUserId(currentUser.getId()));
                    }
                });
            } else {
                showInfo("Silmek için bir işlem seçin.");
            }
        });

        root.getChildren().addAll(title, inputBox, table);
        return root;
    }

    // Bilgilendirme mesajı
    private static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bilgi");
        alert.setHeaderText("ℹ️ İşlem");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
