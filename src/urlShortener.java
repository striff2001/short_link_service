import java.awt.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class urlShortener {
    private static final String BASE_URL = "http://short.ly/";
    private final Map<String, String> urlMapping = new HashMap<>();
    private int counter = 1;


    public String shortenUrl(String originalUrl) {
        String shortUrl = BASE_URL + Integer.toHexString(counter);
        urlMapping.put(shortUrl, originalUrl);
        counter++;
        return shortUrl;
    }

    public String getOriginalUrl(String shortUrl) {
        return urlMapping.getOrDefault(shortUrl, "URL not found");
    }

    public void openInBrowser(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        } catch (Exception e) {
            System.out.println("Failed to open URL in browser: " + e.getMessage());
        }
    }
}
