package dataaccess;

import model.UserData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void clearUserGood() throws SQLException {
        DataAccessMySQLUser dataAccess = new DataAccessMySQLUser();
        dataAccess.clear();
        assertTrue(dataAccess.isTableEmpty(), "Table should be empty after clear");
    }





}
