package server.websocket;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;
import dataaccess.DataAccessMySQLAuth;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> {
                Connect connectAction = new Gson().fromJson(message, Connect.class);
                connect(connectAction.getAuthString(), connectAction.getGameID(), session);
            }
            // Handle other cases like MAKE_MOVE, LEAVE, RESIGN here
        }
    }

    private void connect(String authToken, Integer gameID, Session session) throws IOException, DataAccessException {
        String userName = dataAccess.verifyToken(authToken).username();
        connections.add(gameID, userName, session);
        var message = String.format("%s joined the game!", userName);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, userName, notification);
    }
}