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

}
