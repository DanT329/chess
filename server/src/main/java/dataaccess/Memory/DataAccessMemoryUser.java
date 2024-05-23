package dataaccess.Memory;

import dataaccess.DataAccessException;
import dataaccess.DataAccessUser;
import model.UserData;
import java.util.ArrayList;

public class DataAccessMemoryUser implements DataAccessUser {
    private static DataAccessMemoryUser instance;
    private final ArrayList<UserData> users = new ArrayList<>();

    private DataAccessMemoryUser() {
        // private constructor to prevent instantiation
    }

    public static synchronized DataAccessMemoryUser getInstance() {
        if (instance == null) {
            instance = new DataAccessMemoryUser();
        }
        return instance;
    }

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public UserData getUser(UserData user) {
        for (UserData userData : users) {
            if (userData.username().equals(user.username())) {
                return userData;
            }
        }
        return null;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (!users.contains(user)) {
            users.add(user);
        } else {
            throw new DataAccessException("User Already In Database");
        }
    }

    @Override
    public UserData verifyUser(UserData user) {
        UserData checkUser = getUser(user);
        if (checkUser != null
                && checkUser.username().equals(user.username())
                && checkUser.password().equals(user.password())) {
            return checkUser;
        }
        return null;
    }
    // Package-private method for testing
    public boolean isEmpty() {
        return users.isEmpty();
    }
}

