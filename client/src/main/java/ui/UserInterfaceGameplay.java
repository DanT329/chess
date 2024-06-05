package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import client.websocket.WebSocketFacade;
import chess.ChessGameDeserializer;

public class UserInterfaceGameplay {

    private final String authToken;
    private WebSocketFacade webSocketFacade;
    private ChessGame chessGame;
    private ChessBoard chessBoard;
    private final Integer gameID;
    private final boolean isWhite;

    public UserInterfaceGameplay(String authToken, WebSocketFacade webSocketFacade, Integer gameID, boolean isWhite) {
        this.authToken = authToken;
        this.webSocketFacade = webSocketFacade;
        this.gameID = gameID;
        this.isWhite = isWhite;
        this.webSocketFacade.setOnGameStateChange(this::updateGameState); // Set listener for game state changes
    }

    public void run() {
        System.out.println("Starting Game! Enter the command: Help");
        webSocketFacade.playGame(gameID, authToken);
        System.out.println("Game played!");
        // TODO: Set Start board in database
        // TODO: Print start board to screen
        // TODO: Implement make move
    }

    private void updateGameState(ChessGame newGameState) {
        this.chessGame = newGameState;
        System.out.println("Game state updated!");
        // TODO: Update the UI with the new game state
    }

    public void startBoard() {
        // Implement start board logic
    }

    public void printBoard() {
        // Implement print board logic
    }
}

