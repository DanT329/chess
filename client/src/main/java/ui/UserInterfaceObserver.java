package ui;

import chess.*;
import client.websocket.WebSocketFacade;
import java.util.Scanner;
import static ui.DrawBoard.printBoard;

public class UserInterfaceObserver {

    private final String authToken;
    private final WebSocketFacade webSocketFacade;
    private ChessGame chessGame;
    private final Integer gameID;
    private final boolean isWhite;

    public UserInterfaceObserver(String authToken, WebSocketFacade webSocketFacade, Integer gameID, boolean isWhite) {
        this.authToken = authToken;
        this.webSocketFacade = webSocketFacade;
        this.gameID = gameID;
        this.isWhite = isWhite;
        this.webSocketFacade.setOnGameStateChange(this::updateGameState); // Set listener for game state changes
    }

    public void run(Scanner scanner){
        webSocketFacade.playGame(gameID, authToken,true);
        try {
            Thread.sleep(1000); //helps with printing board in correct order
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("The game was interrupted.");
        }
        System.out.println("Observing Game! Enter the command: Help");
        while (true) {
            System.out.print("[OBSERVING GAME] >> ");
            String input = scanner.nextLine();

            switch (input.toLowerCase()) {
                case "help":
                    printHelp();
                    break;
                case "redraw":
                    printBoard(chessGame.getBoard(),isWhite);
                    break;
                case "leave":
                    webSocketFacade.leaveGame(gameID,authToken);
                    System.out.println("Leaving Game...");
                    return;
                default:
                    System.out.println("Unknown command. Type 'Help' for a list of commands.");
                    break;
            }
        }
    }

private void printHelp() {
    System.out.println("Available commands:");
    System.out.println("- Redraw: To reprint game board.");
    System.out.println("- Leave: To leave game.");
    System.out.println("- Help: To display this help message.");
}
    private void updateGameState(ChessGame newGameState) {
        this.chessGame = newGameState;
        System.out.println("Game state updated!");
        printBoard(chessGame.getBoard(), isWhite);
        System.out.print("[OBSERVING GAME] >> ");
    }
}
