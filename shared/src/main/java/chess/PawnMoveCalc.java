package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoveCalc {

    public static Collection<ChessMove> staticMove(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        Collection<ChessMove> moves = new HashSet<>();

        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        //WHITE PAWN
        if(pieceColor == ChessGame.TeamColor.WHITE){
            //first move two spaces
            ChessPosition currentCheck = new ChessPosition(row+2, column);
            ChessPosition currentCheckOne = new ChessPosition(row+1, column);
            if(row == 2 && board.getPiece(currentCheck) == null && board.getPiece(currentCheckOne) == null){
                moves.add(new ChessMove(myPosition,currentCheck,null));
            }

            //second move one space
            currentCheck = new ChessPosition(row+1, column);
            if(row < 8 && board.getPiece(currentCheck) == null){
                enemyRightTwo(myPosition, moves, row, currentCheck);
            }

            //enemy left
            currentCheck = new ChessPosition(row+1, column-1);
            if(row < 8 && column > 1 && board.getPiece(currentCheck) != null && board.getPiece(currentCheck).getTeamColor() != pieceColor){
                enemyRightTwo(myPosition, moves, row, currentCheck);
            }
            //enemy right
            currentCheck = new ChessPosition(row+1, column+1);
            if(row < 8 && column < 8 && board.getPiece(currentCheck) != null && board.getPiece(currentCheck).getTeamColor() != pieceColor){
                enemyRightTwo(myPosition, moves, row, currentCheck);
            }
        }else{
            //first move two spaces
            ChessPosition currentCheck = new ChessPosition(row-2, column);
            ChessPosition currentCheckOne = new ChessPosition(row-1, column);
            if(row == 7  && board.getPiece(currentCheck) == null && board.getPiece(currentCheckOne) == null){
                moves.add(new ChessMove(myPosition,currentCheck,null));
            }
            //second move one space
            currentCheck = new ChessPosition(row-1, column);
            if(row > 1 && board.getPiece(currentCheck) == null){
                enemyRight(myPosition, moves, row, currentCheck);
            }
            //enemy left
            currentCheck = new ChessPosition(row-1, column-1);
            if(row > 1 && column > 1 && board.getPiece(currentCheck) != null && board.getPiece(currentCheck).getTeamColor() != pieceColor){
                enemyRight(myPosition, moves, row, currentCheck);
            }
            //enemy right
            currentCheck = new ChessPosition(row-1, column+1);
            if(row > 1 && column < 8 && board.getPiece(currentCheck) != null && board.getPiece(currentCheck).getTeamColor() != pieceColor){
                enemyRight(myPosition, moves, row, currentCheck);
            }
        }
        return moves;
    }

    private static void enemyRightTwo(ChessPosition myPosition, Collection<ChessMove> moves, int row, ChessPosition currentCheck) {
        if(row+1 == 8){
            promotePiece(myPosition, moves, currentCheck);
        }else{
            moves.add(new ChessMove(myPosition,currentCheck,null));
        }
    }

    private static void promotePiece(ChessPosition myPosition, Collection<ChessMove> moves, ChessPosition currentCheck) {
        moves.add(new ChessMove(myPosition,currentCheck, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(myPosition,currentCheck,ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(myPosition,currentCheck,ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(myPosition,currentCheck,ChessPiece.PieceType.KNIGHT));
    }

    private static void enemyRight(ChessPosition myPosition, Collection<ChessMove> moves, int row, ChessPosition currentCheck) {
        if(row-1 == 1){
            promotePiece(myPosition, moves, currentCheck);
        }else{
            moves.add(new ChessMove(myPosition,currentCheck,null));
        }
    }
}
