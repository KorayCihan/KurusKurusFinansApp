import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Finansal hedeflerin veritabanı işlemlerini gerçekleştiren DAO sınıfı
public class FinancialGoalDAO extends AbstractDAO implements BaseDAO<FinancialGoal> {

    // Yeni hedef ekler
    public boolean add(FinancialGoal goal, int userId) {
        String sql = "INSERT INTO financial_goals (user_id, goal_name, target_amount, current_amount, target_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        return executeUpdate(sql,
                userId,
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getTargetDate().toString()
        );
    }

    // Hedefi günceller
    public boolean update(FinancialGoal goal, int userId) {
        String sql = "UPDATE financial_goals SET goal_name = ?, target_amount = ?, current_amount = ?, target_date = ? " +
                "WHERE id = ?";
        return executeUpdate(sql,
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getTargetDate().toString(),
                goal.getId()
        );
    }

    // Hedefi isme göre siler
    public boolean delete(String goalName, int userId) {
        String sql = "DELETE FROM financial_goals WHERE user_id = ? AND goal_name = ?";
        return executeUpdate(sql, userId, goalName);
    }

    // Kullanıcıya ait tüm hedefleri getirir
    public List<FinancialGoal> getAllByUserId(int userId) {
        List<FinancialGoal> goals = new ArrayList<>();
        String sql = "SELECT id, goal_name, target_amount, current_amount, target_date FROM financial_goals WHERE user_id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FinancialGoal goal = new FinancialGoal(
                        rs.getString("goal_name"),
                        rs.getDouble("target_amount"),
                        rs.getDouble("current_amount"),
                        LocalDate.parse(rs.getString("target_date"))
                );
                goal.setId(rs.getInt("id")); // Veritabanından gelen ID atanır
                goals.add(goal);
            }

        } catch (SQLException e) {
            System.err.println("Finansal hedefler alınırken hata oluştu: " + e.getMessage());
        }

        return goals;
    }

    // Aynı isimde hedef var mı kontrol eder
    public boolean exists(String goalName, int userId) {
        String sql = "SELECT COUNT(*) FROM financial_goals WHERE user_id = ? AND goal_name = ?";
        return recordExists(sql, userId, goalName);
    }
}
