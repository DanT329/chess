package client.websocket;

import com.google.gson.Gson;
import model.UserData;
import websocket.messages.*;
import websocket.commands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

// Extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    private Session session;

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
            var action = new Connect(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void handleIncomingMessage(String message) {

        Notification notification = new Gson().fromJson(message, Notification.class);
        NotificationHandler handler = new NotificationHandler();
        handler.notify(notification);
    }
}
