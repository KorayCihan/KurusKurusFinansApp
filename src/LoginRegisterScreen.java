import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginRegisterScreen extends Application {

    private final UserDAO userDAO = new UserDAO();

    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Logo ve başlık
        ImageView logoView = new ImageView(new Image("file:src/kuruskurus.png"));
        logoView.setFitWidth(100);
        logoView.setPreserveRatio(true);

        Label titleLabel = new Label("KuruşKuruş");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Giriş alanları
        TextField usernameField = new TextField();
        usernameField.setPromptText("Kullanıcı Adı");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Şifre");

        Button loginButton = new Button("Giriş Yap");
        Button registerButton = new Button("Kayıt Ol");
        Label messageLabel = new Label();

        // Giriş butonu aksiyonu
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (!isValidUsername(username)) {
                messageLabel.setText("Kullanıcı adı yalnızca harf, rakam veya _ içermeli (3-15 karakter)");
                return;
            }

            IUser user = userDAO.getUserByUsernameAndPassword(username, password);
            Session.setCurrentUser(user);
            if (user != null) {
                messageLabel.setText("Giriş başarılı!");
                MainDashboard dashboard = new MainDashboard(user);
                Stage dashboardStage = new Stage();
                dashboard.startDashboard(dashboardStage);
                primaryStage.close();
            } else {
                messageLabel.setText("Hatalı kullanıcı adı veya şifre.");
            }
        });

        // Kayıt olma butonu aksiyonu
        registerButton.setOnAction(e -> {
            Stage registerStage = new Stage();
            registerStage.setTitle("Yeni Kayıt");

            VBox registerRoot = new VBox(10);
            registerRoot.setPadding(new Insets(20));
            registerRoot.setAlignment(Pos.CENTER);

            TextField newUsernameField = new TextField();
            newUsernameField.setPromptText("Yeni kullanıcı adı");

            PasswordField newPasswordField = new PasswordField();
            newPasswordField.setPromptText("Şifre");

            PasswordField confirmPasswordField = new PasswordField();
            confirmPasswordField.setPromptText("Şifreyi tekrar girin");

            Button confirmRegisterBtn = new Button("Kayıt Ol");
            Label feedbackLabel = new Label();

            confirmRegisterBtn.setOnAction(ev -> {
                String username = newUsernameField.getText();
                String password = newPasswordField.getText();
                String confirm = confirmPasswordField.getText();

                if (!isValidUsername(username)) {
                    feedbackLabel.setText("Kullanıcı adı yalnızca harf, rakam veya _ içermeli (3-15 karakter)");
                    return;
                }

                if (password.isBlank() || confirm.isBlank()) {
                    feedbackLabel.setText("Şifre alanları boş bırakılamaz!");
                    return;
                }

                if (!password.equals(confirm)) {
                    feedbackLabel.setText("Şifreler uyuşmuyor!");
                    return;
                }

                if (userDAO.getUserByUsername(username.trim()) != null) {
                    feedbackLabel.setText("Bu kullanıcı adı zaten mevcut!");
                    return;
                }

                boolean success = userDAO.addUser(new User(username.trim(), password));
                if (success) {
                    feedbackLabel.setText("Kayıt başarılı! Giriş yapabilirsiniz.");
                    PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
                    pause.setOnFinished(e2 -> registerStage.close());
                    pause.play();
                } else {
                    feedbackLabel.setText("Kayıt sırasında bir hata oluştu.");
                }
            });

            registerRoot.getChildren().addAll(
                    newUsernameField,
                    newPasswordField,
                    confirmPasswordField,
                    confirmRegisterBtn,
                    feedbackLabel
            );

            Scene registerScene = new Scene(registerRoot, 300, 250);
            registerScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            registerStage.setScene(registerScene);
            registerStage.show();
        });

        // Butonları yerleştir
        HBox buttonBox = new HBox(10, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Ana düzen
        root.getChildren().addAll(
                logoView,
                titleLabel,
                usernameField,
                passwordField,
                buttonBox,
                messageLabel
        );

        Scene scene = new Scene(root, 350, 300);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.getIcons().add(new Image("file:src/kuruskurus.png"));
        primaryStage.setTitle("Giriş / Kayıt Ekranı");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Kullanıcı adı geçerlilik kontrolü
    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,15}$");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
