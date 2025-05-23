import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        //  Tabloları oluştur
        new DatabaseManager().createTables();
        System.out.println("ÇALIŞAN DB YOLU: " + System.getProperty("user.dir") + "/finansapp.db");



        //  Giriş ekranını başlat
        new LoginRegisterScreen().start(primaryStage);
    }

    public static void main(String[] args) {

        // JavaFX uygulamasını başlat
        launch(args); //
    }
}
