import java.awt.*;
import java.io.*;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.time.LocalDateTime;

public class urlShortener {
    //private static final String BASE_URL = "http://short.ly/";
//    private final Map<String, String> urlMapping = new HashMap<>();
//    private final Map<String, Integer> urlClicks = new HashMap<>();
//    private final Map<String, Boolean> urlBlocked = new HashMap<>();
//    private final Map<String, LocalDateTime> urlCreationTime = new HashMap<>();
    public static int counter = 0;
    //private int maxClicks;
    private long linkLifetimeMinutes;

//    public urlShortener() {
//        loadConfig();
//    }

    public static String createLink(UUID userID, String originalURL, int maxClicks) {
        LocalDateTime creationDateTime = LocalDateTime.now();
        int currentClicks = 0;
        String hash = genHash(userID + originalURL + maxClicks + creationDateTime);
        ShortLink shortLink = new ShortLink(hash, originalURL, maxClicks, currentClicks, creationDateTime);
        //shortLink.saveToFile();

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


////////////////////////////////////////////////////////////////
//    public String shortenUrl(String originalUrl) {
//        String shortUrl = BASE_URL + Integer.toHexString(counter);
//        urlMapping.put(shortUrl, originalUrl);
//        urlClicks.put(shortUrl, 0);
//        urlBlocked.put(shortUrl, false);
//        urlCreationTime.put(shortUrl, LocalDateTime.now());
//        counter++;
//        System.out.println("Link will be valid for " + linkLifetimeMinutes + " minutes.");
//        return shortUrl;
//    }
//
//    public String getOriginalUrl(String shortUrl) {
//        String response = "";
//        if (!urlMapping.containsKey(shortUrl)) {
//            response = "URL not found";
//        }
//
//
//        if (urlBlocked.getOrDefault(shortUrl, false)) {
//            response = "URL is blocked due to exceeding the maximum allowed clicks. Max clicks = " + maxClicks;
//        }
//
//        if (urlMapping.containsKey(shortUrl)) {
//            // Check if the link has expired
//            LocalDateTime creationTime = urlCreationTime.get(shortUrl);
//            if (creationTime != null && creationTime.plusMinutes(linkLifetimeMinutes).isBefore(LocalDateTime.now())) {
//                urlMapping.remove(shortUrl);
//                urlClicks.remove(shortUrl);
//                urlBlocked.remove(shortUrl);
//                urlCreationTime.remove(shortUrl);
//                response = "URL has expired and is no longer available.";
//            } else {
//                int clicks = urlClicks.getOrDefault(shortUrl, 0) + 1;
//                if (clicks > maxClicks) {
//                    urlBlocked.put(shortUrl, true);
//                    response = "URL is blocked due to exceeding the maximum allowed clicks. Max clicks = " + maxClicks;
//                } else {
//                    urlClicks.put(shortUrl, clicks);
//                    response = urlMapping.get(shortUrl);
//                }
//            }
//        }
//        return response;
//
//    }
//
//    public void openInBrowser(String url) {
//        try {
//            Desktop desktop = Desktop.getDesktop();
//            desktop.browse(new URI(url));
//        } catch (Exception e) {
//            System.out.println("Failed to open URL in browser: " + e.getMessage());
//        }
//    }
//
//    private void loadConfig() {
//        Properties properties = new Properties();
//        try (FileInputStream input = new FileInputStream("config.properties")) {
//            properties.load(input);
//            maxClicks = Integer.parseInt(properties.getProperty("max_clicks", "5"));
//            linkLifetimeMinutes = Integer.parseInt(properties.getProperty("link_lifetime_minutes", "10"));
//        } catch (IOException e) {
//            System.out.println("Failed to load config: " + e.getMessage());
//            maxClicks = 5; // Default value if config fails
//            linkLifetimeMinutes = 10; // Default value if config fails
//        }
//    }
//}

