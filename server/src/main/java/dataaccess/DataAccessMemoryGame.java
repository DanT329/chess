package dataaccess;

import model.AuthData;
import model.GameData;
import java.util.ArrayList;
import java.util.UUID;
import java.security.SecureRandom;
import java.util.Random;

public class DataAccessMemoryGame implements DataAccessGame{
    private static DataAccessMemoryGame instance;
    final private ArrayList<GameData> gameList = new ArrayList<>();
    private int nextGameId = 1;

    private DataAccessMemoryGame(){

    }

    public static synchronized DataAccessMemoryGame getInstance() {
        if (instance == null) {
            instance = new DataAccessMemoryGame();
        }
        return instance;
    }

    public void clear(){
        gameList.clear();
        nextGameId = 1;
    }

    public GameData getGame(GameData checkGame){
        for(GameData game : gameList){
            if(game.gameName().equals(checkGame.gameName())){
                return game;
            }
        }
        return null;
    }

    public GameData addGame(GameData checkGame) throws DataAccessException {
        if (getGame(checkGame) == null) {
            int gameID = nextGameId++;
            String gameName = checkGame.gameName();
            GameData newGame = new GameData(gameID, null, null, gameName, null);
            gameList.add(newGame);
            return newGame;
        }
        throw new DataAccessException("Game already exists");
    }
}
