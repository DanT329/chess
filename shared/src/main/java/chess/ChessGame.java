package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    //Game starts with white
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        TeamColor checkTeam = board.getPiece(startPosition).getTeamColor();

        //get list of every possible move //
        Collection<ChessMove> possibleMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        //save original HashMap board implementation
        ChessBoard originalBoard = board.copy();
        for(ChessMove move : possibleMoves) {
            board = originalBoard.copy();
            board.addPiece(move.getEndPosition(),board.getPiece(move.getStartPosition()));
            board.removePiece(move.getStartPosition());

            if(!isInCheck(checkTeam)){
                validMoves.add(move);
            }
        }
        //set board back to orginal
        board = originalBoard.copy();
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        //check if empty
        if(!board.getBoard().containsKey(move.getStartPosition())){
            throw new InvalidMoveException("No piece at position");
        }
            //check turn
        if(board.getPiece(move.getStartPosition()).getTeamColor() != teamTurn){
          throw new InvalidMoveException("Wrong turn");
        }

            ChessPosition startPosition = move.getStartPosition();
            Collection<ChessMove> validMoves = validMoves(startPosition);
            for (ChessMove validMove : validMoves) {
                if (validMove.equals(move)) {
                    //check if pawn
                    //make move
                    if(board.getPiece(move.getStartPosition()).getPieceType() == ChessPiece.PieceType.PAWN
                        && move.getPromotionPiece() != null){
                        board.addPiece(move.getEndPosition(), new ChessPiece(board.getPiece(move.getStartPosition()).getTeamColor(),move.getPromotionPiece()));
                    }else{
                        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                    }
                    board.removePiece(move.getStartPosition());
                    if(teamTurn == TeamColor.BLACK){
                        teamTurn = TeamColor.WHITE;
                    }else{
                        teamTurn = TeamColor.BLACK;
                    }
                    return;
                }
            }
            throw new InvalidMoveException();

        }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        //first check is in checkMate;

        ChessPosition kingPosition = null;
        ArrayList<ChessPosition> enemyPieces = new ArrayList<>();
        HashMap<ChessPosition,ChessPiece> boardCheck = board.getBoard();

        for(HashMap.Entry<ChessPosition,ChessPiece> entry : boardCheck.entrySet()) {
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();
            //king check
            if(piece.getTeamColor() == teamColor) {
                if(piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = position;
                }
            }else{
                enemyPieces.add(position);
            }
        }

        for(ChessPosition position : enemyPieces){
            Collection<ChessMove> possibleMoves = board.getPiece(position).pieceMoves(board,position);
            for(ChessMove move : possibleMoves) {
                if(move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //find all teamColor pieces
        //add start positions to list
        //call valid moves on each list item
        //if any return !null, return false;

        if (isInCheck(teamColor)) {
            ArrayList<ChessPosition> teamPieces = findFriendlyPieces(teamColor);
            for(ChessPosition position : teamPieces){
                if(!validMoves(position).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private ArrayList<ChessPosition> findFriendlyPieces(TeamColor friendlyColor) {
        ArrayList<ChessPosition> friendlyPieces = new ArrayList<>();
        for (HashMap.Entry<ChessPosition, ChessPiece> entry : board.getBoard().entrySet()) {
            ChessPosition position = entry.getKey();
            ChessPiece piece = entry.getValue();
            if(piece.getTeamColor() == friendlyColor) {
                friendlyPieces.add(position);
            }
        }
        return friendlyPieces;
    }
    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //call any valid moves?
        //call inCheckmate?
        // if no to both, return true;
        if (isInCheck(teamColor) || isInCheckmate(teamColor)) {
            return false;
        }else{
            ArrayList<ChessPosition> teamPieces = findFriendlyPieces(teamColor);
            for(ChessPosition position : teamPieces){
                if(!validMoves(position).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
