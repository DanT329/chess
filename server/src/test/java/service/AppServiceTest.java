package service;

import dataaccess.mysql.DataAccessMySQLUser;
import dataaccess.mysql.DataAccessMySQLAuth;
import dataaccess.mysql.DataAccessMySQLGame;
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

