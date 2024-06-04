package websocket.commands;

public class Connect extends UserGameCommand{
    private final Integer gameID;

    public Connect(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.CONNECT;
    }

    public Integer getGameID() {
        return gameID;
    }

}
