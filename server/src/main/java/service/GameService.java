package service;

import dataaccess.*;
import dataaccess.memory.DataAccessMemoryAuth;
import dataaccess.memory.DataAccessMemoryGame;
import dataaccess.DataAccessMySQLGame;
import model.AuthData;
import model.GameData;
import server.GameJoinUser;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.GeneralFailureException;
import service.exception.UnauthorizedException;

import java.util.Collection;

public class GameService {
    //private final DataAccessMemoryAuth dataAccessAuth = DataAccessMemoryAuth.getInstance();
    //private final DataAccessMemoryGame dataAccessGame = DataAccessMemoryGame.getInstance();
    private final DataAccessMySQLAuth dataAccessAuth = new DataAccessMySQLAuth();
    private final DataAccessMySQLGame dataAccessGame = new DataAccessMySQLGame();
    public GameData createGame(GameData game, String authToken) throws BadRequestException, GeneralFailureException, UnauthorizedException {
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

    public Collection<GameData> listGames(String authToken) throws GeneralFailureException, UnauthorizedException {
        try{
            AuthData auth = dataAccessAuth.verifyToken(authToken);
            if(auth == null){
                throw new UnauthorizedException("unauthorized");
            }
            return dataAccessGame.getAllGames();
        }catch(DataAccessException e){
            throw new GeneralFailureException(e.getMessage());
        }
    }

    public void updateGamePlayer(GameJoinUser gameInfo,String authToken) throws GeneralFailureException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        try{
            AuthData auth = dataAccessAuth.verifyToken(authToken);
            if(auth == null){
                throw new UnauthorizedException("unauthorized");
            }
            if(gameInfo.playerColor() == null || gameInfo.gameID() == 0){
                throw new BadRequestException("bad request");
            }
            GameData game;
            if(gameInfo.playerColor().equals("WHITE")){
                game = new GameData(gameInfo.gameID(),auth.username(),null,null, null);
            }else{
                game = new GameData(gameInfo.gameID(), null, auth.username(), null, null);
            }

            dataAccessGame.updateGame(game);

        }catch(DataAccessException e){
            throw new GeneralFailureException(e.getMessage());
        }

    }
}
