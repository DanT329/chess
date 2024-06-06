package websocket.commands;

public class Resign extends UserGameCommand{
    private final Integer gameID;
    public Resign(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
    }
    public Integer getGameID(){
        return gameID;
    }
}
