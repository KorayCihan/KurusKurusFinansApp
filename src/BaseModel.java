

// Tüm model sınıflarının kalıtım alacağı temel sınıfımız

public abstract class BaseModel {
    protected int id; // Her modelde ortak olan ID alanı

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}