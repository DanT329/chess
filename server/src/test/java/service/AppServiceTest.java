package service;

import model.UserData;
import model.GameData;
import dataaccess.memory.DataAccessMemoryUser;
import dataaccess.memory.DataAccessMemoryAuth;
import dataaccess.memory.DataAccessMemoryGame;
import dataaccess.DataAccessMySQLUser;
import dataaccess.DataAccessMySQLAuth;
import dataaccess.DataAccessMySQLGame;
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
        appService.resetApp();

        assertTrue(DataAccessMySQLUser.isTableEmpty());
        assertTrue(DataAccessMySQLAuth.isTableEmpty());
        assertTrue(DataAccessMySQLGame.isTableEmpty());
    }
}

