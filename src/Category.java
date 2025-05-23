// Harcama veya gelir kategorilerini temsil eden sınıf
public class Category extends BaseModel {

    private String name;
    private String description;

    // Kategori adı ve açıklamasıyla oluşturulur
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getter ve setter metodları
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Kategori bilgisi yazdırılırken açıklama varsa parantez içinde gösterilir
    public String toString() {
        return name + (description != null && !description.isEmpty() ? " (" + description + ")" : "");
    }
}
