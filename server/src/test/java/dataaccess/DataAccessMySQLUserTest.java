package dataaccess;

import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessMySQLUserTest {

    @Test
    public void getUserGood() throws SQLException {
        DataAccessMySQLUser dataAccess = new DataAccessMySQLUser();
        UserData user = new UserData("example_user","password123","example@example.com");
        UserData actualUser = dataAccess.getUser(user);
        assertEquals(actualUser.username(),user.username());
    }

    @Test
    public void createUserGood() throws SQLException {
        DataAccessMySQLUser dataAccess = new DataAccessMySQLUser();
        UserData user = new UserData("example_user","password123","example@example.com");
        try{
            dataAccess.createUser(user);
        }catch(DataAccessException e){
            System.out.println(e);
        }

    }


    @Test
    public void verifyUserValidCredentials() throws DataAccessException {
        DataAccessMySQLUser dataAccess = new DataAccessMySQLUser();
        UserData user = new UserData("existing_user", "password123", "existing@example.com");
        dataAccess.createUser(user);
        UserData verifiedUser = dataAccess.verifyUser(user);
        assertEquals(user.username(), verifiedUser.username(), "User should be verified successfully");
    }

    // Negative test for verifyUser
    @Test
    public void verifyUserInvalidCredentials() throws DataAccessException {
        DataAccessMySQLUser dataAccess = new DataAccessMySQLUser();
        UserData user = new UserData("existing_user", "invalid_password", "existing@example.com");
        UserData verifiedUser = dataAccess.verifyUser(user);
        assertNull(verifiedUser, "User should not be verified with invalid credentials");
    }
    @Test
    public void clearUserGood() throws SQLException {
        DataAccessMySQLUser dataAccess = new DataAccessMySQLUser();
        dataAccess.clear();
        assertTrue(dataAccess.isTableEmpty(), "Table should be empty after clear");
    }





}
