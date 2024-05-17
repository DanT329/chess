package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccess {

    UserData getUser(UserData user);

    void createUser(UserData user);

    AuthData createAuth(AuthData auth);

    void deleteAuth(AuthData auth);

    GameData getGames();

    GameData addGame(GameData game);

    void upDateGamePlayer(GameData game);

    void deleteTables();


}
