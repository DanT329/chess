package service;

import model.UserData;
import model.AuthData;
import model.GameData;
import dataaccess.Memory.DataAccessMemoryUser;
import dataaccess.Memory.DataAccessMemoryAuth;
import dataaccess.Memory.DataAccessMemoryGame;
import dataaccess.DataAccessException;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Exception.AlreadyTakenException;
import service.Exception.BadRequestException;
import service.Exception.GeneralFailureException;
import service.Exception.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;
public class GameServiceTest {
    private GameService gameService;
    private UserService userService;

    @BeforeEach
    public void setUp(){
        DataAccessMemoryUser.getInstance().clear();
        DataAccessMemoryAuth.getInstance().clear();
        DataAccessMemoryGame.getInstance().clear();
        gameService = new GameService();
        userService = new UserService();
    }

    @Test
    public void createGameGood() throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException, GeneralFailureException {
        UserData newUser = new UserData("Jerry","12345","jerry@gmail.com");
        AuthData newAuth = userService.register(newUser);

        GameData newGame = gameService.createGame(new GameData(0,null,null,"MyGame",null),newAuth.authToken());
        assertTrue(newGame.gameID() != 0);
    }

    @Test
    public void createGameNoName() throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException, GeneralFailureException {
        UserData newUser = new UserData("Jerry","12345","jerry@gmail.com");
        AuthData newAuth = userService.register(newUser);
        assertThrows(BadRequestException.class,()-> gameService.createGame(new GameData(0,null,null,null,null),newAuth.authToken()));
    }
}
