package service;
import dataaccess.DataAccessMySQLAuth;
import dataaccess.DataAccessMySQLGame;
import dataaccess.DataAccessException;
import dataaccess.DataAccessMySQLUser;


public class AppService {

    private final DataAccessMySQLUser dataAccessUser = new DataAccessMySQLUser();
    private final DataAccessMySQLAuth dataAccessAuth = new DataAccessMySQLAuth();
    private final DataAccessMySQLGame dataAccessGame = new DataAccessMySQLGame();
    public void resetApp() throws DataAccessException {
        dataAccessGame.clear();
        dataAccessUser.clear();
        dataAccessAuth.clear();
    }
}
