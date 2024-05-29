package dataaccess;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import dataaccess.DataAccessAuth;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import model.GameData;
import service.exception.AlreadyTakenException;

public class DataAccessMySQLGame implements DataAccessGame{
    public DataAccessMySQLGame() {
        try{
            configureDatabase();
        }catch(DataAccessException e){
            e.printStackTrace();
        }
    }
    public void clear(){
        String query = "DELETE FROM games";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public GameData getGame(GameData game) {
        String query = "SELECT * FROM games WHERE gamename = ?";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, game.gameName());
                try(ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
                        String gamename = resultSet.getString("gamename");
                        int gameID = resultSet.getInt("gameID");
                        String whitePlayer = resultSet.getString("whiteusername");
                        String blackPlayer = resultSet.getString("blackusername");
                        String gameInstance = resultSet.getString("game");
                        return new GameData(gameID,whitePlayer,blackPlayer,gamename,null);
                    }
                    return null;
                }
            }
        }catch(SQLException | DataAccessException e){
            return null;
        }
    }

    public GameData addGame(GameData game) throws DataAccessException{
        if(getGame(game) != null){
            throw new DataAccessException("Game already exists");
        }
        String query = "INSERT INTO games (gamename) VALUES (?)";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, game.gameName());
                statement.executeUpdate();
                return getGame(game);
                }
            } catch (SQLException ex) {
            throw new DataAccessException("Data Access Error");
            }
        }
    public void updateGame(GameData updatedGameData) throws DataAccessException, AlreadyTakenException {
        String query = "SELECT * FROM games WHERE gameID = ?";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setInt(1, updatedGameData.gameID());
                try(ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
                        String whitePlayer = resultSet.getString("whiteusername");
                        String blackPlayer = resultSet.getString("blackusername");
                        if(updatedGameData.blackUsername()!=null && blackPlayer == null){
                            String blackQuery = "UPDATE games SET blackusername = ? WHERE gameID = ?";
                            updateColor(blackQuery,connection,updatedGameData.blackUsername(), updatedGameData.gameID());
                        }else if(updatedGameData.whiteUsername()!=null && whitePlayer == null){
                            String whiteQuery = "UPDATE games SET whiteusername = ? WHERE gameID = ?";
                            updateColor(whiteQuery,connection,updatedGameData.whiteUsername(),updatedGameData.gameID());
                        }else{
                            throw new AlreadyTakenException("already taken");
                        }
                    }
                }
            }
        }catch(SQLException e){
            throw new DataAccessException("Data Access Error");
        }
    }
    private void updateColor(String query,Connection connection,String username,int gameID) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,username);
            statement.setInt(2,gameID);
            statement.executeUpdate();
        }
    }

    public Collection<GameData> getAllGames() throws DataAccessException{
        String query = "SELECT * FROM games";
        Collection<GameData> gameList = new ArrayList<GameData>();
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                try(ResultSet resultSet = statement.executeQuery()){
                    while(resultSet.next()){
                        String gamename = resultSet.getString("gamename");
                        int gameID = resultSet.getInt("gameID");
                        String whitePlayer = resultSet.getString("whiteusername");
                        String blackPlayer = resultSet.getString("blackusername");
                        String gameInstance = resultSet.getString("game");
                        gameList.add(new GameData(gameID,whitePlayer,blackPlayer,gamename,null));
                    }
                    return gameList;
                }
            }
        }catch(SQLException e){
            throw new DataAccessException("Data Access Error");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` INT AUTO_INCREMENT PRIMARY KEY,
              `whiteUsername` VARCHAR(255),
              `blackUsername` VARCHAR(255),
              `gameName` VARCHAR(255),
              'game' VARCHAR(255)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Can't create data....sorry");
        }
    }
}

