package client.websocket;

import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public class NotificationHandler implements ServerMessageHandler{

    @Override
    public void notify(ServerMessage message) {
        Notification notification = (Notification) message;
        String passedMessage = notification.getMessage();
        System.out.println("Received notification: " + passedMessage);
    }
}
