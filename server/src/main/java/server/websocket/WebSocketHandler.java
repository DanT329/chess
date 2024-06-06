package server.websocket;
import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        try {
            switch (action.getCommandType()) {
                case CONNECT -> {
                    Connect connectAction = new Gson().fromJson(message, Connect.class);
                    connect(connectAction.getAuthString(), connectAction.getGameID(), session);
                }
                case MAKE_MOVE -> {
                    MakeMove makeMoveAction = new Gson().fromJson(message, MakeMove.class);
                    makeMove(makeMoveAction.getAuthString(), makeMoveAction.getGameID(), session, makeMoveAction.getGameState(), makeMoveAction.getMove());
                }
                case LEAVE -> {
                    Leave leaveAction = new Gson().fromJson(message, Leave.class);
                    leave(leaveAction.getAuthString(), leaveAction.getGameID());
                }
                case ERROR_SUB ->{
                    sendError(new Exception(),session);
                }
                // Handle other cases here
            }
        } catch (Exception e) {
            sendError(e, session);
        }
    }

    private void leave(String authToken, Integer gameID) throws DataAccessException, IOException {
        String userName = dataAccess.verifyToken(authToken).username();
        connections.remove(gameID,userName);
        var message = String.format("%s has left the game!", userName);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        dataAccessGame.removeUserFromGame(gameID,userName);
        connections.broadcast(gameID, userName, notification);
    }
    private void connect(String authToken, Integer gameID, Session session) throws IOException, DataAccessException, SQLException {
        String userName = dataAccess.verifyToken(authToken).username();
        connections.add(gameID, userName, session);
        var message = String.format("%s joined the game!", userName);
        loadGame(authToken,gameID,session);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, userName, notification);
    }

    private void makeMove(String authToken, Integer gameID, Session session,String gameState, ChessMove move) throws IOException, DataAccessException, SQLException {
        String userName = dataAccess.verifyToken(authToken).username();
        try{
            dataAccessGame.pushGame(gameID,gameState);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        var message = String.format("%s Made Move:%s ", move,userName);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, userName,notification);
        loadGameAll(authToken,gameID,session);
    }

    private void loadGameAll(String authToken, Integer gameID, Session session) throws IOException, DataAccessException, SQLException {
        String userName = dataAccess.verifyToken(authToken).username();
        String game = dataAccessGame.loadGame(gameID);
        var notification = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME,game);
        connections.broadcastAll(gameID,notification);
    }

    private void loadGame(String authToken, Integer gameID, Session session) throws IOException, DataAccessException, SQLException {
        String userName = dataAccess.verifyToken(authToken).username();
        String game = dataAccessGame.loadGame(gameID);
        var notification = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME,game);
        connections.broadcast(gameID, userName, notification);
    }

    private void sendError(Exception e,Session session) {
        var notification = new ErrorNotification(ServerMessage.ServerMessageType.ERROR, e.getMessage());
        try {
            session.getRemote().sendString(new Gson().toJson(notification));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}