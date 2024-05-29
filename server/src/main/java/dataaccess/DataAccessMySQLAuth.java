package dataaccess;
import java.sql.*;
import java.util.UUID;

import dataaccess.DataAccessAuth;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class DataAccessMySQLAuth implements DataAccessAuth{
    public DataAccessMySQLAuth() {
        try{
            configureDatabase();
        }catch(DataAccessException e){
            e.printStackTrace();
        }
    }
    public void clear() {
        String query = "DELETE FROM auth";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthData createAuth(UserData user) throws DataAccessException {
        String checkQuery = "SELECT COUNT(*) FROM auth WHERE username = ?";
        String insertQuery = "INSERT INTO auth (username, authtoken) VALUES (?, ?)";
        String authToken = UUID.randomUUID().toString();

        try (Connection connection = DatabaseManager.getConnection()) {
            // Check if the username already exists
            try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
                statement.setString(1, user.username());
                ResultSet rs = statement.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new DataAccessException("User already exists");
                }
            }

            // If the username doesn't exist, insert the new user
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setString(1, user.username());
                statement.setString(2, authToken);
                statement.executeUpdate();
                return new AuthData(authToken, user.username());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public AuthData verifyToken(String authToken) throws DataAccessException{
        String query = "SELECT * FROM auth WHERE authtoken= ?";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1,authToken);
                try(ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
                        return new AuthData(resultSet.getString("authtoken"),resultSet.getString("username"));
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAuth(AuthData auth) throws DataAccessException{
        String query = "DELETE FROM auth WHERE authtoken= ?";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1,auth.authToken());
                statement.executeUpdate();
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `username` VARCHAR(255),
              `authtoken` VARCHAR(255)
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

    //FOR TESTING ONLY
    public static boolean isTableEmpty() {
        String query = "SELECT COUNT(*) AS total FROM auth";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    if(resultSet.next()){
                        int count = resultSet.getInt("total");
                        return count == 0;
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
