import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    UUID userID;
    LocalDateTime userCreationDT;

    public User() {
        this.userID = UUID.randomUUID();
        this.userCreationDT = LocalDateTime.now();
    }

    public UUID getUserID() {
        return userID;
    }

    public void saveToFile() {
        String path = "./resources/users.txt";
        try (FileWriter writer = new FileWriter(path, true)) {
            writer.write(userID + "," + userCreationDT + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }
}