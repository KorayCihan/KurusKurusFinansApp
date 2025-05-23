// Oturum bilgisini yöneten yardımcı sınıf
public class Session {

    // Şu anda oturum açmış kullanıcı
    private static IUser currentUser;

    // Oturumu başlatır (kullanıcıyı kaydeder)
    public static void setCurrentUser(IUser user) {
        currentUser = user;
    }

    // Mevcut oturumdaki kullanıcıyı döner
    public static IUser getCurrentUser() {
        return currentUser;
    }

    // Oturumu sonlandırır (kullanıcıyı sıfırlar)
    public static void logout() {
        currentUser = null;
    }
}
