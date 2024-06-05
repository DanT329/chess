package client.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.UserData;
import websocket.messages.*;
import websocket.commands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import chess.ChessGameDeserializer;
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
}
