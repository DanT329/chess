package client;

import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import org.junit.jupiter.api.Assertions;
import client.ServerFacade;
import service.AppService;
import dataaccess.DataAccessMySQLAuth;
import dataaccess.DataAccessMySQLGame;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() throws DataAccessException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("localhost",port);
    }
    @BeforeEach
    public void initTest() throws DataAccessException {
        AppService appService = new AppService();
        appService.resetApp();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerOK() throws IOException, URISyntaxException {
        AuthData auth = facade.register(new UserData("example_user","example_password","example_email"));
        Assertions.assertEquals(auth.username(),"example_user");
    }
    @Test
    public void registerDuplicate() throws IOException, URISyntaxException {
        facade.register(new UserData("example_user","example_password","example_email"));
        Assertions.assertThrows(IOException.class,()->facade.register(new UserData("example_user","example_password","example_email")));
    }

    @Test
    public void loginGoodAuth() throws IOException, URISyntaxException {
        facade.register(new UserData("example_user","example_password","example_email"));
        AuthData auth = facade.login(new UserData("example_user","example_password",null));
        Assertions.assertEquals(auth.username(),"example_user");
    }

    @Test
    public void loginBadPassword() throws IOException, URISyntaxException {
        facade.register(new UserData("example_user","example_password","example_email"));
        Assertions.assertThrows(IOException.class,()->facade.login(new UserData("bad_password","example_password","example_email")));

    }

    @Test
    public void logoutGoodAuth() throws IOException, URISyntaxException {
        AuthData auth = facade.register(new UserData("example_user","example_password","example_email"));
        facade.logout(auth);
        DataAccessMySQLAuth dba = new DataAccessMySQLAuth();
        Assertions.assertTrue(DataAccessMySQLAuth.isTableEmpty());
    }

    @Test
    public void logoutBadAuth() throws IOException, URISyntaxException {
        facade.register(new UserData("example_user","example_password","example_email"));
        AuthData auth = new AuthData("BadToken","example_user");
        Assertions.assertThrows(IOException.class,()->facade.logout(auth));
    }

    @Test
    public void createGameGood() throws IOException, URISyntaxException {
        AuthData auth = facade.register(new UserData("example_user","example_password","example_email"));
        GameData game = new GameData(0,null,null,"My_game",null);
        game = facade.createGame(auth,game);
        Assertions.assertNotNull(game);
        Assertions.assertFalse(DataAccessMySQLGame.isTableEmpty());
    }
    @Test
    public void createGameBadAuth() throws IOException, URISyntaxException {
        facade.register(new UserData("example_user","example_password","example_email"));
        GameData game = new GameData(0,null,null,"My_game",null);
        Assertions.assertThrows(IOException.class,()->facade.createGame(new AuthData("BadToken",null),game));
    }

    @Test
    public void listGamesGood() throws IOException, URISyntaxException {
        AuthData auth = facade.register(new UserData("example_user","example_password","example_email"));
        facade.createGame(auth,new GameData(0,null,null,"My_game",null));
        facade.createGame(auth,new GameData(0,null,null,"My_game2",null));
        GameWrapper gameList = facade.listGames(auth);
        Assertions.assertEquals(gameList.games().size(),2);
    }

    @Test
    public void listGamesBadAuth() throws IOException, URISyntaxException {
        AuthData auth = facade.register(new UserData("example_user","example_password","example_email"));
        facade.createGame(auth,new GameData(0,null,null,"My_game",null));
        facade.createGame(auth,new GameData(0,null,null,"My_game2",null));
        Assertions.assertThrows(IOException.class,()->facade.listGames(new AuthData("BadToken",null)));
    }

    @Test
    public void joinGameGood() throws IOException, URISyntaxException {
        AuthData auth = facade.register(new UserData("example_user","example_password","example_email"));
        GameData game = facade.createGame(auth,new GameData(0,null,null,"My_game",null));
        facade.joinGame(new GameJoinUser("WHITE", game.gameID(), auth.authToken()));
        GameWrapper gameList = facade.listGames(auth);
        ArrayList<GameData> gameArrayList = new ArrayList<>(gameList.games());
        GameData checkGame = gameArrayList.get(0);
        Assertions.assertEquals(checkGame.whiteUsername(),"example_user");
    }

    @Test
    public void joinGameAlreadyTaken() throws IOException, URISyntaxException {
        AuthData auth = facade.register(new UserData("example_user","example_password","example_email"));
        GameData game = facade.createGame(auth,new GameData(0,null,null,"My_game",null));
        facade.joinGame(new GameJoinUser("WHITE", game.gameID(), auth.authToken()));
        Assertions.assertThrows(IOException.class,()->facade.joinGame(new GameJoinUser("WHITE", game.gameID(), auth.authToken())));
    }
}
