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
    public void createUserAlreadyExists() throws SQLException,DataAccessException {
        DataAccessMySQLUser dataAccess = new DataAccessMySQLUser();
        UserData user = new UserData("example_user", "password123", "example@example.com");

        // First call to createUser should succeed
        dataAccess.createUser(user);

        // Second call to createUser should throw DataAccessException
        assertThrows(DataAccessException.class, () -> {
            dataAccess.createUser(user);
        });
    }

    @Test
    public void verifyUserGood() throws SQLException {
        DataAccessMySQLUser dataAccess = new DataAccessMySQLUser();
        UserData user = new UserData("example_user2","password123","example@example.com");
        try{dataAccess.createUser(user);}catch(DataAccessException e){}
        UserData actualUser = dataAccess.verifyUser(user);
        assertEquals(actualUser.username(),user.username());
    }
    @Test
    public void clearUserGood() throws SQLException {
        DataAccessMySQLUser dataAccess = new DataAccessMySQLUser();
        dataAccess.clear();
        assertTrue(dataAccess.isTableEmpty(), "Table should be empty after clear");
    }





}
