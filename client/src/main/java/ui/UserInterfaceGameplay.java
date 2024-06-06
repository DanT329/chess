package ui;

import chess.*;
import com.google.gson.Gson;
import client.websocket.WebSocketFacade;
import java.util.Scanner;
import static ui.DrawBoard.printBoard;
import static ui.DrawBoard.printBoardWithHighlights;

public class UserInterfaceGameplay {

    private final String authToken;
    private WebSocketFacade webSocketFacade;
    private ChessGame chessGame;
    private final Integer gameID;
    private final boolean isWhite;
    private boolean gameAvtive = true;
    public UserInterfaceGameplay(String authToken, WebSocketFacade webSocketFacade, Integer gameID, boolean isWhite) {
        this.authToken = authToken;
        this.webSocketFacade = webSocketFacade;
        this.gameID = gameID;
        this.isWhite = isWhite;
        this.webSocketFacade.setOnGameStateChange(this::updateGameState); // Set listener for game state changes
    }

    public void run(Scanner scanner) {

        webSocketFacade.playGame(gameID, authToken,false);
        try {
            Thread.sleep(1000); // 1000 milliseconds delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.out.println("The game was interrupted.");
        }
        System.out.println("Starting Game! Enter the command: Help");
        // Main game loop
        while (true) {
            System.out.print("[IN GAME] >> ");
            String input = scanner.nextLine();

            switch (input.toLowerCase()) {
                case "make move":
                    makeMove(scanner);
                    break;
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
                case "resign":
                    resignGame(scanner);
                    break;
                case "highlight moves":
                    highLightValidMoves(scanner);
                    break;
                default:
                    System.out.println("Unknown command. Type 'Help' for a list of commands.");
                    break;
            }
        }
    }

    // Method to print help commands
    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("- Make Move: To make a move in the game.");
        System.out.println("- Redraw: To reprint gameboard.");
        System.out.println("- Resign: To resign game but not leave.");
        System.out.println("- Leave: To leave game.");
        System.out.println("- Help: To display this help message.");
    }

    private void resignGame(Scanner scanner){
        System.out.println("Are you sure you want to resign? yes/no: ");
        if(scanner.nextLine().equals("yes")){
            chessGame.setGameUp(false);
            webSocketFacade.resignGame(gameID,authToken);
        }
    }
    private void updateGameState(ChessGame newGameState) {
        this.chessGame = newGameState;
        System.out.println("Game state updated!");
        printBoard(chessGame.getBoard(), isWhite);
        System.out.print("[IN GAME] >> ");
    }

    private void highLightValidMoves(Scanner scanner){
        System.out.println("Enter chess position to highlight moves for: ");
        String position = scanner.nextLine();
        if(isValidPosition(position)){
            int row = Character.getNumericValue(position.charAt(0));
            int column = position.charAt(1) - 'A' + 1;
            ChessPosition startPosition = new ChessPosition(row, column);
            if(chessGame.getBoard().getPiece(startPosition) != null){
                printBoardWithHighlights(chessGame,startPosition,isWhite);
            }
        }else{
            System.out.println("Not Valid Space");
        }
    }
    private void makeMove(Scanner scanner) {
        if(chessGame.getTeamTurn().equals(ChessGame.TeamColor.WHITE) == isWhite){
            System.out.println("Select start position (e.g., 1A): ");
            String startPosition = scanner.nextLine();
            System.out.println("Select end position (e.g., 2A): ");
            String endPosition = scanner.nextLine();
            if(isValidPosition(startPosition) && isValidPosition(endPosition)){
                ChessMove playerMove = convertMoveInput(startPosition, endPosition, scanner);
                try {
                    chessGame.makeMove(playerMove);
                    checkStatus();
                    sendMove(playerMove);
                } catch (InvalidMoveException e) {
                    System.out.println(e.getMessage());
                }
            }else{
                System.out.println("Invalid Input");
            }

        }else{
            System.out.println("Not Your Turn");
        }
    }

    private void checkStatus(){
        if(chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) || chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)){
            chessGame.setGameUp(false);
        }else if(chessGame.isInStalemate(ChessGame.TeamColor.WHITE) || chessGame.isInStalemate(ChessGame.TeamColor.BLACK)){
            chessGame.setGameUp(false);
        }

    }
    private void sendMove(ChessMove move) throws InvalidMoveException {
        var gsonGame = new Gson().toJson(chessGame);
        webSocketFacade.moveGame(gameID,authToken,gsonGame,move);
    }

    private ChessMove convertMoveInput(String start, String end, Scanner scanner) {
        ChessPiece.PieceType pieceType = null;

        // Extract and convert the row and column from the start position
        int row = Character.getNumericValue(start.charAt(0));
        int column = start.charAt(1) - 'A' + 1;
        ChessPosition startPosition = new ChessPosition(row, column);

        // Extract and convert the row and column from the end position
        row = Character.getNumericValue(end.charAt(0));
        column = end.charAt(1) - 'A' + 1;
        ChessPosition endPosition = new ChessPosition(row, column);

        // Check for pawn promotion
        if (chessGame.getBoard().getPiece(startPosition)!= null && chessGame.getBoard().getPiece(startPosition).getPieceType().equals(ChessPiece.PieceType.PAWN)) {
            System.out.println("Attempt promotion (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("""
                        Select promotion type:
                        1. QUEEN
                        2. BISHOP
                        3. ROOK
                        4. KNIGHT""");
                pieceType = selectPromotion(scanner.nextInt());
                scanner.nextLine();  // Consume the newline character after reading an integer

                if (pieceType == null) {
                    System.out.println("Invalid promotion type");
                }
            }
        }

        return new ChessMove(startPosition, endPosition, pieceType);
    }

    private ChessPiece.PieceType selectPromotion(int selection) {
        return switch (selection) {
            case 1 -> ChessPiece.PieceType.QUEEN;
            case 2 -> ChessPiece.PieceType.BISHOP;
            case 3 -> ChessPiece.PieceType.ROOK;
            case 4 -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }

    private boolean isValidPosition(String position) {
        if (position.length() != 2) return false;

        char row = position.charAt(0);
        char col = position.charAt(1);

        return (row >= '1' && row <= '8') && (col >= 'A' && col <= 'H');
    }
}

