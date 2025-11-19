package se.sprinto.hakan.chatapp;

import org.junit.jupiter.api.Test;
import se.sprinto.hakan.chatapp.dao.UserDatabaseDAO;
import se.sprinto.hakan.chatapp.model.User;
import se.sprinto.hakan.chatapp.util.DatabaseUtil;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserDatabaseTestDAO {

    @Test
    public void loginTest() {
        // ARRANGE
        UserDatabaseDAO dbDAO = new UserDatabaseDAO();
        User testUser = new User("Tester", "123456");
        dbDAO.register(testUser);

        // ACT
        User loginOk = dbDAO.login("Tester", "123456");
        User loginFail = dbDAO.login("Tester", "654321");

        // ASSERT
        assertNotNull(loginOk, "Du borde loggats in...");
        assertNull(loginFail, "Detta borde inte funkat...");
    }
}
