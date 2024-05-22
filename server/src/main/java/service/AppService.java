package service;
import dataaccess.DataAccessMemoryUser;
import dataaccess.DataAccessMemoryAuth;
import dataaccess.DataAccessMemoryGame;
import dataaccess.DataAccessException;


public class AppService {
    private final DataAccessMemoryUser dataAccessUser = DataAccessMemoryUser.getInstance();
    private final DataAccessMemoryAuth dataAccessAuth = DataAccessMemoryAuth.getInstance();
    private final DataAccessMemoryGame dataAccessGame = DataAccessMemoryGame.getInstance();

    public void resetApp() throws DataAccessException {
        dataAccessGame.clear();
        dataAccessUser.clear();
        dataAccessAuth.clear();
    }
}
