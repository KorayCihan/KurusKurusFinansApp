import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Veritabanı bağlantısı sağlayan yardımcı sınıf
public class DatabaseConnection {

    // SQLite veritabanına bağlanır
    public static Connection connect() {
        try {
            String url = "jdbc:sqlite:finansapp.db";
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Veritabanı bağlantı hatası: " + e.getMessage());
            return null;
        }
    }
}
