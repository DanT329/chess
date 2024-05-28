package dataaccess;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataAccessMySQLUser implements DataAccessUser {

    @Override
    public void clear(){
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
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void createUser(UserData user) {
    }

    @Override
    public UserData verifyUser(UserData user) {
        return user;
    }

}
