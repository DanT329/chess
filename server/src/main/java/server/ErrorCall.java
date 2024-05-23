package server;

import dataaccess.DataAccessException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.ResponseMessage;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.GeneralFailureException;
import service.exception.UnauthorizedException;
import spark.*;

import java.util.Collection;

public class ErrorCall {
    private int statusCode;
    private String response;

    public ErrorCall(Exception e){
       this.response = handleException(e);
    }
    private String handleException (Exception e){
        if(e instanceof BadRequestException){
            statusCode = 400;
        }else if(e instanceof UnauthorizedException){
            statusCode = 401;
        }else if(e instanceof AlreadyTakenException){
            statusCode = 403;
        }else if(e instanceof GeneralFailureException){
            statusCode = 500;
        }

        ResponseMessage responseMessage = new ResponseMessage("Error: " + e.getMessage());
        return new Gson().toJson(responseMessage);
    }

    public int getStatusCode(){
        return statusCode;
    }

    public String getResponse(){
        return response;
    }
}
