import chess.*;
import client.ServerFacade;
import ui.UserInterfaceConsole;

public class Main {
    public static void main(String[] args) {
        UserInterfaceConsole ui = new UserInterfaceConsole(new ServerFacade("localhost",8080));
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ui.run();
    }
}