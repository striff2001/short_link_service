import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.time.LocalDateTime;

public class urlShortener {
    private static final String BASE_URL = "http://short.ly/";
    private final Map<String, String> urlMapping = new HashMap<>();
    private final Map<String, Integer> urlClicks = new HashMap<>();
    private final Map<String, Boolean> urlBlocked = new HashMap<>();
    private final Map<String, LocalDateTime> urlCreationTime = new HashMap<>();
    private int counter = 1;
    private int maxClicks;
    private long linkLifetimeMinutes;

    public urlShortener() {
        loadConfig();
    }

    private void loadConfig() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            maxClicks = Integer.parseInt(properties.getProperty("max_clicks", "5"));
            linkLifetimeMinutes = Integer.parseInt(properties.getProperty("link_lifetime_minutes", "10"));
        } catch (IOException e) {
            System.out.println("Failed to load config: " + e.getMessage());
            maxClicks = 5; // Default value if config fails
            linkLifetimeMinutes = 10; // Default value if config fails
        }
    }

    public String shortenUrl(String originalUrl) {
        String shortUrl = BASE_URL + Integer.toHexString(counter);
        urlMapping.put(shortUrl, originalUrl);
        urlClicks.put(shortUrl, 0);
        urlBlocked.put(shortUrl, false);
        urlCreationTime.put(shortUrl, LocalDateTime.now());
        counter++;
        System.out.println("Link will be valid for " + linkLifetimeMinutes + " minutes.");
        return shortUrl;
    }

    public String getOriginalUrl(String shortUrl) {
        String response = "";
        if (!urlMapping.containsKey(shortUrl)) {
            response = "URL not found";
        }


        if (urlBlocked.getOrDefault(shortUrl, false)) {
            response = "URL is blocked due to exceeding the maximum allowed clicks. Max clicks = " + maxClicks;
        }

        if (urlMapping.containsKey(shortUrl)) {
            // Check if the link has expired
            LocalDateTime creationTime = urlCreationTime.get(shortUrl);
            if (creationTime != null && creationTime.plusMinutes(linkLifetimeMinutes).isBefore(LocalDateTime.now())) {
                urlMapping.remove(shortUrl);
                urlClicks.remove(shortUrl);
                urlBlocked.remove(shortUrl);
                urlCreationTime.remove(shortUrl);
                response = "URL has expired and is no longer available.";
            } else {
                int clicks = urlClicks.getOrDefault(shortUrl, 0) + 1;
                if (clicks > maxClicks) {
                    urlBlocked.put(shortUrl, true);
                    response = "URL is blocked due to exceeding the maximum allowed clicks. Max clicks = " + maxClicks;
                } else {
                    urlClicks.put(shortUrl, clicks);
                    response = urlMapping.get(shortUrl);
                }
            }
        }
        return response;

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

