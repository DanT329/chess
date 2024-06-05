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
        System.out.println("In onMessage()");
        UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> {
                Connect connectAction = new Gson().fromJson(message, Connect.class);
                connect(connectAction.getAuthString(), connectAction.getGameID(), session);
            }
            case MAKE_MOVE -> {
                MakeMove makeMoveAction = new Gson().fromJson(message,MakeMove.class);
                System.out.println("MAKEMOVEACTION GAMESTATE");
                System.out.println(makeMoveAction.getGameState());
                makeMove(makeMoveAction.getAuthString(),makeMoveAction.getGameID(),session,makeMoveAction.getGameState(),makeMoveAction.getMove());

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

    private void makeMove(String authToken, Integer gameID, Session session,String gameState, ChessMove move) throws IOException, DataAccessException{
        String userName = dataAccess.verifyToken(authToken).username();
        try{
            dataAccessGame.pushGame(gameID,gameState);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        var message = String.format("Made Move:%s ", move);
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

    private void printBoard(ChessBoard board, boolean isWhitePerspective) {
        int startRow = isWhitePerspective ? 8 : 1;
        int endRow = isWhitePerspective ? 1 : 8;
        int startCol = isWhitePerspective ? 1 : 8;
        int endCol = isWhitePerspective ? 8 : 1;
        int rowIncrement = isWhitePerspective ? -1 : 1;
        int colIncrement = isWhitePerspective ? 1 : -1;
        if(isWhitePerspective){
            System.out.println("   A\u2003 B   C\u2003 D\u2003 E\u2003 F\u2003 G\u2003 H");
        }else{
            System.out.println("   H\u2003 G   F\u2003 E\u2003 D\u2003 C\u2003 B\u2003 A");
        }
        for (int row = startRow; isWhitePerspective ? row >= endRow : row <= endRow; row += rowIncrement) {
            System.out.print(row + " ");
            for (int col = startCol; isWhitePerspective ? col <= endCol : col >= endCol; col += colIncrement) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                // Alternate colors for board spaces
                boolean isWhiteSpace = (row + col) % 2 == 0;
                String bgColor = isWhiteSpace ? EscapeSequences.SET_BG_COLOR_DARK_GREY:EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

                String pieceRepresentation = getPieceRepresentation(piece);

                System.out.print(bgColor + pieceRepresentation + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }
            System.out.println();
        }
        if(isWhitePerspective){
            System.out.println("   A\u2003 B   C\u2003 D\u2003 E\u2003 F\u2003 G\u2003 H");
        }else{
            System.out.println("   H\u2003 G   F\u2003 E\u2003 D\u2003 C\u2003 B\u2003 A");
        }
    }

    private static String getPieceRepresentation(ChessPiece piece) {
        String pieceRepresentation;
        if (piece != null) {
            pieceRepresentation = switch (piece.getPieceType()) {
                case KING ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
                case QUEEN ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
                case BISHOP ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
                case KNIGHT ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
                case ROOK ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
                case PAWN ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
                default -> EscapeSequences.EMPTY;
            };
        } else {
            pieceRepresentation = EscapeSequences.EMPTY;
        }
        return pieceRepresentation;
    }

    private ChessGame getDaGame(String gameInstance){
        if(gameInstance != null){
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
                    .create();
            return gson.fromJson(gameInstance, ChessGame.class);
        }
        return null;
    }
}