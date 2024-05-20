package service;

import dataaccess.DataAccessMemoryUser;
import dataaccess.DataAccessMemoryAuth;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserService {
    private final DataAccessMemoryUser dataAccessUser = DataAccessMemoryUser.getInstance();
    private final DataAccessMemoryAuth dataAccessAuth = DataAccessMemoryAuth.getInstance();

    public AuthData register(UserData user) {
        try {
            if (dataAccessUser.getUser(user) == null) {
                dataAccessUser.createUser(user);
                return dataAccessAuth.createAuth(user);
            } else {
                // User already exists, handle accordingly
                return null; // Or return an appropriate response
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}

