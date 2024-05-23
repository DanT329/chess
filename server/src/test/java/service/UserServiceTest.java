package service;

import model.UserData;
import model.AuthData;
import model.GameData;
import dataaccess.Memory.DataAccessMemoryUser;
import dataaccess.Memory.DataAccessMemoryAuth;
import dataaccess.Memory.DataAccessMemoryGame;
import service.Exception.AlreadyTakenException;
import service.Exception.BadRequestException;
import service.Exception.GeneralFailureException;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Exception.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;
public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        DataAccessMemoryUser.getInstance().clear();
        DataAccessMemoryAuth.getInstance().clear();
        DataAccessMemoryGame.getInstance().clear();
        userService = new UserService();
    }

    @Test
    public void testRegisterGood() throws BadRequestException, AlreadyTakenException, GeneralFailureException {
        UserData newUser = new UserData("Jerry","12345","jerry@gmail.com");
        AuthData expected = userService.register(newUser);

        assert(expected.username().equals(newUser.username()));
        assert(expected.authToken() != null);
    }

    @Test
    public void testRegisterDuplicate() throws BadRequestException, AlreadyTakenException, GeneralFailureException {
        UserData newUser = new UserData("Jerry", "12345", "jerry@gmail.com");
        UserData duplicateUser = new UserData("Jerry", "12345", "jerry@gmail.com");

        userService.register(newUser);
        assertThrows(AlreadyTakenException.class, () -> {
            userService.register(duplicateUser);
        });
    }

    @Test
    public void testLoginGood() throws BadRequestException, AlreadyTakenException, GeneralFailureException, UnauthorizedException {
        UserData newUser = new UserData("Jerry", "12345", "jerry@gmail.com");
        AuthData expectedAuth = userService.register(newUser);
        AuthData actualAuth = userService.login(newUser);
        assertEquals(expectedAuth.username(),actualAuth.username());

    }

    @Test
    public void testLoginWrongPassword() throws BadRequestException, AlreadyTakenException, GeneralFailureException, UnauthorizedException {
        UserData newUser = new UserData("Jerry", "12345", "jerry@gmail.com");
        UserData loginAttempt = new UserData("Jerry", "abcd", null);
        userService.register(newUser);
        assertThrows(UnauthorizedException.class, ()-> userService.login(loginAttempt));

    }

    @Test
    public void testLogoutGood() throws BadRequestException, AlreadyTakenException, GeneralFailureException, UnauthorizedException {
        UserData newUser = new UserData("Jerry", "12345", "jerry@gmail.com");
        AuthData userAuth = userService.register(newUser);
        userService.logout(userAuth.authToken());
        assertTrue(DataAccessMemoryAuth.getInstance().isEmpty());

    }

    @Test
    public void testLogoutBadToken() throws BadRequestException, AlreadyTakenException, GeneralFailureException, UnauthorizedException {
        UserData newUser = new UserData("Jerry", "12345", "jerry@gmail.com");
        userService.register(newUser);
        AuthData badAuth = new AuthData("badToken","Jerry");
        assertThrows(UnauthorizedException.class,()->userService.logout(badAuth.authToken()));


    }

}
