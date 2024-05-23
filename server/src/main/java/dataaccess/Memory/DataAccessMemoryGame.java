package dataaccess.Memory;

import dataaccess.DataAccessException;
import dataaccess.DataAccessGame;
import model.GameData;
import service.Exception.AlreadyTakenException;

import java.util.ArrayList;
import java.util.Collection;

public class DataAccessMemoryGame implements DataAccessGame {
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

    public Collection<GameData> getAllGames() throws DataAccessException {
        return new ArrayList<>(gameList);
    }

    public void updateGame(GameData updatedGameData) throws DataAccessException, AlreadyTakenException {
        for (int i = 0; i < gameList.size(); i++) {
            GameData gameData = gameList.get(i);
            if (gameData.gameID() == updatedGameData.gameID()) {
                // Create a new GameData record with updated values
                GameData updatedGame;
                if(updatedGameData.blackUsername() == null && gameData.whiteUsername() == null){
                            updatedGame = new GameData(
                            gameData.gameID(),
                            updatedGameData.whiteUsername(),
                            gameData.blackUsername(),
                            gameData.gameName(),
                            gameData.game()
                    );
                }else if(updatedGameData.whiteUsername() == null && gameData.blackUsername() == null){
                            updatedGame = new GameData(
                            gameData.gameID(),
                            gameData.whiteUsername(),
                            updatedGameData.blackUsername(),
                            gameData.gameName(),
                            gameData.game()
                    );
                }else{
                    throw new AlreadyTakenException("already taken");
                }


                // Replace the old record with the new one
                gameList.set(i, updatedGame);
                return;
            }
        }
        throw new DataAccessException("Game not found");
    }
    // Package-private method for testing
    public boolean isEmpty() {
        return gameList.isEmpty();
    }

}
