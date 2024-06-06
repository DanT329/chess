package client.websocket;

import websocket.messages.ErrorNotification;
import websocket.messages.ServerMessage;

public class ErrorHandler implements ServerMessageHandler{
    @Override
    public void notify(ServerMessage message) {
        ErrorNotification notification = (ErrorNotification) message;
        String passedMessage = notification.getErrorMessage();
        System.out.println("Received notification: " + passedMessage);
    }
}
