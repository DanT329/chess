package dataaccess;

import model.UserData;

public interface DataAccessUser {

    void clear();

    UserData getUser(UserData user);

    void createUser(UserData user) throws DataAccessException;

    UserData verifyUser(UserData user);
}
