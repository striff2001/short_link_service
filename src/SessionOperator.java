import javax.swing.plaf.multi.MultiSeparatorUI;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    // ЗАПИХНУТЬ ДВЕ ПРОВЕРКИ НА ЛИМИТ КЛИКОВ И НА ВРЕМЯ ЖИЗНИ.
    // ЕСЛИ ПРОВЕРКИ НЕ ПРОХОДЯТ -> ВЫВОДИМ СООБЩЕНИЕ, ЕСЛИ ОК ТО ОСТАВЛЯЕМ КАК ЕСТЬ СЕЙЧАС
    // НАПИСАТЬ ДВА МЕТОДА, КОТОРЫЕ ВЫКИНУТ ЭКСЕПШЕН ЕСЛИ ПРОВЕРКА НЕ ПРОЙДЕТ И ИСПОЛЬЗОВАТЬ ДАННЫЕ МЕТОДЫ КАК ПРОВЕРКИ
    // ЕСли вылетет эксепшен, то сделать метод по удалению ссылки из файла
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
        List<String> linkLine = this.loadLink(this.path, session.userID, session.link);
        //checkClicks(linkLine);
        String basicURL = linkLine.get(2);
        return basicURL;
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
        session.openInBrowser(url);
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

    private static void checkClicks(List<String> line) throws CustomException {
        if (Integer.parseInt(line.get(4)) > Integer.parseInt(line.get(3))) {
            throw new CustomException("Превышено число кликов");
        }
    }

    // Создаем собственное исключение
    static class CustomException extends Exception {
        public CustomException(String message) {
            super(message);
        }
    }



}

