package dataaccess;

import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataAccessMySQLUserTest {
    private DataAccessMySQLUser dataAccess;

    @Test
    public void getUserGood() throws SQLException {
        dataAccess = new DataAccessMySQLUser();
        UserData user = new UserData("example_user","password123","example@example.com");
        UserData actualUser = dataAccess.getUser(user);
        // Print username, email, and password of the actualUser to the console
        System.out.println("Username: " + actualUser.username());
        System.out.println("Email: " + actualUser.email());
        System.out.println("Password: " + actualUser.password());
        assertEquals(actualUser.username(),user.username());
    }

}
