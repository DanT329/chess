package ui;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import client.websocket.WebSocketFacade;

import java.util.Scanner;

public class UserInterfaceGameplay {

    private final String authToken;
    private WebSocketFacade webSocketFacade;
    private ChessGame chessGame;
    private final Integer gameID;
    private final boolean isWhite;

    public UserInterfaceGameplay(String authToken, WebSocketFacade webSocketFacade, Integer gameID, boolean isWhite) {
        this.authToken = authToken;
        this.webSocketFacade = webSocketFacade;
        this.gameID = gameID;
        this.isWhite = isWhite;
        this.webSocketFacade.setOnGameStateChange(this::updateGameState); // Set listener for game state changes
    }

    public void run(Scanner scanner) {

        webSocketFacade.playGame(gameID, authToken);
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
                case "quit":
                    System.out.println("Quitting the game...");
                    return; // Exit the method, effectively ending the game loop
                case "help":
                    printHelp();
                    break;
                case "redraw":
                    printBoard(chessGame.getBoard(),isWhite);
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
        System.out.println("- Quit: To exit the game.");
        System.out.println("- Help: To display this help message.");
    }

    private void updateGameState(ChessGame newGameState) {
        this.chessGame = newGameState;
        System.out.println("Game state updated!");
        printBoard(chessGame.getBoard(), isWhite);
        System.out.print("[IN GAME] >> ");
    }

    private void makeMove(Scanner scanner) {
        if(chessGame.getTeamTurn().equals(ChessGame.TeamColor.WHITE) == isWhite){
            System.out.println("Select start position (e.g., 1A): ");
            String startPosition = scanner.nextLine();
            System.out.println("Select end position (e.g., 2A): ");
            String endPosition = scanner.nextLine();
            System.out.println(chessGame.getBoard().toString());
            ChessMove playerMove = convertMoveInput(startPosition, endPosition, scanner);

            try {
                chessGame.makeMove(playerMove);
                sendMove(playerMove);
            } catch (InvalidMoveException e) {
                System.out.println(e.getMessage());
            }
        }else{
            System.out.println("Not Your Turn");
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
        if (chessGame.getBoard().getPiece(startPosition).getPieceType().equals(ChessPiece.PieceType.PAWN)) {
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


    private void invalidInput(String message){
        System.out.println(message);
    }


    private void printBoard(ChessBoard board, boolean isWhitePerspective) {
        int startRow = isWhitePerspective ? 8 : 1;
        int endRow = isWhitePerspective ? 1 : 8;
        int startCol = isWhitePerspective ? 1 : 8;
        int endCol = isWhitePerspective ? 8 : 1;
        int rowIncrement = isWhitePerspective ? -1 : 1;
        int colIncrement = isWhitePerspective ? 1 : -1;
        if(isWhitePerspective){
            System.out.println("   A\u2003 B   C\u2003 D\u2003 E\u2003 F\u2003 G\u2003 H");
        }else{
            System.out.println("   H\u2003 G   F\u2003 E\u2003 D\u2003 C\u2003 B\u2003 A");
        }
        for (int row = startRow; isWhitePerspective ? row >= endRow : row <= endRow; row += rowIncrement) {
            System.out.print(row + " ");
            for (int col = startCol; isWhitePerspective ? col <= endCol : col >= endCol; col += colIncrement) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                // Alternate colors for board spaces
                boolean isWhiteSpace = (row + col) % 2 == 0;
                String bgColor = isWhiteSpace ? EscapeSequences.SET_BG_COLOR_DARK_GREY:EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

                String pieceRepresentation = getPieceRepresentation(piece);

                System.out.print(bgColor + pieceRepresentation + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }
            System.out.println();
        }
        if(isWhitePerspective){
            System.out.println("   A\u2003 B   C\u2003 D\u2003 E\u2003 F\u2003 G\u2003 H");
        }else{
            System.out.println("   H\u2003 G   F\u2003 E\u2003 D\u2003 C\u2003 B\u2003 A");
        }
    }

    private static String getPieceRepresentation(ChessPiece piece) {
        String pieceRepresentation;
        if (piece != null) {
            pieceRepresentation = switch (piece.getPieceType()) {
                case KING ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
                case QUEEN ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
                case BISHOP ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
                case KNIGHT ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
                case ROOK ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
                case PAWN ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
                default -> EscapeSequences.EMPTY;
            };
        } else {
            pieceRepresentation = EscapeSequences.EMPTY;
        }
        return pieceRepresentation;
    }

    private ChessGame getDaGame(String gameInstance){
        if(gameInstance != null){
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
                    .create();
            return gson.fromJson(gameInstance, ChessGame.class);
        }
        return null;
    }
}

