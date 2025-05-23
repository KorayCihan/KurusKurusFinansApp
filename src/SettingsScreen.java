import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// Ayarlar ekranı bileşenlerini oluşturan sınıf
public class SettingsScreen {

    // Ayarlar ekranı arayüzünü döner
    public static VBox getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("root-container");

        // --- Şifre değiştirme alanları ---
        PasswordField oldPass = new PasswordField();       // Mevcut şifre
        oldPass.setPromptText("Mevcut Şifre");

        PasswordField newPass = new PasswordField();       // Yeni şifre
        newPass.setPromptText("Yeni Şifre");

        PasswordField confirmPass = new PasswordField();   // Yeni şifre tekrarı
        confirmPass.setPromptText("Yeni Şifre (Tekrar)");

        Button updatePasswordBtn = new Button("🔒 Şifreyi Güncelle");

        // Şifre güncelleme işlemi
        updatePasswordBtn.setOnAction(e -> {
            UserDAO dao = new UserDAO();
            User user = (User) Session.getCurrentUser();

            // Eski şifre doğru mu?
            if (!dao.verifyPassword(user.getUsername(), oldPass.getText())) {
                new Alert(Alert.AlertType.ERROR, "Mevcut şifre yanlış!").showAndWait();
                return;
            }

            // Yeni şifreler eşleşiyor mu?
            if (!newPass.getText().equals(confirmPass.getText())) {
                new Alert(Alert.AlertType.ERROR, "Yeni şifreler eşleşmiyor!").showAndWait();
                return;
            }

            // Şifre güncelle
            dao.updatePassword(user.getId(), newPass.getText());
            new Alert(Alert.AlertType.INFORMATION, "Şifre başarıyla güncellendi!").showAndWait();

            // Alanları temizle
            oldPass.clear();
            newPass.clear();
            confirmPass.clear();
        });

        VBox passwordBox = new VBox(10, oldPass, newPass, confirmPass, updatePasswordBtn);
        passwordBox.setPadding(new Insets(10));

        TitledPane passwordPane = new TitledPane("🔑 Şifre Değiştir", passwordBox);
        passwordPane.setExpanded(true);

        // --- Hesap silme alanı ---
        Button deleteAccountBtn = new Button("🗑️ Hesabı Sil");

        deleteAccountBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Hesap Silme");
            confirm.setHeaderText("Hesabınızı silmek üzeresiniz!");
            confirm.setContentText("Bu işlem geri alınamaz. Emin misiniz?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    new UserDAO().deleteUser(Session.getCurrentUser().getId());
                    Session.logout();
                    new LoginRegisterScreen().start(new Stage());
                    ((Stage) root.getScene().getWindow()).close();
                }
            });
        });

        VBox deleteBox = new VBox(10, deleteAccountBtn);
        deleteBox.setPadding(new Insets(10));
        deleteBox.setAlignment(Pos.CENTER);

        TitledPane deletePane = new TitledPane("❌ Hesabı Sil", deleteBox);
        deletePane.setExpanded(false);

        // Ekrana ekle
        root.getChildren().addAll(passwordPane, deletePane);
        return root;
    }
}