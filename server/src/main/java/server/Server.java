package server;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import com.google.gson.Gson;
import service.UserService;
import model.ResponseMessage;

import spark.*;

public class Server {
    private final UserService userService = new UserService();
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here
        Spark.post("/user", (req, res) -> {
            UserData user = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData = userService.register(user);

            if (authData != null) {
                res.status(200); // Created
                return new Gson().toJson(authData);
            } else {
                res.status(403); // Bad Request, e.g., user already exists
                ResponseMessage responseMessage = new ResponseMessage("Error: already taken");
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
