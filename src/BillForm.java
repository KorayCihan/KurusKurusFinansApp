import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

// Fatura ekleme/güncelleme form ekranı
public class BillForm {

    // Form gönderildiğinde yapılacak işlemi temsil eden arayüz
    public interface FormCallback {
        void onSubmit(Bill bill);
    }

    // Formu gösterir (
    public static void showForm(Stage owner, Bill existing, FormCallback callback) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existing == null ? "Yeni Fatura Ekle" : "Faturayı Güncelle");

        // Form bileşenleri
        Label nameLabel = new Label("Fatura Adı:");
        TextField nameField = new TextField();
        nameField.setPrefWidth(200);

        Label amountLabel = new Label("Tutar (₺):");
        TextField amountField = new TextField();
        amountField.setPrefWidth(100);

        Label dateLabel = new Label("Son Ödeme:");
        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPrefWidth(150);

        CheckBox paidCheckBox = new CheckBox("Ödendi");

        // Güncelleme modunda mevcut verileri doldurma
        if (existing != null) {
            nameField.setText(existing.getBillName());
            amountField.setText(String.valueOf(existing.getAmount()));
            dueDatePicker.setValue(existing.getDueDate());
            paidCheckBox.setSelected(existing.isPaid());
        } else {
            dueDatePicker.setValue(LocalDate.now().plusDays(7));
        }

        Button saveButton = new Button("💾 Kaydet");
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(120);
        saveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        // Kaydet butonuna tıklanınca yapılacak işlemler
        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String amountText = amountField.getText().trim();
                LocalDate dueDate = dueDatePicker.getValue();
                boolean paid = paidCheckBox.isSelected();

                // Giriş kontrolleri
                if (name.isEmpty()) throw new IllegalArgumentException("Fatura adı boş olamaz.");
                if (amountText.isEmpty()) throw new IllegalArgumentException("Tutar boş olamaz.");

                double amount;
                try {
                    amount = Double.parseDouble(amountText);
                    if (amount <= 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Pozitif bir sayı girin.");
                }

                if (dueDate == null) throw new IllegalArgumentException("Tarih seçin.");

                // Eğer yeni kayıt ise aynı isimde fatura var mı kontrol et
                if (existing == null) {
                    BillDAO dao = new BillDAO();
                    IUser user = Session.getCurrentUser();
                    if (dao.exists(name, user.getId())) {
                        throw new IllegalArgumentException("Bu isimde bir fatura zaten var!");
                    }
                }

                // Güncelleme ya da yeni fatura işlemi
                if (existing != null) {
                    existing.setBillName(name);
                    existing.setAmount(amount);
                    existing.setDueDate(dueDate);
                    existing.setPaid(paid);
                    callback.onSubmit(existing);
                } else {
                    Bill newBill = new Bill(name, amount, dueDate, paid);
                    callback.onSubmit(newBill);
                }

                dialog.close();

            } catch (Exception ex) {
                // Hata mesajı göster
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hata");
                alert.setHeaderText("⚠️ Geçersiz Giriş");
                alert.setContentText("Hata: " + ex.getMessage());

                DialogPane pane = alert.getDialogPane();
                pane.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
                pane.getStylesheets().add(BillForm.class.getResource("/style.css").toExternalForm());
                pane.getStyleClass().add("custom-alert");

                alert.showAndWait();
            }
        });

        // Grid yerleşimi
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20));

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(amountLabel, 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(dateLabel, 0, 2);
        grid.add(dueDatePicker, 1, 2);
        grid.add(paidCheckBox, 1, 3);

        HBox buttonBox = new HBox(saveButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        VBox layout = new VBox(10, grid, buttonBox);
        layout.setPadding(new Insets(15));
        layout.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);");

        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.setMinWidth(450);
        dialog.showAndWait();
    }
}
