package websocket.commands;

public class Connect extends UserGameCommand{
    private final Integer gameID;
    private boolean isObserver;

    public Connect(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.CONNECT;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setIsObserver(boolean observer){
        this.isObserver = observer;
    }

    public boolean getIsObserver(){
        return this.isObserver;
    }

}
