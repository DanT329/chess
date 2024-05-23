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

}
