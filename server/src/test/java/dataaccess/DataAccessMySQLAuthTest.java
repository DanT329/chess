package dataaccess;
import model.AuthData;
import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessMySQLAuthTest {

    @Test
    public void createAuthGood() throws SQLException,DataAccessException {
        UserData user = new UserData("user", "password","email@email.com");
        DataAccessMySQLAuth dataAccess = new DataAccessMySQLAuth();
        dataAccess.createAuth(user);
    }
}
