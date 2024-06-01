package client;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import org.junit.jupiter.api.Assertions;
import client.ServerFacade;
import service.AppService;

import java.io.IOException;


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
    public void registerOK() throws IOException {
        AuthData auth = facade.register(new UserData("example_user","example_password","example_email"));
        Assertions.assertEquals(auth.username(),"example_user");
    }
    @Test
    public void registerDuplicate() throws IOException {
        facade.register(new UserData("example_user","example_password","example_email"));
        Assertions.assertThrows(IOException.class,()->facade.register(new UserData("example_user","example_password","example_email")));
    }

}
