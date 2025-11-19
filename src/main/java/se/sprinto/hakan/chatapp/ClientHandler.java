package se.sprinto.hakan.chatapp;

import se.sprinto.hakan.chatapp.dao.*;
import se.sprinto.hakan.chatapp.model.Message;
import se.sprinto.hakan.chatapp.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;
    private PrintWriter out;
    private User user;

    private final UserDAO userDAO = new UserDatabaseDAO();
    private final MessageDAO messageDAO = new MessageDatabaseDAO();

    ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)

        ) {
            this.out = writer;

            writer.println("Välkommen! Har du redan ett konto? (ja/nej)");
            String answer = in.readLine();

            if ("ja".equalsIgnoreCase(answer)) {
                writer.println("Ange användarnamn:");
                String username = in.readLine();
                writer.println("Ange lösenord:");
                String password = in.readLine();

                user = userDAO.login(username, password);
                if (user == null) {
                    writer.println("Fel användarnamn eller lösenord.");
                    writer.println("Du måste skriva /quit nu för att avsluta denna klient");
                    writer.println("Pröva att återansluta med en ny klient");
                    
                }
            } else {
                writer.println("Skapa nytt konto. Ange användarnamn:");
                String username = in.readLine();
                writer.println("Ange lösenord:");
                String password = in.readLine();
                user = userDAO.register(new User(username, password));
                writer.println("Konto skapat. Välkommen, " + user.getUsername() + "!");
            }

            writer.println("Du är inloggad som: " + user.getUsername() + "");
            writer.println("Nu kan du börja skriva meddelanden");
            writer.println("Skriv /quit för att avsluta");
            writer.println("Skriv /mymsgs för att lista alla dina meddelanden");

            System.out.println(user.getUsername() + " anslöt.");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                } else if (message.equalsIgnoreCase("/mymsgs")) {
                    // Hämta meddelanden för denna användare
                    List<Message> messages = messageDAO.getMessagesByUserId(user.getId());
                    if (messages.isEmpty()) {
                        out.println("Inga sparade meddelanden.");
                    } else {
                        out.println("Dina meddelanden:");
                        for (Message m : messages) {
                            out.println("[" + m.getTimestamp() + "] " + m.getText());
                        }
                    }
                } else {
                    server.broadcast(message, this);
                    messageDAO.saveMessage(new Message(user.getId(), message, java.time.LocalDateTime.now()));
                }
            }

        } catch (IOException e) {
            System.out.println("Problem med klient: " + e.getMessage());
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    void sendMessage(String msg) {
        if (out != null) out.println(msg);
    }
}

