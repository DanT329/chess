package ui;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import model.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserInterfaceConsole {
    ServerFacade serverFacade;
    private boolean loggedIn = false;
    String AUTH_TOKEN;
    String USER_NAME;
    HashMap<Integer, GameData> currentGames = new HashMap<>();

    public UserInterfaceConsole(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void run() {
        System.out.println("Welcome to Chess for CS240!");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if(!loggedIn){
                System.out.print("[LOGGED OUT] >> ");
            }else{
                System.out.print("[LOGGED IN] >> ");
            }

            String input = scanner.nextLine();

            if(input.equals("Quit") && !loggedIn){
                break;
            }else if(input.equals("Help") && !loggedIn){
                System.out.println("""
                        Quite - Exit Application
                        Login - Login to play games
                        Register - Register a new account
                        Help - List commands""");
            }else if(input.equals("Register") && !loggedIn){
                System.out.println("Enter username >> ");
                String username = scanner.nextLine();
                System.out.println("Enter password >> ");
                String password = scanner.nextLine();
                System.out.println("Enter email >> ");
                String email = scanner.nextLine();
                try{
                    AuthData auth = serverFacade.register(new UserData(username, password,email));
                    AUTH_TOKEN = auth.authToken();
                    USER_NAME = auth.username();
                    loggedIn = true;
                }catch(IOException | URISyntaxException e){
                    System.out.println("[ERROR >> ]" + e.getMessage());
                }
            }else if(input.equals("Login") && !loggedIn){
                System.out.println("Enter a username >> ");
                String username = scanner.nextLine();
                System.out.println("Enter a password >> ");
                String password = scanner.nextLine();
                try{
                   AuthData auth = serverFacade.login(new UserData(username,password,null));
                   AUTH_TOKEN = auth.authToken();
                   USER_NAME = auth.username();
                   loggedIn = true;
                }catch(IOException | URISyntaxException e){
                    System.out.println("[ERROR >> ]" + e.getMessage());
                }
            }else if(input.equals("Help") && loggedIn){
                System.out.println("""
                        Logout - Logs out user from Application
                        Create Game - Input name for new game
                        Play Game - Chose a game and color to join
                        Observe Game - View a game as an observer
                        Help - List commands""");
            }else if(input.equals("List Games") && loggedIn){
                try{
                    printGames();
                } catch (IOException | URISyntaxException e) {
                    System.out.println("[ERROR >> ]" + e.getMessage());;
                }
            }else if(input.equals("Logout") && loggedIn){
                try{
                    serverFacade.logout(new AuthData(AUTH_TOKEN,USER_NAME));
                    loggedIn = false;
                }catch(IOException | URISyntaxException e){
                    System.out.println("[ERROR >> ]" + e.getMessage());
                }
            }else if(input.equals("Create Game") && loggedIn){
                try{
                    System.out.print("Enter a game name >> ");
                    String gameName = scanner.nextLine();
                    serverFacade.createGame(new AuthData(AUTH_TOKEN,USER_NAME),new GameData(0,null,null,gameName,null));
                }catch(IOException | URISyntaxException e) {
                    System.out.println("[ERROR >> ]" + e.getMessage());
                }
            }else if(input.equals("Play Game") && loggedIn){
                    System.out.print("Enter a game number >> ");
                    if (scanner.hasNextInt()) {
                        int gameNumber = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter a color >> ");
                        String color = scanner.nextLine().toUpperCase();
                        //System.out.println(" ");
                        try {
                            serverFacade.joinGame(new GameJoinUser(color, currentGames.get(gameNumber).gameID(),AUTH_TOKEN));
                            ChessBoard board = new ChessBoard();
                            board.resetBoard();
                            printBoard(board, color.equals("WHITE"));
                        }catch(IOException | URISyntaxException e) {
                            System.out.println("[ERROR >> ]" + e.getMessage());
                        }catch(NullPointerException e){
                            System.out.println("[ERROR >> ] Invalid Game Number");
                        }
                    } else {
                        System.out.println("Invalid input. Please enter a valid integer.");
                    }
            }
        }
    }

    private void printGames() throws IOException, URISyntaxException {
        GameWrapper gameList = serverFacade.listGames(new AuthData(AUTH_TOKEN, null));
        Integer i = 1;
        currentGames.clear();
        for (GameData game : gameList.games()) {
            currentGames.put(i, game);
            i++;
        }
        System.out.println("List of Available Games:");
        for (Map.Entry<Integer, GameData> entry : currentGames.entrySet()) {
            Integer key = entry.getKey();
            GameData game = entry.getValue();
            System.out.printf("Game %d: ID=%s, White Player=%s, Black Player=%s, Name=%s%n",
                    key, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
        }
    }

    private void printBoard(ChessBoard board, boolean isWhitePerspective) {
        int startRow = isWhitePerspective ? 8 : 1;
        int endRow = isWhitePerspective ? 1 : 8;
        int startCol = isWhitePerspective ? 1 : 8;
        int endCol = isWhitePerspective ? 8 : 1;
        int rowIncrement = isWhitePerspective ? -1 : 1;
        int colIncrement = isWhitePerspective ? 1 : -1;

        for (int row = startRow; isWhitePerspective ? row >= endRow : row <= endRow; row += rowIncrement) {
            System.out.print(row + " ");
            for (int col = startCol; isWhitePerspective ? col <= endCol : col >= endCol; col += colIncrement) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                // Alternate colors for board spaces
                boolean isWhiteSpace = (row + col) % 2 == 0;
                String bgColor = isWhiteSpace ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                // Piece representation with colors
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

                System.out.print(bgColor + pieceRepresentation + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }
            System.out.println();
        }
        System.out.println("   A   B   C  D   E   F   G   H");
    }

}
