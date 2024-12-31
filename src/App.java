import java.util.Scanner;
import java.util.UUID;
/*
Класс для работы с пользователем в консоли.
Представляет разные сценарии использования программы для пользователя
и обращается к соответствующим классам и методам для обработки данных.
 */
public class App {
    public static void startApp() {
        Scanner scanner = new Scanner(System.in);
        String userID = "";
        String link = "";
        boolean auth = false;

        while (!auth) {
            System.out.println("""
                    Здравствуйте! Вы зарегистрированы в сервисе?
                    1. Зарегистрирован
                    2. Нет, я в первый раз
                    3. Завершить работу
                    """);
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Введите свой идентификатор: ");
                    userID = scanner.nextLine();
                    System.out.println("Ищем Вас в системе...");

                    boolean found = User.findUser(userID);
                    if (found) {
                        auth = true;
                        break;
                    } else {
                        System.out.println("Мы Вас не смогли найти, поэтому создали новый аккаунт");
                        User newUser = new User();
                        newUser.saveToFile();
                        userID = String.valueOf(newUser.getUserID());
                        System.out.println("Ваш идентификатор: " + userID);
                        System.out.println("Запомните его для дальнейшей работы с аккаунтом.");
                        auth = true;
                        break;
                    }
                case 2:
                    User newUser = new User();
                    newUser.saveToFile();
                    userID = String.valueOf(newUser.getUserID());
                    System.out.println("Ваш идентификатор: " + userID);
                    System.out.println("Запомните его для дальнейшей работы с аккаунтом.");
                    auth = true;
                    break;
                case 3:
                    System.out.println("Завершение работы...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Недоступная команда. Попробуйте еще раз.");
            }
        }

        while (true) {
            System.out.println("""
                    Выберите действие:
                    1. Создать короткую ссылку.
                    2. Перейти на страницу по короткой ссылке.
                    3. Удалить короткую ссылку.
                    4. Изменить лимит переходов по ссылке
                    5. Завершить работу.
                    """);
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Введите URL для генерации короткой ссылки: ");
                    String url = scanner.nextLine();
                    System.out.println("Введите лимит переходов по ссылке: ");
                    int maxClicks = scanner.nextInt();

                    link = UrlShortener.createLink(UUID.fromString(userID), url, maxClicks);
                    System.out.println("Ваша короткая ссылка: " + link);
                    break;
                case 2:
                    System.out.println("Введите короткую ссылку: ");
                    link = scanner.nextLine();

                    SessionOperator redirectSession = new SessionOperator(String.valueOf(userID), link);
                    boolean res =  redirectSession.redirectFromLinkToBasic(redirectSession);
                    if (res) {
                        redirectSession.addClick(redirectSession.userID, redirectSession.link);
                    }
                    break;
                case 3:
                    System.out.println("Введите короткую ссылку: ");
                    link = scanner.nextLine();

                    SessionOperator deleteSession = new SessionOperator(String.valueOf(userID), link);
                    deleteSession.deleteLink(deleteSession.userID, deleteSession.link);
                    break;
                case 4:
                    System.out.println("Введите короткую ссылку: ");
                    link = scanner.nextLine();
                    System.out.println("Введите новый лимит переходов для ссылки: ");
                    int newMaxClicks = scanner.nextInt();
                    SessionOperator clicksSetterSession = new SessionOperator(String.valueOf(userID), link);
                    clicksSetterSession.setNewMaxClicks(clicksSetterSession.userID, clicksSetterSession.link, newMaxClicks);
                    break;
                case 5:
                    System.out.println("Завершение работы...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Недоступная команда. Попробуйте еще раз.");

            }
        }
    }
}
