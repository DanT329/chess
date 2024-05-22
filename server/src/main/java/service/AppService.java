package service;
import dataaccess.Memory.DataAccessMemoryUser;
import dataaccess.Memory.DataAccessMemoryAuth;
import dataaccess.Memory.DataAccessMemoryGame;
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
