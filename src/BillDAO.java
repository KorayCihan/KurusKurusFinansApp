import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Fatura veritabanı işlemleri bu sınıfta yapılır
public class BillDAO extends AbstractDAO implements BaseDAO<Bill> {

    // Yeni fatura ekler
    public boolean add(Bill bill, int userId) {
        String sql = "INSERT INTO bills (user_id, bill_name, amount, due_date, is_paid) VALUES (?, ?, ?, ?, ?)";
        return executeUpdate(sql,
                userId,
                bill.getBillName(),
                bill.getAmount(),
                bill.getDueDate().toString(),
                bill.isPaid()
        );
    }

    // Faturayı günceller
    public boolean update(Bill bill, int userId) {
        String sql = "UPDATE bills SET bill_name = ?, amount = ?, due_date = ?, is_paid = ? WHERE id = ?";
        return executeUpdate(sql,
                bill.getBillName(),
                bill.getAmount(),
                bill.getDueDate().toString(),
                bill.isPaid(),
                bill.getId()
        );
    }

    // Faturayı ismine göre siler
    public boolean delete(String billName, int userId) {
        String sql = "DELETE FROM bills WHERE user_id = ? AND bill_name = ?";
        return executeUpdate(sql, userId, billName);
    }

    // Kullanıcıya ait tüm faturaları getirir
    public List<Bill> getAllByUserId(int userId) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT id, bill_name, amount, due_date, is_paid FROM bills WHERE user_id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Bill bill = new Bill(
                        rs.getString("bill_name"),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("due_date")),
                        rs.getBoolean("is_paid")
                );
                bill.setId(rs.getInt("id")); // ID'yi set et
                bills.add(bill);
            }

        } catch (SQLException e) {
            System.err.println("Faturalar alınırken hata oluştu: " + e.getMessage());
        }

        return bills;
    }

    // Aynı isimde fatura var mı kontrol eder
    public boolean exists(String billName, int userId) {
        String sql = "SELECT COUNT(*) FROM bills WHERE user_id = ? AND bill_name = ?";
        return recordExists(sql, userId, billName);
    }
}
