package ui;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import model.*;
import client.websocket.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserInterfaceConsole {
    ServerFacade serverFacade;
    private boolean loggedIn = false;
    String authToken;
    String userName;
    HashMap<Integer, GameData> currentGames = new HashMap<>();

    public UserInterfaceConsole(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }
    public void run() {
        System.out.println("Welcome to Chess for CS240! Enter the command:Help");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if(!loggedIn){
                System.out.print("[LOGGED OUT] >> ");
            }else{
                System.out.print("[LOGGED IN] >> ");
            }
            String input = scanner.nextLine();
            if(input.equals("Quit") && !loggedIn){
                System.exit(0);
            }else if(input.equals("Help") && !loggedIn){
                helpLoggedOut();
            }else if(input.equals("Register") && !loggedIn){
                System.out.println("Enter username >> ");
                String username = scanner.nextLine();
                System.out.println("Enter password >> ");
                String password = scanner.nextLine();
                System.out.println("Enter email >> ");
                String email = scanner.nextLine();
                try{
                    AuthData auth = serverFacade.register(new UserData(username, password,email));
                    authToken = auth.authToken();
                    userName = auth.username();
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
                   authToken = auth.authToken();
                   userName = auth.username();
                   loggedIn = true;
                }catch(IOException | URISyntaxException e){
                    System.out.println("[ERROR >> ]" + e.getMessage());
                }
            }else if(input.equals("Help") && loggedIn){
                helpLoggedIn();
            }else if(input.equals("List Games") && loggedIn){
                try{
                    printGames();
                } catch (IOException | URISyntaxException e) {
                    System.out.println("[ERROR >> ]" + e.getMessage());;
                }
            }else if(input.equals("Logout") && loggedIn){
                try{
                    serverFacade.logout(new AuthData(authToken, userName));
                    loggedIn = false;
                }catch(IOException | URISyntaxException e){
                    System.out.println("[ERROR >> ]" + e.getMessage());
                }
            }else if(input.equals("Create Game") && loggedIn){
                try{
                    System.out.print("Enter a game name >> ");
                    String gameName = scanner.nextLine();
                    serverFacade.createGame(new AuthData(authToken, userName),new GameData(0,null,null,gameName,null));
                }catch(IOException | URISyntaxException e) {
                    System.out.println("[ERROR >> ]" + e.getMessage());
                }
            }else if(input.equals("Play Game") && loggedIn){
                playGame(scanner);
            }
            else if(input.equals("Observe Game") && loggedIn){
                observeGame(scanner);
            }else{
                System.out.println("Invalid input");
            }
        }
    }

    private void playGame(Scanner scanner){
        System.out.print("Enter a game number >> ");
        if (scanner.hasNextInt()) {
            int gameNumber = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter a color >> ");
            String color = scanner.nextLine().toUpperCase();

            try {
                    try {
                        serverFacade.joinGame(new GameJoinUser(color, currentGames.get(gameNumber).gameID(), authToken));
                        UserInterfaceGameplay gamePlay = new UserInterfaceGameplay(authToken,new WebSocketFacade("http://localhost:8080"),currentGames.get(gameNumber).gameID(),color.equals("WHITE"));
                        gamePlay.run(scanner);
                    } catch(IOException | URISyntaxException e) {
                        System.out.println("[ERROR >> ] " + e.getMessage());
                    } catch(NullPointerException e) {
                        System.out.println("[ERROR >> ] Invalid Game Number");
                    }
            } catch(NullPointerException e) {
                System.out.println("[ERROR >> ] Invalid Game Number");
            }
        } else {
            System.out.println("Invalid input. Please enter a valid integer.");
        }
    }
    private void observeGame(Scanner scanner){
        System.out.print("Enter a game number >> ");
        if (scanner.hasNextInt()) {
            int gameNumber = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter a color perspective >> ");
            String color = scanner.nextLine().toUpperCase();

            if(currentGames.containsKey(gameNumber)){
                UserInterfaceObserver observer = new UserInterfaceObserver(authToken,new WebSocketFacade("http://localhost:8080"),currentGames.get(gameNumber).gameID(),color.equals("WHITE"));
                observer.run(scanner);
            }else{
                System.out.println("Invalid game number.");
            }

        }else{
            System.out.println("Invalid input. Please enter a valid integer.");
        }


    }


    private void printGames() throws IOException, URISyntaxException {
        GameWrapper gameList = serverFacade.listGames(new AuthData(authToken, null));
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

    private void helpLoggedIn(){
        System.out.println("""
                        Logout - Logs out user from Application
                        Create Game - Input name for new game
                        Play Game - Chose a game and color to join
                        Observe Game - View a game as an observer
                        List Games - View all active games
                        Help - List commands""");
    }

    private void helpLoggedOut(){
        System.out.println("""
                        Quite - Exit Application
                        Login - Login to play games
                        Register - Register a new account
                        Help - List commands""");
    }

}
