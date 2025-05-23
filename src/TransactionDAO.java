import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Veritabanı üzerinden Transaction (işlem) nesneleriyle çalışmayı sağlayan DAO sınıfı
public class TransactionDAO extends AbstractDAO implements BaseDAO<Transaction> {

    // Yeni bir işlem ekler

    public boolean add(Transaction t, int userId) {
        String sql = "INSERT INTO transactions (user_id, type, category, amount, date, description) VALUES (?, ?, ?, ?, ?, ?)";
        return executeUpdate(sql,
                userId,
                t.getType(),
                t.getCategory(),
                t.getAmount(),
                t.getDate().toString(),
                t.getDescription()
        );
    }

    // Var olan bir işlemi günceller

    public boolean update(Transaction t, int userId) {
        String sql = "UPDATE transactions SET type = ?, category = ?, amount = ?, date = ?, description = ? " +
                "WHERE id = ? AND user_id = ?";
        return executeUpdate(sql,
                t.getType(),
                t.getCategory(),
                t.getAmount(),
                t.getDate().toString(),
                t.getDescription(),
                t.getId(),
                userId
        );
    }

    // Bir işlemi id'sine göre siler

    public boolean delete(String idAsString, int userId) {
        int id = Integer.parseInt(idAsString);
        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";
        return executeUpdate(sql, id, userId);
    }

    // Kullanıcıya ait tüm işlemleri getirir

    public List<Transaction> getAllByUserId(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Transaction(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("date")),
                        rs.getString("description")
                ));
            }

        } catch (SQLException e) {
            System.err.println("İşlemler alınırken hata oluştu: " + e.getMessage());
        }

        return list;
    }

    // Aynı işlem zaten var mı kontrol eder
    public boolean exists(Transaction t, int userId) {
        String sql = """
            SELECT COUNT(*) FROM transactions 
            WHERE user_id = ? AND type = ? AND category = ? AND amount = ? AND date = ? AND description = ?
        """;
        return recordExists(sql,
                userId,
                t.getType(),
                t.getCategory(),
                t.getAmount(),
                t.getDate().toString(),
                t.getDescription()
        );
    }
}
