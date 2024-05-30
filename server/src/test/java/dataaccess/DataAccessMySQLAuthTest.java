package dataaccess;
import dataaccess.mysql.DataAccessMySQLAuth;
import model.AuthData;
import model.UserData;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AppService;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessMySQLAuthTest {

    @BeforeEach
    public void setUp() throws Exception {
        AppService appService = new AppService();
        appService.resetApp();

    }
    @Test
    public void createAuthGood() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        AuthData auth = dataAccess.createAuth(user);
        assertEquals(auth.username(),user.username());
    }

    @Test
    public void createAuthSeveral() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        AuthData auth = dataAccess.createAuth(user);
        auth = dataAccess.createAuth(user);
        assertEquals(auth.username(),user.username());
    }

    @Test
    public void verifyAuthGood() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        AuthData auth = dataAccess.createAuth(user);
        AuthData actual = dataAccess.verifyToken(auth.authToken());
        assertEquals(auth.username(),actual.username());
    }

    @Test
    public void verifyAuthBadToken() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        AuthData auth = dataAccess.createAuth(user);
        AuthData actual = dataAccess.verifyToken("BadToken");
        assertNull(actual);
    }

    @Test
    public void deleteAuthTokenGood() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        AuthData auth = dataAccess.createAuth(user);
        dataAccess.deleteAuth(auth);
        assertNull(dataAccess.verifyToken(auth.authToken()));
    }

    @Test
    public void deleteAuthTokenMultiple() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        AuthData auth = dataAccess.createAuth(user);
        dataAccess.deleteAuth(auth);
        dataAccess.deleteAuth(auth);
        assertNull(dataAccess.verifyToken(auth.authToken()));
    }
}
