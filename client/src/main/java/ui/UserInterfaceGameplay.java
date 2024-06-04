package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import model.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import client.websocket.WebSocketFacade;


public class UserInterfaceGameplay {

    private final String authToken;
    private WebSocketFacade webSocketFacade;
    private ChessGame chessGame;
    private ChessBoard chessBoard;
    private final Integer gameID;
    public UserInterfaceGameplay(String authToken, WebSocketFacade webSocketFacade, Integer gameID) {
        this.authToken = authToken;
        this.webSocketFacade = webSocketFacade;
        this.gameID = gameID;
    }

    public void run(){
        System.out.println("Starting Game! Enter the command:Help");
        //TODO: Add commands and interaction with websocket
        //Open a socket
        //Send CONNECT to server
        webSocketFacade.playGame(gameID,authToken);
    }
}
