package client.websocket;

import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGame;

public class LoadGameHandler implements ServerMessageHandler {
    @Override
    public void notify(ServerMessage message) {
        LoadGame notification = (LoadGame) message;
        String passedMessage = notification.getGame();
    }

    public String getGame(ServerMessage message){
        LoadGame loadGame = (LoadGame) message;
        return loadGame.getGame();
    }
}
