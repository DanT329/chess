package service;

import dataaccess.DataAccessMemoryUser;
import dataaccess.DataAccessMemoryAuth;
import dataaccess.DataAccessMemoryGame;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

public class GameService {
    private final DataAccessMemoryUser dataAccessUser = DataAccessMemoryUser.getInstance();
    private final DataAccessMemoryAuth dataAccessAuth = DataAccessMemoryAuth.getInstance();
    private final DataAccessMemoryGame dataAccessGame = DataAccessMemoryGame.getInstance();

    public GameData createGame(GameData game, String authToken) throws BadRequestException,GeneralFailureException,UnauthorizedException {
        if(game.gameName() == null || game.gameName().isEmpty()){
            throw new BadRequestException("bad request");
        }
        try{
            AuthData user = dataAccessAuth.verifyToken(authToken);
            if(user == null){
                throw new UnauthorizedException("unauthorized");
            }
            return dataAccessGame.addGame(game);

        }catch(DataAccessException e){
            throw new GeneralFailureException(e.getMessage());
        }
    }
}
