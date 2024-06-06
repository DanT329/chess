package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import dataaccess.DataAccessMySQLGame;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionManager {
    // Map from gameID to list of connections (participants)
    private final ConcurrentHashMap<Integer, List<Connection>> gameConnections = new ConcurrentHashMap<>();
    private final DataAccessMySQLGame dataAccess = new DataAccessMySQLGame();
    public void add(int gameID, String visitorName, Session session) {
        var connection = new Connection(gameID, visitorName, session);
        gameConnections.computeIfAbsent(gameID, k -> new CopyOnWriteArrayList<>()).add(connection);
    }

    public void remove(int gameID, String visitorName) {
        List<Connection> connections = gameConnections.get(gameID);
        if (connections != null) {
            connections.removeIf(connection -> connection.userName.equals(visitorName));
            if (connections.isEmpty()) {
                gameConnections.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, String excludeVisitorName, ServerMessage notification) throws IOException {
        List<Connection> connections = gameConnections.get(gameID);
        if (connections != null) {
            String notificationMessage = new Gson().toJson(notification);

            for (Connection connection : connections) {
                if (notification.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
                    if (connection.session.isOpen() && connection.userName.equals(excludeVisitorName)) {
                        connection.send(notificationMessage);
                    }
                } else {
                    // Send other types of messages excluding the current user
                    if (connection.session.isOpen() && !connection.userName.equals(excludeVisitorName)) {
                        connection.send(notificationMessage);
                    }
                }
            }
        }
    }

    public void broadcastAll(int gameID, ServerMessage notification) throws IOException {
        List<Connection> connections = gameConnections.get(gameID);
        if (connections != null) {
            String notificationMessage = new Gson().toJson(notification);

            for (Connection connection : connections) {
                if (connection.session.isOpen()) {
                    connection.send(notificationMessage);
                }
            }
        }
    }


}
