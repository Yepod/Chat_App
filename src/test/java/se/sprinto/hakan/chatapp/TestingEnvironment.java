package se.sprinto.hakan.chatapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.sprinto.hakan.chatapp.dao.MessageDatabaseDAO;
import se.sprinto.hakan.chatapp.dao.UserDatabaseDAO;
import se.sprinto.hakan.chatapp.model.Message;
import se.sprinto.hakan.chatapp.model.User;
import se.sprinto.hakan.chatapp.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestingEnvironment {

    @BeforeEach
    void setup() throws SQLException {
        // SETTING UP CONNECTION TO H2
        Connection connection = DatabaseUtil.getInstance().getConnection();

        try(Statement stmt = connection.createStatement()) {
            // SKAPAR TABELLERNA (ARRANGE)
            stmt.execute("CREATE TABLE user (" +
                    "user_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(20) NOT NULL," +
                    "password VARCHAR(20) NOT NULL" + ");");

            stmt.execute("CREATE TABLE message (" +
                    "message_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "text TEXT NOT NULL," +
                    "timestamp TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES user(user_id)" +
                    ");");
        }
    }

    @Test
    void testRegisterUserAndMessages() {
        // Arrange
        UserDatabaseDAO userDAO = new UserDatabaseDAO();
        MessageDatabaseDAO messageDAO = new MessageDatabaseDAO();

        // ACT
        User testUser = new User("SuperHåkan", "HackerLord");
        userDAO.register(testUser);

        int userId = testUser.getId();
        Message message1 = new Message(userId, "Tjena klassen, idag ska det testas!", LocalDateTime.now());
        Message message2 = new Message(userId, "Tror vi på detta eller?", LocalDateTime.now());
        messageDAO.saveMessage(message1);
        messageDAO.saveMessage(message2);

        // ASSERT
        assertEquals(2, messageDAO.getMessagesByUserId(userId).size());
    }
}
