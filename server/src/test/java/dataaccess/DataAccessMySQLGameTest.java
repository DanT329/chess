package dataaccess;

import dataaccess.mysql.DataAccessMySQLGame;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AppService;
import service.exception.AlreadyTakenException;

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
        assertThrows(DataAccessException.class, () -> dataAccess.addGame(gameInfo));
    }

    @Test
    public void getGameGood() throws SQLException, DataAccessException {
        DataAccessMySQLGame dataAccess = new DataAccessMySQLGame();
        GameData gameInfo = new GameData(0,null,null,"Mygame",null);
        dataAccess.addGame(gameInfo);
        GameData actualGame = dataAccess.getGame(gameInfo);
        assertEquals(gameInfo.gameName(), actualGame.gameName());
    }
    @Test
    public void getGameNull() throws SQLException, DataAccessException {
        DataAccessMySQLGame dataAccess = new DataAccessMySQLGame();
        GameData gameInfo = new GameData(0,null,null,"Mygame",null);
        dataAccess.addGame(gameInfo);
        GameData badGameInfo = new GameData(0,null,null,"BadName",null);
        GameData actualGame = dataAccess.getGame(badGameInfo);
        assertNull(actualGame);
    }

    @Test
    public void updateGameGood() throws SQLException, DataAccessException, AlreadyTakenException {
        DataAccessMySQLGame dataAccess = new DataAccessMySQLGame();
        GameData gameInfo = new GameData(0,null,null,"Mygame",null);
        dataAccess.addGame(gameInfo);
        gameInfo = dataAccess.getGame(gameInfo);
        GameData gameInfoUpdated = new GameData(gameInfo.gameID(),"Jimmy",null,"Mygame",null);
        dataAccess.updateGame(gameInfoUpdated);
        gameInfo = dataAccess.getGame(gameInfo);
        assertEquals(gameInfo.whiteUsername(), "Jimmy");
    }
    @Test
    public void updateGameAlreadyTaken() throws SQLException, DataAccessException, AlreadyTakenException {
        DataAccessMySQLGame dataAccess = new DataAccessMySQLGame();
        GameData gameInfo = new GameData(0,null,null,"Mygame",null);
        dataAccess.addGame(gameInfo);
        gameInfo = dataAccess.getGame(gameInfo);
        GameData gameInfoUpdated = new GameData(gameInfo.gameID(),"Jimmy",null,"Mygame",null);
        dataAccess.updateGame(gameInfoUpdated);
        assertThrows(AlreadyTakenException.class, () -> dataAccess.updateGame(gameInfoUpdated));
    }

    @Test
    public void listGamesGood() throws SQLException, DataAccessException {
        DataAccessMySQLGame dataAccess = new DataAccessMySQLGame();
        GameData gameInfo = new GameData(0,null,null,"Mygame",null);
        dataAccess.addGame(gameInfo);
        Collection<GameData> games = dataAccess.getAllGames();
        assertNotNull(games);
    }

    @Test
    public void listGamesEmpty() throws SQLException, DataAccessException {
        DataAccessMySQLGame dataAccess = new DataAccessMySQLGame();
        Collection<GameData> games = dataAccess.getAllGames();
        assertTrue(games.isEmpty());
    }

}
