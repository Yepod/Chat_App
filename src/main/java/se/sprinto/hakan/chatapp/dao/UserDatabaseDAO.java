package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.model.Message;
import se.sprinto.hakan.chatapp.model.User;
import se.sprinto.hakan.chatapp.util.DatabaseUtil;

import java.sql.*;

public class UserDatabaseDAO implements UserDAO {

    @Override
    public User register(User user) {
        String sql = "INSERT INTO user (username, password) VALUES (?,?)";

        try (Connection connection = DatabaseUtil.getInstance().getConnection(); // ↓ RETURNERAR ID SKAPAT AV DATABAS
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());

            int usersCreated = pstmt.executeUpdate();
            System.out.println(usersCreated + " users created");

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User login(String username, String password) {
        String sql =
                """
                SELECT text, message.timestamp, user.user_id, username, password FROM user
                LEFT JOIN message
                ON user.user_id = message.user_id
                WHERE username = ?
                """;

        try(Connection connection = DatabaseUtil.getInstance().getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try(ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    User user = rowToUser(rs);
                    user.addMessage(new Message(
                            rs.getInt("user_id"),
                            rs.getString("text"),
                            rs.getTimestamp("timestamp").toLocalDateTime()
                    ));

                    while(rs.next()) {
                        user.addMessage(new Message(
                                rs.getInt("user_id"),
                                rs.getString("text"),
                                rs.getTimestamp("timestamp").toLocalDateTime()
                        ));
                    }

                    if(user.getPassword().equals(password)) {
                        return user;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private User rowToUser(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        return new User(userId, username, password);
    }
}