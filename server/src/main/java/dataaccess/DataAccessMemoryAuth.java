package dataaccess;
import java.util.UUID;
import java.security.SecureRandom;
import java.util.Random;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;

public class DataAccessMemoryAuth implements DataAccessAuth{
    private static DataAccessMemoryAuth instance;
    final private ArrayList<AuthData> authList = new ArrayList<>();

    private DataAccessMemoryAuth() {
        // private constructor to prevent instantiation
    }

    public static synchronized DataAccessMemoryAuth getInstance() {
        if (instance == null) {
            instance = new DataAccessMemoryAuth();
        }
        return instance;
    }

    public void clear(){
        authList.clear();
    }

    public AuthData createAuth(UserData user) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, user.username());
        if (!authList.contains(newAuth)) {
            authList.add(newAuth);
            return newAuth;
        } else {
            throw new DataAccessException("User already exists");
        }
    }

}



