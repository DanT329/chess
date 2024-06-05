package ui;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import client.websocket.WebSocketFacade;

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
        printBoard(chessGame.getBoard(), isWhite);
        // TODO: Update the UI with the new game state
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
}

