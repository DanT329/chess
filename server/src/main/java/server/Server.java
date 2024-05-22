package server;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import model.GameData;
import com.google.gson.Gson;
import service.*;
import model.ResponseMessage;
import com.google.gson.JsonObject;

import spark.*;

public class Server {
    private final UserService userService = new UserService();
    private final GameService gameService = new GameService();
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", (req, res) -> {
            try {
                UserData user = new Gson().fromJson(req.body(), UserData.class);
                AuthData authData = userService.register(user);

                res.status(200); // OK
                return new Gson().toJson(authData);
            } catch (BadRequestException e) {
                res.status(400); // Bad Request
                ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
                return new Gson().toJson(responseMessage);
            } catch (AlreadyTakenException e) {
                res.status(403); // Already taken
                ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
                return new Gson().toJson(responseMessage);
            } catch (GeneralFailureException e) {
                res.status(500); // Internal Server Error
                ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
                return new Gson().toJson(responseMessage);
            }
        });

        Spark.post("/session", (req,res) -> {
            try{
                UserData user = new Gson().fromJson(req.body(), UserData.class);
                AuthData authData = userService.login(user);
                res.status(200);
                return new Gson().toJson(authData);
            }catch(UnauthorizedException e){
                res.status(401);
                ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
                return new Gson().toJson(responseMessage);
            }catch(GeneralFailureException e){
                res.status(500);
                ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
                return new Gson().toJson(responseMessage);
            }
        });

        Spark.delete("/session", (req,res) -> {
            try {
                String authToken = req.headers("authorization");
                userService.logout(authToken);
                res.status(200);
                return new Gson().toJson(new JsonObject());
            }catch(UnauthorizedException e){
                res.status(401);
                ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
                return new Gson().toJson(responseMessage);
            }catch(GeneralFailureException e){
                res.status(500);
                ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
                return new Gson().toJson(responseMessage);
            }
        });

        Spark.post("/game", (req,res)->{
            try{
             String authToken = req.headers("authorization");
             GameData game = new Gson().fromJson(req.body(), GameData.class);
             GameData newGame = gameService.createGame(game,authToken);
             return new Gson().toJson(newGame);
            }catch(UnauthorizedException e){
                res.status(401);
                ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
                return new Gson().toJson(responseMessage);
            }catch(GeneralFailureException e){
                res.status(500);
                ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
                return new Gson().toJson(responseMessage);
            }catch(BadRequestException e){
                res.status(400);
                ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
                return new Gson().toJson(responseMessage);
            }
        });

        Spark.delete("/db", (req, res) -> {
            res.status(200); // Always return 200 OK
            return ""; // Return an empty response body
        });



        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
