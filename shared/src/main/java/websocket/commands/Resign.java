package websocket.commands;

import chess.ChessGame;

public class Resign extends UserGameCommand{
    private final Integer gameID;
    private final String gameState;
    public Resign(String authToken, Integer gameID, String gameState) {
        super(authToken);
        this.gameID = gameID;
        this.gameState = gameState;
    }
    public Integer getGameID(){
        return gameID;
    }

    public  String getGameState(){
        return gameState;
    }
}
