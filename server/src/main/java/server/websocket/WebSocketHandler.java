package server.websocket;
import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import dataaccess.DataAccessMySQLGame;
import model.GameData;
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
                    connect(connectAction.getAuthString(), connectAction.getGameID(), session,connectAction.getIsObserver());
                }
                case MAKE_MOVE -> {
                    MakeMove makeMoveAction = new Gson().fromJson(message, MakeMove.class);
                    makeMove(makeMoveAction.getAuthString(), makeMoveAction.getGameID(), session, makeMoveAction.getGameState(), makeMoveAction.getMove());
                }
                case LEAVE -> {
                    Leave leaveAction = new Gson().fromJson(message, Leave.class);
                    leave(leaveAction.getAuthString(), leaveAction.getGameID());
                }
                case RESIGN -> {
                    Resign resignAction = new Gson().fromJson(message, Resign.class);
                    resign(resignAction.getAuthString(), resignAction.getGameID(), session);
                }
                case ERROR_SUB -> {
                    sendError(new Exception(),session);
                }
                // Handle other cases here
            }
        } catch (Exception e) {
            sendError(e, session);
        }
    }

    private void resign(String authToken, Integer gameID,Session session) throws DataAccessException, IOException, SQLException, InvalidMoveException {
        String userName = dataAccess.verifyToken(authToken).username();


        GameData gameData = dataAccessGame.getGameByID(gameID);
        ChessGame game = gameData.game();

        if(!game.getGameUp()){
            throw new InvalidMoveException("Game already resigned by player");
        }

        //Not observer
        isPlayer(userName,gameID);

        //Set new gamestate to over
        game.setGameUp(false);
        var gson = new Gson().toJson(game);
        dataAccessGame.pushGame(gameID,gson);

        var message = String.format("%s has resigned from the game!", userName);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcastAll(gameID, notification);
    }

    private void isPlayer(String username,Integer gameID) throws InvalidMoveException {
        GameData gameData = dataAccessGame.getGameByID(gameID);
        ChessGame.TeamColor playerColor;
        if(gameData.whiteUsername().equals(username)){
            playerColor = ChessGame.TeamColor.WHITE;
        }else if(gameData.blackUsername().equals(username)){
            playerColor = ChessGame.TeamColor.BLACK;
        }else{
            throw new InvalidMoveException();
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
    private void connect(String authToken, Integer gameID, Session session,boolean isObserver) throws IOException, DataAccessException, SQLException {
        String userName = dataAccess.verifyToken(authToken).username();
        connections.add(gameID, userName, session);
        GameData gameData = dataAccessGame.getGameByID(gameID);
        String message = "Error: Can't see who username is!";

        if(isObserver){
            message = String.format("%s joined the game as observer!", userName);
        }else if(gameData.whiteUsername().equals(userName)){
            message = String.format("%s joined the game as WHITE!", userName);
        }else if(gameData.blackUsername().equals(userName)){
            message = String.format("%s joined the game as BLACK!", userName);
        }

        loadGame(authToken,gameID,session);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, userName, notification);
    }

    private void makeMove(String authToken, Integer gameID, Session session,String gameState, ChessMove move) throws IOException, DataAccessException, SQLException, InvalidMoveException {
        String userName = dataAccess.verifyToken(authToken).username();
        badMoveCheck(userName,gameID,move);
        try{
            dataAccessGame.pushGame(gameID,gameState);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        var message = String.format("%s Made Move:%s ", move,userName);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, userName,notification);
        loadGameAll(authToken,gameID,session);
        checkState(gameID,authToken,session,userName);
    }

    private void checkState(Integer gameID,String authToken,Session session, String userName) throws IOException, SQLException, DataAccessException {
        GameData gameData = dataAccessGame.getGameByID(gameID);
        ChessGame game = gameData.game();
        String message = null;
        if(game.isInCheck(ChessGame.TeamColor.WHITE)){
            message = "White is in check!";
        }else if(game.isInCheck(ChessGame.TeamColor.BLACK)){
            message = "Black is in check!";
        }else if(game.isInCheckmate(ChessGame.TeamColor.WHITE)){
            message = "White is in checkmate!";
        }else if(game.isInCheckmate(ChessGame.TeamColor.BLACK)){
            message = "Black is in checkmate!";
        }else if(game.isInStalemate(ChessGame.TeamColor.WHITE)){
            message = "White is in stalemate!";
        }else if(game.isInCheckmate(ChessGame.TeamColor.BLACK)){
            message = "Black is in stalemate!";
        }
        if(message != null){
            var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcastAll(gameID,notification);
        }

    }

    private void badMoveCheck(String username, Integer gameID,ChessMove move) throws InvalidMoveException {
        GameData gameData = dataAccessGame.getGameByID(gameID);
        ChessGame.TeamColor playerColor;
        if(gameData.whiteUsername().equals(username)){
            playerColor = ChessGame.TeamColor.WHITE;
        }else if(gameData.blackUsername().equals(username)){
            playerColor = ChessGame.TeamColor.BLACK;
        }else{
            throw new InvalidMoveException();
        }
        ChessBoard board = gameData.game().getBoard();
        gameData.game().makeMove(move);

        if(!board.getPiece(move.getStartPosition()).getTeamColor().equals(playerColor)){
            throw new InvalidMoveException();
        }
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