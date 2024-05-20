package dataaccess;
import model.AuthData;
import model.UserData;
public interface DataAccessAuth {
    void clear();

    AuthData createAuth(UserData user) throws DataAccessException;
}
