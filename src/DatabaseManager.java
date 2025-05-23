import java.sql.*;
import java.time.LocalDate;
import java.util.Map;

// Veritabanı tablolarını oluşturur ve temel ekleme işlemlerini yapar
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:finansapp.db";

    // Veritabanı bağlantısı kurar
    public Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        System.out.println("✅ Veritabanına bağlanıldı: " + DB_URL);
        return conn;
    }

    // Uygulama için gerekli tüm tabloları oluşturur
    public void createTables() {

        // Kullanıcı tablosu
        String userTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "password TEXT NOT NULL);";

        // Gelir/Gider işlemleri tablosu
        String transactionTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "type TEXT, " +
                "category TEXT, " +
                "amount REAL, " +
                "date TEXT, " +
                "description TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE);";

        // Fatura tablosu
        String billTable = "CREATE TABLE IF NOT EXISTS bills (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "bill_name TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "due_date TEXT NOT NULL, " +
                "is_paid INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE);";

        // Finansal hedefler tablosu
        String goalTable = "CREATE TABLE IF NOT EXISTS financial_goals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "goal_name TEXT NOT NULL, " +
                "target_amount REAL NOT NULL, " +
                "current_amount REAL DEFAULT 0.0, " +
                "target_date TEXT NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE);";

        // Aylık bütçeler tablosu
        String budgetTable = "CREATE TABLE IF NOT EXISTS budgets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "month TEXT NOT NULL, " +
                "total_income REAL DEFAULT 0.0, " +
                "total_expense REAL DEFAULT 0.0, " +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE);";

        // Bütçe kategorileri tablosu
        String categoryTable = "CREATE TABLE IF NOT EXISTS budget_categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "budget_id INTEGER NOT NULL, " +
                "category TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "FOREIGN KEY(budget_id) REFERENCES budgets(id) ON DELETE CASCADE);";

        // Tüm tabloları sırayla oluşturur
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(transactionTable);
            stmt.execute(billTable);
            stmt.execute(goalTable);
            stmt.execute(budgetTable);
            stmt.execute(categoryTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kullanıcı ekler ve yeni ID'yi döner
    public int insertUser(User user) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        int userId = -1;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                userId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    // Yeni işlem ekler
    public void insertTransaction(Transaction t, int userId) {
        String sql = "INSERT INTO transactions(user_id, type, category, amount, date, description) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, t.getType());
            pstmt.setString(3, t.getCategory());
            pstmt.setDouble(4, t.getAmount());
            pstmt.setString(5, t.getDate().toString());
            pstmt.setString(6, t.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Yeni fatura ekler
    public void insertBill(Bill bill, int userId) {
        String sql = "INSERT INTO bills(user_id, bill_name, amount, due_date, is_paid) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, bill.getBillName());
            pstmt.setDouble(3, bill.getAmount());
            pstmt.setString(4, bill.getDueDate().toString());
            pstmt.setInt(5, bill.isPaid() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Yeni hedef ekler
    public void insertGoal(FinancialGoal goal, int userId) {
        String sql = "INSERT INTO financial_goals(user_id, goal_name, target_amount, current_amount, target_date) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, goal.getGoalName());
            pstmt.setDouble(3, goal.getTargetAmount());
            pstmt.setDouble(4, goal.getCurrentAmount());
            pstmt.setString(5, goal.getTargetDate().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Yeni bütçe ekler ve bütçe ID'sini döner
    public int insertBudget(int userId, String month, double income, double expense) {
        String sql = "INSERT INTO budgets(user_id, month, total_income, total_expense) VALUES(?, ?, ?, ?)";
        int budgetId = -1;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, month);
            pstmt.setDouble(3, income);
            pstmt.setDouble(4, expense);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                budgetId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgetId;
    }

    // Bütçeye kategori gideri ekler
    public void insertBudgetCategory(int budgetId, String category, double amount) {
        String sql = "INSERT INTO budget_categories(budget_id, category, amount) VALUES(?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, budgetId);
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
