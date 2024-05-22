package dataaccess;
import model.GameData;

public interface DataAccessGame {
    void clear();

    GameData getGame(GameData game);

    GameData addGame(GameData game) throws DataAccessException;


}
