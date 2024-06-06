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

    public void playGame(Integer gameID, String authToken,boolean isObserver) {
        try {
            var action = new Connect(authToken, gameID);
            action.setIsObserver(isObserver);
            var gson = new Gson().toJson(action);
            this.session.getBasicRemote().sendText(gson);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void leaveGame(Integer gameID, String authToken){
        try{
            var action = new Leave(authToken,gameID);
            var gson = new Gson().toJson(action);
            this.session.getBasicRemote().sendText(gson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveGame(Integer gameID, String authToken, String gameState, ChessMove move){
        try{
            var action = new MakeMove(authToken,move,gameState,gameID);
            var gson = new Gson().toJson(action);
            this.session.getBasicRemote().sendText(gson);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void resignGame(Integer gameID, String authToken){
        try{
            var action = new Resign(authToken,gameID);
            var gson = new Gson().toJson(action);
            this.session.getBasicRemote().sendText(gson);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    private void handleIncomingMessage(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)){
            Notification notification = new Gson().fromJson(message, Notification.class);
            NotificationHandler handler = new NotificationHandler();
            handler.notify(notification);
            System.out.print("[IN GAME] >> ");
        }else if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)) {
            LoadGame loadGame = new Gson().fromJson(message, LoadGame.class);
            if (loadGame.getGame() != null) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
                        .create();
                gameState = gson.fromJson(loadGame.getGame(), ChessGame.class);
                if (onGameStateChange != null) {
                    onGameStateChange.accept(gameState); // Notify listener of game state change
                }
            } else {
                gameState = null;
            }
        }else if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)){
            ErrorNotification notification = new Gson().fromJson(message, ErrorNotification.class);
            ErrorHandler handler = new ErrorHandler();
            handler.notify(notification);
            System.out.print("[IN GAME] >> ");
        }
    }
    public void setOnGameStateChange(Consumer<ChessGame> listener) {
        this.onGameStateChange = listener; // Set listener for game state changes
    }

    public ChessGame getGameState() {
        return gameState; // Getter for game state
    }
}
