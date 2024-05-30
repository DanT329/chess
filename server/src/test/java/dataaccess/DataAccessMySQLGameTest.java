package dataaccess;

import dataaccess.mysql.DataAccessMySQLGame;
import model.GameData;
import model.UserData;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AppService;

import static org.junit.jupiter.api.Assertions.*;
public class DataAccessMySQLGameTest {

    @BeforeEach
    public void setUp() throws Exception {
        AppService appService = new AppService();
        appService.resetApp();
    }

    @Test
    public void addGameGood() throws SQLException, DataAccessException {
        DataAccessMySQLGame dataAccess = new DataAccessMySQLGame();
        GameData gameInfo = new GameData(0,null,null,"Mygame",null);
        GameData actualGameInfo = dataAccess.addGame(gameInfo);
        assertEquals(gameInfo.gameName(), actualGameInfo.gameName());
    }
    @Test
    public void addGameDuplicate() throws SQLException, DataAccessException {
        DataAccessMySQLGame dataAccess = new DataAccessMySQLGame();
        GameData gameInfo = new GameData(0,null,null,"Mygame",null);
        dataAccess.addGame(gameInfo);
        assertThrows(DataAccessException.class, () -> {dataAccess.addGame(gameInfo);});
    }

}
