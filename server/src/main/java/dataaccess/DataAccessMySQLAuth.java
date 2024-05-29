package dataaccess;
import java.sql.*;
import java.util.UUID;

import dataaccess.DataAccessAuth;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class DataAccessMySQLAuth implements DataAccessAuth{

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

    public AuthData createAuth(UserData user) throws DataAccessException{
        String query = "INSERT INTO auth (username, authtoken) VALUES (?, ?)";
        String authToken = UUID.randomUUID().toString();
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
             statement.setString(1,user.username());
             statement.setString(2,authToken);
             statement.executeUpdate();
             return new AuthData(authToken,user.username());
            }
        } catch(SQLIntegrityConstraintViolationException e){
            System.err.println("SQL Integrity Constraint Violation Exception: " + e.getMessage());
            throw new DataAccessException("User already exists");
        }catch (SQLException e) {
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

}
