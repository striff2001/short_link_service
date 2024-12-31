import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class ShortLink {

    private final String url;
    private final String originalURL;
    private LocalDateTime creationDateTime;
    private int maxClicks;
    private int currentClicks = 0;

    public ShortLink(String hash, String originalURL, int maxClicks, int currentClicks, LocalDateTime creationDateTime) {
        this.url = "http://short.ly/" + hash;
        this.originalURL = originalURL;
        this.maxClicks = maxClicks;
        this.currentClicks = currentClicks;
        this.creationDateTime = creationDateTime;
    }

    public String getUrl() {
        return url;
    }

    public String getOriginalURL() {
        return originalURL;
    }

    public void setMaxClicks(int maxClicks) {
        this.maxClicks = maxClicks;
    }

    public int getMaxClicks() {
        return maxClicks;
    }

    public int getCurrentClicks() {
        return currentClicks;
    }

    public void setCurrentClicks(int currentClicks) {
        this.currentClicks = currentClicks;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationDateTime = creationTime;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }



//    public void saveToFile() {
//        String path = "./resources/links.txt";
//        try (FileWriter writer = new FileWriter(path, true)) {
//            writer.write(this.toCSV() + System.lineSeparator());
//        } catch (IOException e) {
//            System.err.println("Ошибка при сохранении в файл: " + e.getMessage());
//        }
//    }
//
//    // Метод для преобразования объекта в строку формата CSV
//    private String toCSV() {
//        return url + "," + originalURL + "," + maxClicks + "," + currentClicks + "," + creationDateTime;
//    }
}
