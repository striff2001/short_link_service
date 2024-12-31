import java.awt.*;
import java.io.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/*
Класс представляющий оператор сессии конкретного пользователя с конкретной ссылкой.
Выполняет основные функции сервиса.
    userID : String - идентификатор пользователя
    link : String - короткая ссылка
    path : String - путь к файлу, где хранятся все ссылки пользователей
Содержит вложенный класс, который представляет из себя пользовательское исключение для обработки лимитов для короткой ссылки.
*/

public class SessionOperator {
    String userID;
    String link;
    String path = "./resources/links.txt";

    public SessionOperator(String userID, String link) {
        this.userID = userID;
        this.link = link;
    }

    // Нахождение короткой ссылки (строки в файле с ссылками) по комбинации userID + link
    public static List<String> loadLink(String path, String userID, String link) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(","); // Разделяем строку на элементы
                if (elements.length >= 2 && elements[0].trim().equals(userID) && elements[1].trim().equals(link)) {
                    return Arrays.asList(elements); // Возвращаем найденную строку как список
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
        return Collections.emptyList(); // Возвращаем пустой список, если ничего не найдено
    }

    // Получение полного URL-адреса который записан за конкретной короткой ссылкой
    public String getBasicURL(SessionOperator session) {
        try {
            List<String> linkLine = this.loadLink(this.path, session.userID, session.link);

            checkListFormat(linkLine);
            // Проверка ссылки на лимиты
            checkClicks(linkLine);
            checkLinkLifetime(linkLine);

            String basicURL = linkLine.get(2);
            return basicURL;
        } catch (CustomException e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    // Переход на страницу в браузере по полному URL-адресу
    public void openInBrowser(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        } catch (Exception e) {
            System.out.println("Failed to open URL in browser: " + e.getMessage());
        }
    }

    // Перенаправление пользователя в браузер по короткой ссылке
    public boolean redirectFromLinkToBasic(SessionOperator session) {
            String url = session.getBasicURL(session);
            if (!url.startsWith("Ошибка")) {
                session.openInBrowser(url);
                return true;
            } else {
                System.out.println(url);
                deleteLink(this.userID, this.link);
                return false;
            }
    }

    // Автоматическое увеличение счетчика переходов по ссылке на единицу (поле currentClicks в файле с ссылками)
    public boolean addClick(String userID, String link) {
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(this.path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(",");
                if (elements[0].trim().equals(userID) && elements[1].trim().equals(link)) {
                    try {
                        int value = Integer.parseInt(elements[4].trim());
                        elements[4] = String.valueOf(value + 1); // Увеличиваем значение на единицу
                        updated = true;
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка преобразования числа в строке: " + line);
                    }
                }
                lines.add(String.join(",", elements)); // Добавляем обновленную или оригинальную строку в список
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return false;
        }

        // Перезаписываем файл с обновленными данными
        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.path))) {
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Ошибка при записи в файл: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("Строка с указанными параметрами не найдена.");
        }

        return updated;
    }

    // Метод ручного изменения текущего лимита переходов для конкретной ссылки
    public boolean setNewMaxClicks(String userID, String link, int newMaxClicks) {
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(this.path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(",");
                if (elements[0].trim().equals(userID) && elements[1].trim().equals(link)) {
                    try {
                        //int value = Integer.parseInt(elements[4].trim());
                        elements[3] = String.valueOf(newMaxClicks); // Увеличиваем значение на единицу
                        updated = true;
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка преобразования числа в строке: " + line);
                    }
                }
                lines.add(String.join(",", elements)); // Добавляем обновленную или оригинальную строку в список
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return false;
        }

        // Перезаписываем файл с обновленными данными
        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.path))) {
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
                System.out.println("Лимит переходов обновлен.");
            } catch (IOException e) {
                System.err.println("Ошибка при записи в файл: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("Строка с указанными параметрами не найдена.");
        }

        return updated;
    }

    // Метод для удаления ссылки. Вызывается, как автоматически при достижении лимита переходов или времени жизни ссылки,
    // так и в ручную, пользователем, при желании удалить ссылку
    public void deleteLink(String userID, String link) {
        List<String> lines = new ArrayList<>();
        boolean removed = false;

        // Чтение файла и поиск строки
        try (BufferedReader reader = new BufferedReader(new FileReader(this.path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(",");
                if (elements[0].trim().equals(userID) && elements[1].trim().equals(link)) {
                    removed = true; // Помечаем строку как найденную
                    continue;       // Пропускаем добавление строки в список
                }
                lines.add(line); // Добавляем только строки, которые не удаляются
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }

        // Перезапись файла без удалённой строки
        if (removed) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.path))) {
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
                System.out.println("Ссылка удалена!");
            } catch (IOException e) {
                System.err.println("Ошибка при записи в файл: " + e.getMessage());
            }
        } else {
            System.out.println("Строка с указанными параметрами не найдена.");
        }
    }

    // Проверка ссылки на допустимое количество переходов.
    // Если лимит превышен, выбрасывает кастомное исключение
    private static void checkClicks(List<String> line) throws CustomException {
        if (Integer.parseInt(line.get(4)) >= Integer.parseInt(line.get(3))) {
            throw new CustomException("Превышено число кликов");
        }
    }

    // Проверка ссылки на истечение срока жизни.
    // Если срок жизни закончился, выбрасывает кастомное исключение
    private static void checkLinkLifetime(List<String> line) throws CustomException {
        int timeDiff = SessionOperator.loadConfig();

        // create a formatter
        DateTimeFormatter formatter
                = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        LocalDateTime linkLifetime = LocalDateTime.parse(line.get(5), formatter);
        linkLifetime = linkLifetime.plusMinutes(timeDiff); // Перевести параметр интервала жизни в конфиг файл

        if (linkLifetime.isBefore(LocalDateTime.now())) {
            throw new CustomException("Время действия ссылки истекло");
        }
    }

    // Проверка корректного формата записи объекта короткой ссылки в файле,
    // чтобы при парсинге и обработке записи из файла на вход не пришла некорректная запись
    private static void checkListFormat(List<String> line) throws CustomException {
        if (line.size() != 6) {
            throw new CustomException("Запись не найдена");
        }
    }

    // Создаем собственное исключение, для обработки условий действия ссылки
    static class CustomException extends Exception {
        public CustomException(String message) {
            super(message);
        }
    }

    // Загрузка конфигурационного файла и получение лимита жизни для всех коротких ссылок
    private static Integer loadConfig() {
       // int maxClicks;
        int linkLifetimeMinutes;

        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            //maxClicks = Integer.parseInt(properties.getProperty("max_clicks", "5"));
            linkLifetimeMinutes = Integer.parseInt(properties.getProperty("link_lifetime_minutes", "10"));
        } catch (IOException e) {
            System.out.println("Failed to load config: " + e.getMessage());
          //  maxClicks = 5; // Default value if config fails
            linkLifetimeMinutes = 10; // Default value if config fails
        }
        return linkLifetimeMinutes;
    }

}

