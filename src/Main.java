import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        User user1 = new User();
        user1.saveToFile();
        UUID id = user1.getUserID();

        String URL = "https://chatgpt.com/c/67725d00-1720-8009-8ed3-ca94300df9e5";

        String link = urlShortener.createLink(id, URL, 2);
        System.out.println(link);

        SessionOperator session = new SessionOperator("e68eadab-11a2-40b3-b682-95b05946cb0f", "http://short.ly/N1FzodI");
        String basicURL = session.getBasicURL(session);
        System.out.println(basicURL);
        session.redirectFromLinkToBasic(session);
        session.addClick("e68eadab-11a2-40b3-b682-95b05946cb0f", "http://short.ly/N1FzodI");

//        urlShortener urlShortener = new urlShortener();
//        Scanner scanner = new Scanner(System.in);
//
//        while (true) {
//            System.out.println("Choose an option:");
//            System.out.println("1. Shorten a URL");
//            System.out.println("2. Retrieve original URL");
//            System.out.println("3. Exit");
//            System.out.print("Enter your choice: ");
//
//            int choice = scanner.nextInt();
//            scanner.nextLine();
//
//            switch (choice) {
//                case 1:
//                    System.out.print("Enter the original URL: ");
//                    String originalUrl = scanner.nextLine();
//                    String shortUrl = urlShortener.shortenUrl(originalUrl);
//                    System.out.println("Shortened URL: " + shortUrl);
//                    break;
//                case 2:
//                    System.out.print("Enter the shortened URL: ");
//                    String inputShortUrl = scanner.nextLine();
//                    String retrievedUrl = urlShortener.getOriginalUrl(inputShortUrl);
//                    if (!retrievedUrl.startsWith("URL is blocked") &&
//                            !retrievedUrl.startsWith("URL has expired") &&
//                            !"URL not found".equals(retrievedUrl)) {
//                        System.out.println("Opening original URL in browser...");
//                        urlShortener.openInBrowser(retrievedUrl);
//                    }
//                    System.out.println("Original URL: " + retrievedUrl);
//                    break;
//                case 3:
//                    System.out.println("Exiting...");
//                    scanner.close();
//                    return;
//                default:
//                    System.out.println("Invalid choice. Please try again.");
//            }
//        }
    }
}
