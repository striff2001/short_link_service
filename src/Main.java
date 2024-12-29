import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        urlShortener urlShortener = new urlShortener();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Shorten a URL");
            System.out.println("2. Retrieve original URL");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter the original URL: ");
                    String originalUrl = scanner.nextLine();
                    String shortUrl = urlShortener.shortenUrl(originalUrl);
                    System.out.println("Shortened URL: " + shortUrl);
                    break;
                case 2:
                    System.out.print("Enter the shortened URL: ");
                    String inputShortUrl = scanner.nextLine();
                    String retrievedUrl = urlShortener.getOriginalUrl(inputShortUrl);
                    if (!retrievedUrl.startsWith("URL is blocked") && !"URL not found".equals(retrievedUrl)) {
                        System.out.println("Opening original URL in browser...");
                        urlShortener.openInBrowser(retrievedUrl);
                    }
                    System.out.println("Original URL: " + retrievedUrl);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
