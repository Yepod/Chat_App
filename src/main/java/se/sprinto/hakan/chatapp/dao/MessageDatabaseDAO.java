package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.model.Message;
import se.sprinto.hakan.chatapp.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDatabaseDAO implements MessageDAO{

    @Override
    public void saveMessage(Message message) {
        String sql = "INSERT INTO message (user_id, text, timestamp) VALUES (?,?,?)";

        try(Connection connection = DatabaseUtil.getInstance().getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setInt(1, message.getUserId());
            pstmt.setString(2,message.getText());
            pstmt.setTimestamp(3, Timestamp.valueOf(message.getTimestamp()));

            int savedMessages = pstmt.executeUpdate();
            System.out.println(savedMessages + " message added to database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> getMessagesByUserId(int userId) {
        List<Message> userMessages = new ArrayList<>();
        String sql = """
                     SELECT user_id, text, timestamp FROM message
                     WHERE user_id = ?
                     """;

        try(Connection connection = DatabaseUtil.getInstance().getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setInt(1, userId);
            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    Message message = new Message(
                    rs.getInt("user_id"),
                    rs.getString("text"),
                    rs.getTimestamp("timestamp").toLocalDateTime()
                    );
                    userMessages.add(message);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userMessages;
    }
}
