package dataaccess;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataAccessMySQLUser implements DataAccessUser {

    @Override
    public void clear(){
        String query = "DELETE FROM users";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.executeUpdate();
            }
        }catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public UserData getUser(UserData user) {
        String username = user.username();
        String query = "SELECT * FROM users WHERE username = ?";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1,username);
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    if(resultSet.next()){
                        String foundUsername = resultSet.getString("username");
                        String foundPassword = resultSet.getString("password");
                        String foundEmail = resultSet.getString("email");
                        return new UserData(foundUsername, foundPassword, foundEmail);
                    }
                    return null;
                }
            }
        }catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if(getUser(user) != null){
            throw new DataAccessException("User already exists");
        }
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1,user.username());
                preparedStatement.setString(2,user.password());
                preparedStatement.setString(3,user.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData verifyUser(UserData user) {
        return user;
    }

    //FOR TESTING ONLY
    public boolean isTableEmpty() {
        String query = "SELECT COUNT(*) AS total FROM users";
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
