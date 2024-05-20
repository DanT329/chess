package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;

public class DataAccessMemoryAuth implements DataAccessAuth{
    final private ArrayList<AuthData> authList = new ArrayList<>();

    public void clear(){
        authList.clear();
    }

    public AuthData createAuth(UserData user) throws DataAccessException{
        AuthData newAuth = new AuthData(user.username(),"1234");
        if(!authList.contains(newAuth)){
            authList.add(newAuth);
            return newAuth;
        }else{
            throw new DataAccessException("User already exists");
        }
    }
}



