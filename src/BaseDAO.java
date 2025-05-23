import java.util.List;

// Tüm DAO sınıflarının uyması gereken temel arayüz
public interface BaseDAO<T extends BaseModel> {

    // Yeni kayıt ekleme
    boolean add(T t, int userId);

    // Mevcut kaydı günceller
    boolean update(T t, int userId);

    // Belirli bir kaydı siler
    boolean delete(String key, int userId);

    // Belirli kullanıcıya ait tüm kayıtları getirir
    List<T> getAllByUserId(int userId);
}
