package ui;

import chess.*;
import client.websocket.WebSocketFacade;

import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

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
        webSocketFacade.playGame(gameID, authToken);
        try {
            Thread.sleep(1000); //helps with printing board in correct order
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("The game was interrupted.");
        }
        System.out.println("Observing Game! Enter the command: Help");
    }

    private void updateGameState(ChessGame newGameState) {
        this.chessGame = newGameState;
        System.out.println("Game state updated!");
        printBoard(chessGame.getBoard(), isWhite);
        System.out.print("[IN GAME] >> ");
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

    private void printBoardWithHighlights(ChessGame game, ChessPosition selectedPosition, boolean isWhitePerspective) {
        ChessBoard board = game.getBoard();
        Collection<ChessMove> validMoves = game.validMoves(selectedPosition);
        Set<ChessPosition> validPositions = validMoves.stream()
                .map(ChessMove::getEndPosition)
                .collect(Collectors.toSet());

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

                boolean isWhiteSpace = (row + col) % 2 == 0;
                String bgColor = isWhiteSpace ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

                if (validPositions.contains(position)) {
                    // Alternate between two highlight colors
                    bgColor = validPositions.stream().toList().indexOf(position) % 2 == 0
                            ? EscapeSequences.SET_BG_COLOR_GREEN
                            : EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                }

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

}
