package service;
import dataaccess.DataAccessMySQLAuth;
import dataaccess.memory.DataAccessMemoryUser;
import dataaccess.memory.DataAccessMemoryAuth;
import dataaccess.memory.DataAccessMemoryGame;
import dataaccess.DataAccessException;
import dataaccess.DataAccessMySQLUser;


public class AppService {
    //private final DataAccessMemoryUser dataAccessUser = DataAccessMemoryUser.getInstance();
    //private final DataAccessMemoryAuth dataAccessAuth = DataAccessMemoryAuth.getInstance();
    private final DataAccessMemoryGame dataAccessGame = DataAccessMemoryGame.getInstance();
    private final DataAccessMySQLUser dataAccessUser = new DataAccessMySQLUser();
    private final DataAccessMySQLAuth dataAccessAuth = new DataAccessMySQLAuth();
    public void resetApp() throws DataAccessException {
        dataAccessGame.clear();
        dataAccessUser.clear();
        dataAccessAuth.clear();
    }
}
