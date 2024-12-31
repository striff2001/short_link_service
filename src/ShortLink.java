import java.time.LocalDateTime;
/*
Класс представляющий сущность короткой ссылки
    url : String - короткая ссылка
    originalURL : String - полная ссылка на сайт которая кодируется короткой ссылкой
    creationDateTime : LocalDateTime - временная метка создания короткой ссылки
    maxClicks : int - лимит переходов по короткой ссылке
    currentClicks : int - текущее количество переходов по короткой ссылке
 */
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
}
