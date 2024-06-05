package websocket.commands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand{
    private final String gameState;
    ChessMove move;
    private final Integer gameID;
    public MakeMove(String authToken, ChessMove move, String gameState, Integer gameID) {
        super(authToken);
        this.gameState = gameState;
        this.move = move;
        this.gameID = gameID;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public String getGameState(){
        return gameState;
    }

    public ChessMove getMove(){
        return move;
    }

    public Integer getGameID(){
        return gameID;
    }
}
