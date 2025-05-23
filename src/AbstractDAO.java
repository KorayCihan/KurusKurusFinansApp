import java.sql.*;

// Tüm DAO sınıflarının kalıtım alacağı temel sınıf
public abstract class AbstractDAO {

    // Veritabanı bağlantı adresimiz
    protected static final String DB_URL = "jdbc:sqlite:finansapp.db";

    // Veritabanına bağlantı kurar
    protected Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // SQL güncelleme insert, update, delete işlemleri için kullanılır
    protected boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kayıt var mı yok mu kontrol eder
    protected boolean recordExists(String sql, Object... params) {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
