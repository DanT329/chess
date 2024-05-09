package chess;


import java.util.HashMap;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    // something to hold all chest pieces currently in game
    // something to hold board state I guess
    // use a map since I don't want to create a hash for position

    private HashMap<ChessPosition,ChessPiece> board;

    public ChessBoard() {
        this.board = new HashMap<>();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board.put(position,piece);
    }

    public void removePiece(ChessPosition position) {
        this.board.remove(position);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.board.get(position);
    }

    public HashMap<ChessPosition, ChessPiece> getBoard() {
        return this.board;
    }

    // Copy constructor shallow
    public ChessBoard(ChessBoard other) {
        this.board = new HashMap<>(other.board);
    }
    // Method to create a copy of the board shallow
    public ChessBoard copy() {
        return new ChessBoard(this);
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.board.clear();
        //add BLACK pieces
        addPiece(new ChessPosition(8,1),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8,2),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,3),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,4),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8,5),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8,6),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,7),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,8),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(7,1),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,2),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,3),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,4),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,5),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,6),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,7),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(7,8),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN));

        //add WHITE pieces
        addPiece(new ChessPosition(1,1),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1,2),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,3),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,4),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1,5),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1,6),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,7),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,8),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(2,1),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,2),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,3),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,4),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,5),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,6),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,7),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN));
        addPiece(new ChessPosition(2,8),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(board);
    }
}
