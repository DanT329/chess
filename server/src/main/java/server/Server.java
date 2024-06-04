package server;
import dataaccess.DataAccessException;
import model.*;
import com.google.gson.Gson;
import service.*;
import com.google.gson.JsonObject;
import spark.*;
import java.util.Collection;
import server.websocket.*;
public class Server {
    private final UserService userService = new UserService();
    private final GameService gameService = new GameService();
    private final AppService appService = new AppService();
    private final WebSocketHandler webSocketHandler = new WebSocketHandler();
    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        Spark.post("/user", (req, res) -> {
            try {
                UserData user = new Gson().fromJson(req.body(), UserData.class);
                AuthData authData = userService.register(user);
                res.status(200); // OK
                return new Gson().toJson(authData);
            } catch (Exception e) {
                ErrorCall response = new ErrorCall(e);
                res.status(response.getStatusCode());
                return response.getResponse();
            }
        });
        Spark.post("/session", (req,res) -> {
            try{
                UserData user = new Gson().fromJson(req.body(), UserData.class);
                AuthData authData = userService.login(user);
                res.status(200);
                return new Gson().toJson(authData);
            }catch (Exception e) {
                ErrorCall response = new ErrorCall(e);
                res.status(response.getStatusCode());
                return response.getResponse();
            }
        });
        Spark.delete("/session", (req,res) -> {
            try {
                String authToken = req.headers("authorization");
                userService.logout(authToken);
                res.status(200);
                return new Gson().toJson(new JsonObject());
            }catch (Exception e) {
                ErrorCall response = new ErrorCall(e);
                res.status(response.getStatusCode());
                return response.getResponse();
            }
        });
        Spark.post("/game", (req,res)->{
            try{
             String authToken = req.headers("authorization");
             GameData game = new Gson().fromJson(req.body(), GameData.class);
             GameData newGame = gameService.createGame(game,authToken);
             return new Gson().toJson(newGame);
            }catch (Exception e) {
                ErrorCall response = new ErrorCall(e);
                res.status(response.getStatusCode());
                return response.getResponse();
            }
        });
        Spark.get("/game", (req,res)->{
            try{
                Collection<GameData> gameList = gameService.listGames(req.headers("authorization"));
                res.status(200);
                GameWrapper gameListWrapper = new GameWrapper(gameList);
                return new Gson().toJson(gameListWrapper);
            }catch (Exception e) {
                ErrorCall response = new ErrorCall(e);
                res.status(response.getStatusCode());
                return response.getResponse();
            }
        });
        Spark.put("/game",(req,res)->{
            try{
                String authToken = req.headers("authorization");
                GameJoinUser userInfo = new Gson().fromJson(req.body(), GameJoinUser.class);
                gameService.updateGamePlayer(userInfo,authToken);
                res.status(200);
                return new Gson().toJson(new JsonObject());
            }catch (Exception e) {
                ErrorCall response = new ErrorCall(e);
                res.status(response.getStatusCode());
                return response.getResponse();
            }
        });
        Spark.delete("/db", (req, res) -> {
            try{
                appService.resetApp();
                res.status(200);
                return new Gson().toJson(new JsonObject());
            }catch(DataAccessException e){
                res.status(500);
                ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
                return new Gson().toJson(responseMessage);
            }
        });
        Spark.awaitInitialization();
        return Spark.port();
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}