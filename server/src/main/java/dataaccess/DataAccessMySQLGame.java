package dataaccess;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;
import service.exception.AlreadyTakenException;

import static dataaccess.DatabaseManager.getAllData;
import static dataaccess.DatabaseManager.getAllData;
import static dataaccess.DatabaseManager.setUpDatabase;

public class DataAccessMySQLGame implements DataAccessGame {
    public DataAccessMySQLGame() {
        try{
            configureDatabase();
        }catch(DataAccessException e){
            e.printStackTrace();
        }
    }
    public void clear(){
        String query = "DELETE FROM games";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public GameData getGame(GameData game) {
        String query = "SELECT * FROM games WHERE gamename = ?";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, game.gameName());
                try(ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
                        String gamename = resultSet.getString("gamename");
                        int gameID = resultSet.getInt("gameID");
                        String whitePlayer = resultSet.getString("whiteusername");
                        String blackPlayer = resultSet.getString("blackusername");
                        String gameInstance = resultSet.getString("game");
                        ChessGame gameInfo = null;
                        if(gameInstance != null){
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
                                    .create();
                            gameInfo = gson.fromJson(gameInstance, ChessGame.class);
                        }
                        return new GameData(gameID,whitePlayer,blackPlayer,gamename,gameInfo);
                    }
                    return null;
                }
            }
        }catch(SQLException | DataAccessException e){
            return null;
        }
    }

    public GameData addGame(GameData game) throws DataAccessException{
        if(getGame(game) != null){
            throw new DataAccessException("Game already exists");
        }
        String query = "INSERT INTO games (gamename) VALUES (?)";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, game.gameName());
                statement.executeUpdate();
                return getGame(game);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Data Access Error");
        }
    }
    public void updateGame(GameData updatedGameData) throws DataAccessException, AlreadyTakenException {
        String query = "SELECT * FROM games WHERE gameID = ?";
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setInt(1, updatedGameData.gameID());
                try(ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
                        String whitePlayer = resultSet.getString("whiteusername");
                        String blackPlayer = resultSet.getString("blackusername");
                        if(updatedGameData.blackUsername()!=null && blackPlayer == null){
                            String blackQuery = "UPDATE games SET blackusername = ? WHERE gameID = ?";
                            updateColor(blackQuery,connection,updatedGameData.blackUsername(), updatedGameData.gameID());
                        }else if(updatedGameData.whiteUsername()!=null && whitePlayer == null){
                            String whiteQuery = "UPDATE games SET whiteusername = ? WHERE gameID = ?";
                            updateColor(whiteQuery,connection,updatedGameData.whiteUsername(),updatedGameData.gameID());
                        }else{
                            throw new AlreadyTakenException("already taken");
                        }
                    }
                }
            }
        }catch(SQLException e){
            throw new DataAccessException("Data Access Error");
        }
    }
    private void updateColor(String query,Connection connection,String username,int gameID) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,username);
            statement.setInt(2,gameID);
            statement.executeUpdate();
        }
    }

    public Collection<GameData> getAllGames() throws DataAccessException{
        String query = "SELECT * FROM games";
        Collection<GameData> gameList = new ArrayList<GameData>();
        try(Connection connection = DatabaseManager.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){
                try(ResultSet resultSet = statement.executeQuery()){
                    while(resultSet.next()){
                        String gamename = resultSet.getString("gamename");
                        int gameID = resultSet.getInt("gameID");
                        String whitePlayer = resultSet.getString("whiteusername");
                        String blackPlayer = resultSet.getString("blackusername");
                        String gameInstance = resultSet.getString("game");
                        ChessGame gameInfo = null;
                        //if(gameInstance != null){
                           // Gson gson = new GsonBuilder()
                            //        .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
                             //       .create();
                            //gameInfo = gson.fromJson(gameInstance, ChessGame.class);
                        //}

                        gameList.add(new GameData(gameID,whitePlayer,blackPlayer,gamename,gameInfo));
                    }
                    return gameList;
                }
            }
        }catch(SQLException e){
            throw new DataAccessException("Data Access Error");
        }
    }

    public String loadGame(int gameID) throws DataAccessException, SQLException {
        String query = "SELECT * FROM games WHERE gameID = ?";
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, gameID);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String gameState = resultSet.getString("game");
                        if (gameState != null) {
                            return gameState;
                        } else {
                            ChessGame newGame = new ChessGame();  // Create a new ChessGame instance
                            Gson gson = new Gson();
                            String serializedGame = gson.toJson(newGame);
                            pushGame(gameID, serializedGame);  // Save the new game state to the database
                            return serializedGame;
                        }
                    } else {
                        // Handle case where no game with the given gameID exists
                        throw new SQLException("No game found with gameID: " + gameID);
                    }
                }
            }
        }
    }

    public void pushGame(int gameID, String newGameState) throws DataAccessException, SQLException {
        String query = "UPDATE games SET game = ? WHERE gameID = ?";
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, newGameState);
                statement.setInt(2, gameID);
                statement.executeUpdate();
            }
        }
    }



    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` INT AUTO_INCREMENT PRIMARY KEY,
              `whiteUsername` VARCHAR(255),
              `blackUsername` VARCHAR(255),
              `gameName` VARCHAR(255),
              `game` LONGTEXT
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        setUpDatabase(createStatements);
    }

    //FOR TESTING ONLY
    public static boolean isTableEmpty() {
        String query = "SELECT COUNT(*) AS total FROM games";
        return getAllData(query);
    }

    private ChessGame getDaGame(String gameInstance){
        if(gameInstance != null){
             Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
                   .create();
            return gson.fromJson(gameInstance, ChessGame.class);
            }
        return null;
    }

    private void printBoard(ChessBoard board, boolean isWhitePerspective) {
        int startRow = isWhitePerspective ? 8 : 1;
        int endRow = isWhitePerspective ? 1 : 8;
        int startCol = isWhitePerspective ? 1 : 8;
        int endCol = isWhitePerspective ? 8 : 1;
        int rowIncrement = isWhitePerspective ? -1 : 1;
        int colIncrement = isWhitePerspective ? 1 : -1;
        if(isWhitePerspective){
            System.out.println("   A\u2003 B   C\u2003 D\u2003 E\u2003 F\u2003 G\u2003 H");
        }else{
            System.out.println("   H\u2003 G   F\u2003 E\u2003 D\u2003 C\u2003 B\u2003 A");
        }
        for (int row = startRow; isWhitePerspective ? row >= endRow : row <= endRow; row += rowIncrement) {
            System.out.print(row + " ");
            for (int col = startCol; isWhitePerspective ? col <= endCol : col >= endCol; col += colIncrement) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                // Alternate colors for board spaces
                boolean isWhiteSpace = (row + col) % 2 == 0;
                String bgColor = isWhiteSpace ? EscapeSequences.SET_BG_COLOR_DARK_GREY:EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

                String pieceRepresentation = getPieceRepresentation(piece);

                System.out.print(bgColor + pieceRepresentation + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }
            System.out.println();
        }
        if(isWhitePerspective){
            System.out.println("   A\u2003 B   C\u2003 D\u2003 E\u2003 F\u2003 G\u2003 H");
        }else{
            System.out.println("   H\u2003 G   F\u2003 E\u2003 D\u2003 C\u2003 B\u2003 A");
        }
    }

    private static String getPieceRepresentation(ChessPiece piece) {
        String pieceRepresentation;
        if (piece != null) {
            pieceRepresentation = switch (piece.getPieceType()) {
                case KING ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
                case QUEEN ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
                case BISHOP ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
                case KNIGHT ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
                case ROOK ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
                case PAWN ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
                default -> EscapeSequences.EMPTY;
            };
        } else {
            pieceRepresentation = EscapeSequences.EMPTY;
        }
        return pieceRepresentation;
    }
}


