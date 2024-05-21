package service;

import dataaccess.DataAccessMemoryUser;
import dataaccess.DataAccessMemoryAuth;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserService {
    private final DataAccessMemoryUser dataAccessUser = DataAccessMemoryUser.getInstance();
    private final DataAccessMemoryAuth dataAccessAuth = DataAccessMemoryAuth.getInstance();

    public AuthData register(UserData user) throws BadRequestException, AlreadyTakenException, GeneralFailureException {
        // Check for missing username, password, or email
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new BadRequestException("bad request");
        }

        try {
            // Check if user already exists
            if (dataAccessUser.getUser(user) != null) {
                throw new AlreadyTakenException("already taken");
            }

            // Register user and create authentication data
            dataAccessUser.createUser(user);
            return dataAccessAuth.createAuth(user);
        } catch (DataAccessException e) {
            // General failure
            throw new GeneralFailureException("Registration failed: " + e.getMessage());
        }
    }

    public AuthData login(UserData user) throws UnauthorizedException,GeneralFailureException{
        try{
            UserData loginAttempt = dataAccessUser.verifyUser(user);
            if(loginAttempt == null){
                throw new UnauthorizedException("unauthorized");
            }

            return dataAccessAuth.createAuth(loginAttempt);

        }catch(DataAccessException e){
            throw new GeneralFailureException("Login failed: " + e.getMessage());
        }

    }
}


