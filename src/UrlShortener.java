import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.time.LocalDateTime;
/*
Класс представляющий генератор коротких ссылок.
Предоставляет набор статических методов:
    - Создание короткой ссылки
    - Сохранение объекта короткая ссылка со всем набором полей в файл
    - Генерация хэш значения
 */
public class UrlShortener {

    public static String createLink(UUID userID, String originalURL, int maxClicks) {
        LocalDateTime creationDateTime = LocalDateTime.now();
        int currentClicks = 0;
        String hash = genHash(userID + originalURL + maxClicks + creationDateTime);
        ShortLink shortLink = new ShortLink(hash, originalURL, maxClicks, currentClicks, creationDateTime);

        String link = shortLink.getUrl();
        saveUserLinkToFile(userID, shortLink);

        return shortLink.getUrl();
    }

    public static void saveUserLinkToFile(UUID userID, ShortLink shortLink) {
        String path = "./resources/links.txt";
        String url = shortLink.getUrl();
        String originalURL = shortLink.getOriginalURL();
        int maxClicks = shortLink.getMaxClicks();
        int currentClicks = shortLink.getCurrentClicks();
        LocalDateTime creationDateTime = shortLink.getCreationDateTime();

        try (FileWriter writer = new FileWriter(path, true)) {
            writer.write(userID + "," + url + "," + originalURL + "," + maxClicks + "," + currentClicks + "," + creationDateTime + "," + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    private static String genHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(input.getBytes());
            String hash = Base64.getUrlEncoder().encodeToString(hashInBytes);

            return hash.substring(0, 7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
}
