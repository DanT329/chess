package ui;
import client.ServerFacade;
import model.AuthData;
import model.GameData;
import model.GameWrapper;
import model.UserData;

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
}
