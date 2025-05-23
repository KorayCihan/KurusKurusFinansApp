import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;

// Finansal hedef ekleme/güncelleme formu
public class FinancialGoalForm {

    // Formun tamamlandığında çağırılacak callback arayüzü
    public interface FormCallback {
        void onSubmit(FinancialGoal goal);
    }

    // Formu gösterir (mevcut hedef varsa güncelleme modunda çalışır)
    public static void showForm(Stage owner, FinancialGoal existing, FormCallback callback) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existing == null ? "Yeni Hedef Ekle" : "Hedefi Güncelle");

        // Form alanları
        Label nameLabel = new Label("Hedef Adı:");
        TextField nameField = new TextField();

        Label targetLabel = new Label("Hedef Tutarı (₺):");
        TextField targetField = new TextField();

        Label currentLabel = new Label("Mevcut Tutar (₺):");
        TextField currentField = new TextField();

        Label dateLabel = new Label("Hedef Tarihi:");
        DatePicker datePicker = new DatePicker();

        // Mevcut hedef varsa alanları doldur
        if (existing != null) {
            nameField.setText(existing.getGoalName());
            targetField.setText(String.valueOf(existing.getTargetAmount()));
            currentField.setText(String.valueOf(existing.getCurrentAmount()));
            datePicker.setValue(existing.getTargetDate());
        } else {
            datePicker.setValue(LocalDate.now().plusMonths(1));
        }

        // Kaydet butonu
        Button saveButton = new Button("💾 Kaydet");
        saveButton.setDefaultButton(true);

        // Kaydetme işlemi
        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String targetText = targetField.getText().trim();
                String currentText = currentField.getText().trim();
                LocalDate date = datePicker.getValue();

                // Giriş kontrolleri
                if (name.isEmpty()) throw new IllegalArgumentException("Hedef adı boş bırakılamaz.");
                if (targetText.isEmpty() || currentText.isEmpty()) throw new IllegalArgumentException("Tutar alanları boş bırakılamaz.");

                double target, current;
                try {
                    target = Double.parseDouble(targetText);
                    current = Double.parseDouble(currentText);
                    if (target <= 0 || current < 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Tutarlar sayısal ve geçerli olmalıdır (hedef > 0, mevcut ≥ 0).");
                }

                if (date == null) throw new IllegalArgumentException("Hedef tarihi seçilmelidir.");

                // Aynı isimde hedef varsa uyar
                if (existing == null) {
                    FinancialGoalDAO dao = new FinancialGoalDAO();
                    IUser user = Session.getCurrentUser();
                    if (dao.exists(name, user.getId())) {
                        throw new IllegalArgumentException("Bu isimde bir hedef zaten var!");
                    }
                }

                // Yeni hedef veya güncelleme işlemi
                FinancialGoal goal = existing != null ? existing : new FinancialGoal(name, target, current, date);
                if (existing != null) {
                    goal.setGoalName(name);
                    goal.setTargetAmount(target);
                    goal.setCurrentAmount(current);
                    goal.setTargetDate(date);
                }

                goal.updateCompletionStatus();
                callback.onSubmit(goal);
                dialog.close();

            } catch (Exception ex) {
                // Hata penceresi göster
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hata");
                alert.setHeaderText("⚠️ Geçersiz Giriş");
                alert.setContentText("Hata: " + ex.getMessage());

                DialogPane pane = alert.getDialogPane();
                pane.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
                pane.getStylesheets().add(FinancialGoalForm.class.getResource("/style.css").toExternalForm());
                pane.getStyleClass().add("custom-alert");

                alert.showAndWait();
            }
        });

        // Form düzeni (Grid ve VBox ile)
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20));

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0, 3, 1);
        grid.add(targetLabel, 0, 1);
        grid.add(targetField, 1, 1);
        grid.add(currentLabel, 2, 1);
        grid.add(currentField, 3, 1);
        grid.add(dateLabel, 0, 2);
        grid.add(datePicker, 1, 2);

        HBox buttonBox = new HBox(saveButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        VBox layout = new VBox(10, grid, buttonBox);
        layout.setPadding(new Insets(15));

        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.setMinWidth(650);
        dialog.showAndWait();
    }
}
