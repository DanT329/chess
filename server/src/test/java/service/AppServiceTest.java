package service;

import model.UserData;
import model.GameData;
import dataaccess.Memory.DataAccessMemoryUser;
import dataaccess.Memory.DataAccessMemoryAuth;
import dataaccess.Memory.DataAccessMemoryGame;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTest {

    private AppService appService;

    @BeforeEach
    public void setUp() {
        appService = new AppService();
    }

    @Test
    public void testResetApp() throws DataAccessException {
        DataAccessMemoryUser.getInstance().createUser(new UserData("testUser", "password", "user@gmail.com"));
        DataAccessMemoryAuth.getInstance().createAuth(new UserData("testUser", "password", "user@gmail.com"));
        DataAccessMemoryGame.getInstance().addGame(new GameData(0,null,null,"MyGame",null));

        assertFalse(DataAccessMemoryUser.getInstance().isEmpty());
        assertFalse(DataAccessMemoryAuth.getInstance().isEmpty());
        assertFalse(DataAccessMemoryGame.getInstance().isEmpty());

        appService.resetApp();

        assertTrue(DataAccessMemoryUser.getInstance().isEmpty());
        assertTrue(DataAccessMemoryAuth.getInstance().isEmpty());
        assertTrue(DataAccessMemoryGame.getInstance().isEmpty());
    }
}

