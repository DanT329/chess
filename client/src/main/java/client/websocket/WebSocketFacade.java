package client.websocket;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.UserData;
import websocket.messages.*;
import websocket.commands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import ui.UserInterfaceGameplay;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

// Extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {
    public ChessGame gameState;
    private Session session;
    private Consumer<ChessGame> onGameStateChange; // Listener for game state changes

    public WebSocketFacade(String url) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // Set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    // Handle incoming messages here
                    handleIncomingMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // You can perform any setup here if needed
    }

    public void playGame(Integer gameID, String authToken) {
        try {
            System.out.println("In playGame()");
            var action = new Connect(authToken, gameID);
            var gson = new Gson().toJson(action);
            this.session.getBasicRemote().sendText(gson);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void moveGame(Integer gameID, String authToken, String gameState, ChessMove move){
        try{
            System.out.println("Server Facade gameState before convert to MakeMove");
            var action = new MakeMove(authToken,move,gameState,gameID);
            var gson = new Gson().toJson(action);
            this.session.getBasicRemote().sendText(gson);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleIncomingMessage(String message) {
        System.out.println("In handleIncomingMessage()");
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        System.out.println(serverMessage);
        if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)){
            Notification notification = new Gson().fromJson(message, Notification.class);
            NotificationHandler handler = new NotificationHandler();
            handler.notify(notification);
        }else if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            System.out.println("Load game");
            LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
            System.out.println(loadGame.getGame());
            if (loadGame.getGame() != null) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
                        .create();
                gameState = gson.fromJson(loadGame.getGame(), ChessGame.class);
                System.out.println(gameState);
                if (onGameStateChange != null) {
                    onGameStateChange.accept(gameState); // Notify listener of game state change
                }
            } else {
                gameState = null;
            }
        }
    }
    public void setOnGameStateChange(Consumer<ChessGame> listener) {
        this.onGameStateChange = listener; // Set listener for game state changes
    }

    public ChessGame getGameState() {
        return gameState; // Getter for game state
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
