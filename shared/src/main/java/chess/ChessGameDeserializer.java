package chess;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.Map;

public class ChessGameDeserializer implements JsonDeserializer<ChessGame> {
    @Override
    public ChessGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ChessGame game = new ChessGame();

        // Deserialize the 'teamTurn' field
        JsonElement teamTurnElement = jsonObject.get("teamTurn");
        if (teamTurnElement != null) {
            ChessGame.TeamColor teamTurn = context.deserialize(teamTurnElement, ChessGame.TeamColor.class);
            game.setTeamTurn(teamTurn);
        }

        // Deserialize the 'gameUp' field
        JsonElement gameUpElement = jsonObject.get("gameUp");
        if (gameUpElement != null) {
            boolean gameUp = context.deserialize(gameUpElement, Boolean.class);
            game.setGameUp(gameUp);  // Assuming you have a setter for this field
        }

        // Clear the board before deserializing
        game.getBoard().clearBoard();

        // Deserialize the nested 'board' object
        JsonObject boardObject = jsonObject.getAsJsonObject("board").getAsJsonObject("board");
        for (Map.Entry<String, JsonElement> entry : boardObject.entrySet()) {
            String[] coordinates = entry.getKey().split(",");
            int row = Integer.parseInt(coordinates[0]);
            int col = Integer.parseInt(coordinates[1]);
            ChessPosition position = new ChessPosition(row, col);
            ChessPiece piece = context.deserialize(entry.getValue(), ChessPiece.class);
            game.getBoard().addPiece(position, piece);
        }

        return game;
    }
}


