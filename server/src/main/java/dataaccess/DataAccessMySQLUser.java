package dataaccess;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.getAllData;
import static dataaccess.DatabaseManager.setUpDatabase;

public class DataAccessMySQLUser implements DataAccessUser {

    public DataAccessMySQLUser(){
        try{
            configureDatabase();
        }catch(DataAccessException e){
            e.printStackTrace();
        }
    }
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
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1,user.username());
                preparedStatement.setString(2,hashedPassword);
                preparedStatement.setString(3,user.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData verifyUser(UserData user) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, user.username());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedHashedPassword = resultSet.getString("password");
                        if (BCrypt.checkpw(user.password(), storedHashedPassword)) {
                            String foundUsername = resultSet.getString("username");
                            String foundEmail = resultSet.getString("email");
                            return new UserData(foundUsername, null, foundEmail); // Return user data without the password
                        }
                    }
                    return null; // User not found or password does not match
                }
            }
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //FOR TESTING ONLY
    public static boolean isTableEmpty() {
        String query = "SELECT COUNT(*) AS total FROM users";
        return getAllData(query);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` VARCHAR(50) NOT NULL,
              `password` VARCHAR(256) NOT NULL,
              `email` VARCHAR(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        setUpDatabase(createStatements);
    }
}
