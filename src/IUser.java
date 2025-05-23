

import java.util.List;

/**
 * IUser arayüzü, kullanıcı ile ilgili temel metotları tanımlar.
 * Böylece farklı kullanıcı sınıfları bu interface'i implement ederek
 * ortak davranışa sahip olur.
 */
public interface IUser {

    int getId();

    // Kullanıcı adı al
    String getUsername();

    // Kullanıcı adı belirle
    void setUsername(String username);

    // Şifre al
    String getPassword();


    // Kullanıcının işlemlerini al
    List<Transaction> getTransactions();

    // Yeni işlem ekle
    void addTransaction(Transaction transaction);

    // Kullanıcının faturalarını al
    List<Bill> getBills();

    // Yeni fatura ekle
    void addBill(Bill bill);

    // Kullanıcının finansal hedeflerini al
    List<FinancialGoal> getGoals();

    // Yeni finansal hedef ekle
    void addGoal(FinancialGoal goal);

    // Kullanıcının karını bakiyesini hesapla
    double getNetBalance();

    // Ödenmemiş fatura sayısını al
    long getUnpaidBillCount();
}