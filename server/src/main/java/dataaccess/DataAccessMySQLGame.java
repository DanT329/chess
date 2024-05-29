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
    }

    public Collection<GameData> getAllGames() throws DataAccessException{
        String query = "SELECT * FROM games";
        Collection<GameData> gameList = new ArrayList<GameData>();
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                try(ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
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
}


