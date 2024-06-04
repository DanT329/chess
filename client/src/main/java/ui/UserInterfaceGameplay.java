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
    private final boolean isWhite;
    public UserInterfaceGameplay(String authToken, WebSocketFacade webSocketFacade, Integer gameID, boolean isWhite) {
        this.authToken = authToken;
        this.webSocketFacade = webSocketFacade;
        this.gameID = gameID;
        this.isWhite = isWhite;
    }

    public void run(){
        System.out.println("Starting Game! Enter the command:Help");
        webSocketFacade.playGame(gameID,authToken);
        //TODO: Set Start board in database
        //TODO: Print start board to screen
        //TODO: Implement make move
    }

    public void startBoard(){

    }

    public void printBoard(){

    }
}
