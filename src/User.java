import java.util.ArrayList;
import java.util.List;

// Sistemde oturum açan kullanıcıyı temsil eder
public class User implements IUser {

    private int id;
    private String username;
    private String password;

    // Kullanıcının işlemleri, faturaları ve hedefleri
    private List<Transaction> transactions;
    private List<Bill> bills;
    private List<FinancialGoal> goals;

    // Veritabanından gelen kullanıcı için tam kurucu
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.transactions = new ArrayList<>();
        this.bills = new ArrayList<>();
        this.goals = new ArrayList<>();
    }

    // Yeni kullanıcı için kurucu
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.transactions = new ArrayList<>();
        this.bills = new ArrayList<>();
        this.goals = new ArrayList<>();
    }


    //getter - setter
    public int getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }


    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }


    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }


    public void addBill(Bill bill) {
        bills.add(bill);
    }


    public List<FinancialGoal> getGoals() {
        return goals;
    }

    public void setGoals(List<FinancialGoal> goals) {
        this.goals = goals;
    }


    public void addGoal(FinancialGoal goal) {
        goals.add(goal);
    }

    // Gelir - gider farkını hesaplar
    public double getNetBalance() {
        double income = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("Gelir"))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("Gider"))
                .mapToDouble(Transaction::getAmount)
                .sum();

        return income - expense;
    }

    // Ödenmemiş fatura sayısını döner
    public long getUnpaidBillCount() {
        return bills.stream()
                .filter(b -> !b.isPaid())
                .count();
    }
}
