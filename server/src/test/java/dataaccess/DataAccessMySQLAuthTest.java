package dataaccess;
import model.AuthData;
import model.UserData;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessMySQLAuthTest {
    @BeforeEach
    public void setUp() throws Exception {
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        dataAccess.clear();
    }
    @Test
    public void createAuthGood() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        dataAccess.createAuth(user);
    }

    @Test
    public void createAuthDuplicate() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        dataAccess.createAuth(user);

        assertThrows(DataAccessException.class, ()-> dataAccess.createAuth(user));
    }

    @Test
    public void verifyTokenGood() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        String authToken = dataAccess.createAuth(user).authToken();
        assertNotNull(dataAccess.verifyToken(authToken));
    }

    @Test
    public void verifyTokenBad() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        dataAccess.createAuth(user).authToken();
        assertNull(dataAccess.verifyToken("bad_token"));
    }

    @Test
    public void deleteTokenGood() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        String authToken = dataAccess.createAuth(user).authToken();
        dataAccess.deleteAuth(new AuthData(authToken,"Jerry"));
        assertNull(dataAccess.verifyToken(authToken));
    }
}
