import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;

// İşlem  eklemek veya güncellemek için kullanılan form penceresi
public class TransactionForm {

    // Formdan geri dönecek işlem nesnesini almak için callback arayüzü
    public interface FormCallback {
        void onSubmit(Transaction transaction);
    }

    // Form penceresini gösterir
    public static void showForm(Stage owner, Transaction existing, FormCallback callback) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existing == null ? "Yeni İşlem Ekle" : "İşlemi Güncelle");

        // Girdi alanları
        Label typeLabel = new Label("Tür:");
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Gelir", "Gider");

        Label categoryLabel = new Label("Kategori:");
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Gıda", "Ulaşım", "Eğlence", "Fatura", "Diğer");

        Label amountLabel = new Label("Tutar (₺):");
        TextField amountField = new TextField();

        Label dateLabel = new Label("Tarih:");
        DatePicker datePicker = new DatePicker();

        Label descriptionLabel = new Label("Açıklama:");
        TextField descriptionField = new TextField();

        // Var olan işlem varsa alanları doldur
        if (existing != null) {
            typeBox.setValue(existing.getType());
            categoryBox.setValue(existing.getCategory());
            amountField.setText(String.valueOf(existing.getAmount()));
            datePicker.setValue(existing.getDate());
            descriptionField.setText(existing.getDescription());
        } else {
            datePicker.setValue(LocalDate.now());
        }

        // Kaydet butonu
        Button saveButton = new Button("💾 Kaydet");
        saveButton.setDefaultButton(true);

        saveButton.setOnAction(e -> {
            try {
                String type = typeBox.getValue();
                String category = categoryBox.getValue();
                String amountText = amountField.getText().trim();
                LocalDate date = datePicker.getValue();
                String description = descriptionField.getText().trim();

                // Alan doğrulamaları
                if (type == null || category == null || amountText.isEmpty() || date == null || description.isEmpty()) {
                    throw new IllegalArgumentException("Lütfen tüm alanları eksiksiz doldurun.");
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountText);
                    if (amount <= 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Tutar geçerli ve pozitif bir sayı olmalıdır.");
                }

                // Yeni işlemse ve zaten varsa engelle
                if (existing == null) {
                    TransactionDAO dao = new TransactionDAO();
                    IUser user = Session.getCurrentUser();
                    Transaction dummy = new Transaction(type, category, amount, date, description);
                    if (dao.exists(dummy, user.getId())) {
                        throw new IllegalArgumentException("Bu işlem zaten kayıtlı!");
                    }
                }

                // Mevcut işlem güncelleniyor
                Transaction tx = existing != null ? existing : new Transaction(type, category, amount, date, description);
                if (existing != null) {
                    tx.setType(type);
                    tx.setCategory(category);
                    tx.setAmount(amount);
                    tx.setDate(date);
                    tx.setDescription(description);
                }

                callback.onSubmit(tx);
                dialog.close();

            } catch (Exception ex) {
                // Hata durumunda kullanıcıya mesaj göster
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hata");
                alert.setHeaderText("⚠️ Bir şeyler ters gitti");
                alert.setContentText("Hata: " + ex.getMessage());

                DialogPane pane = alert.getDialogPane();
                pane.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
                pane.getStylesheets().add(TransactionForm.class.getResource("/style.css").toExternalForm());
                pane.getStyleClass().add("custom-alert");

                alert.showAndWait();
            }
        });

        // Grid düzeni
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20));

        grid.add(typeLabel, 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(categoryLabel, 2, 0);
        grid.add(categoryBox, 3, 0);
        grid.add(amountLabel, 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(dateLabel, 2, 1);
        grid.add(datePicker, 3, 1);
        grid.add(descriptionLabel, 0, 2);
        grid.add(descriptionField, 1, 2, 3, 1);

        // Kaydet butonu kutusu
        HBox buttonBox = new HBox(saveButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        // Ana pencere düzeni
        VBox layout = new VBox(10, grid, buttonBox);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.setMinWidth(700);
        dialog.showAndWait();
    }
}
