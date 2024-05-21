package server;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import com.google.gson.Gson;
import service.*;
import model.ResponseMessage;

import spark.*;

public class Server {
    private final UserService userService = new UserService();
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
