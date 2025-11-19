package se.sprinto.hakan.chatapp;

import org.junit.jupiter.api.Test;
import se.sprinto.hakan.chatapp.dao.MessageDatabaseDAO;
import se.sprinto.hakan.chatapp.dao.UserDatabaseDAO;
import se.sprinto.hakan.chatapp.model.Message;
import se.sprinto.hakan.chatapp.model.User;

import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MessageDatabaseTestDAO {

    @Test
    void getMessageByUserIdTest(){
        // ARRANGE
        UserDatabaseDAO userDbDAO = new UserDatabaseDAO();
        MessageDatabaseDAO messageDbDAO = new MessageDatabaseDAO();

        User existingUser = userDbDAO.findByUsername("Tester");
        int userId = existingUser.getId();

        Message msg = new Message(
                userId,
                "Well, does this even work?",
                LocalDateTime.now()
        );
        messageDbDAO.saveMessage(msg);

        // ACT
        List<Message> messages = messageDbDAO.getMessagesByUserId(userId);

        // ASSERT
        assertFalse(messages.isEmpty(), "Should be atleast one message here...");
        assertEquals("Well, does this even work?", messages.getFirst().getText());
        assertEquals(userId, messages.getFirst().getUserId());
    }
}
