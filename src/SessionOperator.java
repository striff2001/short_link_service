import javax.swing.plaf.multi.MultiSeparatorUI;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/* Класс который выполняет действия с определенной ссылкой определенного пользователя
    1. Нахождение строки в файле с ссылками по заданным userID и url (короткая ссылка)
    2. Переход в браузер по полной ссылке на сайт
    3. Увеличение счетчика для ссылки
    4. Проверка на время жизни ссылки (удаление ссылки в случае провала проверки)
    5. Проверка на допустимое количество переходов по ссылке (удаление ссылки в случае провала проверки)
*/

public class SessionOperator {
    String userID;
    String link;
    String path = "./resources/links.txt";

    public SessionOperator(String userID, String link) {
        this.userID = userID;
        this.link = link;
    }

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

    public String getBasicURL(SessionOperator session) {
        try {
            List<String> linkLine = this.loadLink(this.path, session.userID, session.link);

            // Проверка ссылки на лимиты
            checkClicks(linkLine);
            checkLinkLifetime(linkLine);

            String basicURL = linkLine.get(2);
            return basicURL;
        } catch (CustomException e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    public void openInBrowser(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        } catch (Exception e) {
            System.out.println("Failed to open URL in browser: " + e.getMessage());
        }
    }

    public void redirectFromLinkToBasic(SessionOperator session) {
            String url = session.getBasicURL(session);
            if (!url.startsWith("Ошибка")) {
                session.openInBrowser(url);
            }
    }

    public boolean addClick(String userID, String link) {
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(this.path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(",");
                if (elements.length >= 4 && elements[0].trim().equals(userID) && elements[1].trim().equals(link)) {
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

//    private static void checkClicks(String[] line) throws CustomException {
//        if (Integer.parseInt(line[4]) > Integer.parseInt(line[3])) {
//            throw new CustomException("Превышено число кликов");
//        }
//    }

    public void deleteLink(String userID, String link) {
        List<String> lines = new ArrayList<>();
        boolean removed = false;

        // Чтение файла и поиск строки
        try (BufferedReader reader = new BufferedReader(new FileReader(this.path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(",");
                if (elements.length >= 4 && elements[0].trim().equals(userID) && elements[1].trim().equals(link)) {
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

        //return "";
    }

    public boolean removeLine(String userID, String link) {
        List<String> lines = new ArrayList<>();
        boolean removed = false;

        // Чтение файла и поиск строки
        try (BufferedReader reader = new BufferedReader(new FileReader(this.path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(",");
                if (elements.length >= 4 && elements[0].trim().equals(userID) && elements[1].trim().equals(link)) {
                    removed = true; // Помечаем строку как найденную
                    continue;       // Пропускаем добавление строки в список
                }
                lines.add(line); // Добавляем только строки, которые не удаляются
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return false;
        }

        // Перезапись файла без удалённой строки
        if (removed) {
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

        return removed;
    }

    private static void checkClicks(List<String> line) throws CustomException {
        if (Integer.parseInt(line.get(4)) > Integer.parseInt(line.get(3))) {
            throw new CustomException("Превышено число кликов");
        }
    }

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

    // Создаем собственное исключение
    static class CustomException extends Exception {
        public CustomException(String message) {
            super(message);
        }
    }

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

