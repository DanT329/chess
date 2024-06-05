package server.websocket;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessMySQLGame;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;
import dataaccess.DataAccessMySQLAuth;

import java.io.IOException;
import java.sql.SQLException;

@WebSocket
public class WebSocketHandler {
    private final DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
    private final DataAccessMySQLGame dataAccessGame = new DataAccessMySQLGame();
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        System.out.println("In onMessage()");
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> {
                Connect connectAction = new Gson().fromJson(message, Connect.class);
                connect(connectAction.getAuthString(), connectAction.getGameID(), session);
            }
            // Handle other cases like MAKE_MOVE, LEAVE, RESIGN here
        }
        System.out.println("Missed case");
    }

    private void connect(String authToken, Integer gameID, Session session) throws IOException, DataAccessException{
        System.out.println("In connect");
        String userName = dataAccess.verifyToken(authToken).username();
        connections.add(gameID, userName, session);
        var message = String.format("%s joined the game!", userName);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, userName, notification);
        try{loadGame(authToken,gameID,session);}catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void loadGame(String authToken, Integer gameID, Session session) throws IOException, DataAccessException, SQLException {
        System.out.println("In loadGame()");
        String userName = dataAccess.verifyToken(authToken).username();
        String game = dataAccessGame.loadGame(gameID);
        var notification = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME,game);
        System.out.println(game);
        connections.broadcast(gameID, userName, notification);
    }
}