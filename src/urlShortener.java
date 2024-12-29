import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class urlShortener {
    private static final String BASE_URL = "http://short.ly/";
    private final Map<String, String> urlMapping = new HashMap<>();
    private final Map<String, Integer> urlClicks = new HashMap<>();
    private final Map<String, Boolean> urlBlocked = new HashMap<>();
    private int counter = 1;
    private int maxClicks;

    public urlShortener() {
        loadConfig();
    }

    private void loadConfig() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            maxClicks = Integer.parseInt(properties.getProperty("max_clicks", "5"));
        } catch (IOException e) {
            System.out.println("Failed to load config: " + e.getMessage());
            maxClicks = 5; // Default value if config fails
        }
    }

    public String shortenUrl(String originalUrl) {
        String shortUrl = BASE_URL + Integer.toHexString(counter);
        urlMapping.put(shortUrl, originalUrl);
        urlClicks.put(shortUrl, 0);
        urlBlocked.put(shortUrl, false);
        counter++;
        return shortUrl;
    }

    public String getOriginalUrl(String shortUrl) {
        if (urlBlocked.getOrDefault(shortUrl, false)) {
            return "URL is blocked due to exceeding the maximum allowed clicks. Max clicks = " + maxClicks;
        }

        if (urlMapping.containsKey(shortUrl)) {
            int clicks = urlClicks.getOrDefault(shortUrl, 0) + 1;
            if (clicks > maxClicks) {
                urlBlocked.put(shortUrl, true);
                return "URL is blocked due to exceeding the maximum allowed clicks. Max clicks = " + maxClicks;
            }
            urlClicks.put(shortUrl, clicks);
            return urlMapping.get(shortUrl);
        }

        return "URL not found";
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
