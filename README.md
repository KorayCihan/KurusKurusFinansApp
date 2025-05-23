Bu proje, JavaFX kullanılarak geliştirilmiş kişisel finans yönetim uygulamasıdır.
Uygulama, kullanıcıların gelir/gider, faturalar, finansal hedefler gibi verilerini yönetmesini sağlar.

GEREKSİNİMLER
-------------
- Java JDK  
- IntelliJ IDEA (veya başka bir IDE)
- JavaFX SDK
- SQLite JDBC (sqlite-jdbc-<version>.jar)

KURULUM ADIMLARI


1. JavaFX SDK Kurulumu:
   - Herhangi bir yerden JavaFX SDK'yı indirin.
   - SDK'yı bir klasöre çıkarın (örneğin: C:\javafx-sdk-20).

2. IntelliJ IDEA Ayarları:
   - File > Project Structure > Libraries sekmesinden javafx-sdk/lib klasörünü projeye ekleyin.

3. VM Options Ayarı:
   - Run > Edit Configurations kısmında VM options alanına aşağıdaki satırı ekleyin:

     --module-path "C:\javafx-sdk-20\lib" --add-modules javafx.controls,javafx.fxml

   - NOT: JavaFX yolunu kendi bilgisayarınızdaki dizine göre ayarlayın.

4. SQLite JDBC Kurulumu:
   - sqlite-jdbc-<version>.jar dosyasını projeye kütüphane olarak ekleyin (Project Structure > Libraries).

5. Projeyi Çalıştırma:
   - LoginRegisterScreen.java içindeki main fonksiyonu çalıştırılır.
   - Açılan giriş ekranından kullanıcı oluşturabilir ya da giriş yapılabilir.
   - Girişten sonra ana kontrol paneline yönlendirilirsiniz.

------
- Veritabanı olarak finansapp.db dosyası kullanılır. Otomatik olarak oluşturulur.
- Stiller style.css ile sağlanır.
- Görseller ve ikonlar /src klasöründe yer almalıdır.