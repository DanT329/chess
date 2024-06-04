package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public Integer gameID;
    public Session session;
    public String userName;

    public Connection(Integer gameID, String userName, Session session) {
        this.gameID = gameID;
        this.session = session;
        this.userName = userName;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
