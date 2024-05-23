package service;

import model.UserData;
import model.AuthData;
import model.GameData;
import dataaccess.memory.DataAccessMemoryUser;
import dataaccess.memory.DataAccessMemoryAuth;
import dataaccess.memory.DataAccessMemoryGame;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.GeneralFailureException;
import service.exception.UnauthorizedException;
import server.GameJoinUser;
import java.util.ArrayList;

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

    @Test
    public void listGamesGood() throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException, GeneralFailureException {
        UserData newUser = new UserData("Jerry","12345","jerry@gmail.com");
        AuthData newAuth = userService.register(newUser);
        gameService.createGame(new GameData(0,null,null,"MyGame",null),newAuth.authToken());
        gameService.createGame(new GameData(0,null,null,"MyGameTwo",null),newAuth.authToken());
        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(new GameData(1,null,null,"MyGame",null));
        expected.add(new GameData(2,null,null,"MyGameTwo",null));
        assertEquals(expected,gameService.listGames(newAuth.authToken()));
    }

    @Test
    public void listGamesBadToken() throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException, GeneralFailureException {
        UserData newUser = new UserData("Jerry","12345","jerry@gmail.com");
        userService.register(newUser);
        assertThrows(UnauthorizedException.class,()->gameService.listGames("badToken"));
    }

    @Test
    public void updateGameGood() throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException, GeneralFailureException {
        UserData newUser = new UserData("Jerry","12345","jerry@gmail.com");
        AuthData newAuth = userService.register(newUser);
        gameService.createGame(new GameData(0,null,null,"MyGame",null),newAuth.authToken());
        gameService.createGame(new GameData(0,null,null,"MyGameTwo",null),newAuth.authToken());
        gameService.updateGamePlayer(new GameJoinUser("WHITE",1,newAuth.authToken()), newAuth.authToken());

        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(new GameData(1,"Jerry",null,"MyGame",null));
        expected.add(new GameData(2,null,null,"MyGameTwo",null));
        assertEquals(expected,gameService.listGames(newAuth.authToken()));
    }

    @Test
    public void updateGameAlreadyTaken() throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException, GeneralFailureException {
        UserData newUser = new UserData("Jerry","12345","jerry@gmail.com");
        AuthData newAuth = userService.register(newUser);
        gameService.createGame(new GameData(0,null,null,"MyGame",null),newAuth.authToken());
        gameService.createGame(new GameData(0,null,null,"MyGameTwo",null),newAuth.authToken());
        gameService.updateGamePlayer(new GameJoinUser("WHITE",1,newAuth.authToken()), newAuth.authToken());


        assertThrows(AlreadyTakenException.class,()-> gameService.updateGamePlayer(new GameJoinUser("WHITE",1,newAuth.authToken()), newAuth.authToken()));
    }
}
