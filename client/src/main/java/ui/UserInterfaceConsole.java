package ui;
import client.ServerFacade;
import model.AuthData;
import model.UserData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class UserInterfaceConsole {
    ServerFacade serverFacade;
    private boolean loggedIn = false;
    String AUTH_TOKEN;
    String USER_NAME;

    public UserInterfaceConsole(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void run() {
        System.out.println("Welcome to Chess for CS240!");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if(!loggedIn){
                System.out.println("[LOGGED OUT >> ]");
            }else{
                System.out.println("[LOGGED IN >> ]");
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
                System.out.println("Enter a username >> ");
                String username = scanner.nextLine();
                System.out.println("Enter a password >> ");
                String password = scanner.nextLine();
                System.out.println("Enter an email");
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
            }
        }
    }
}
