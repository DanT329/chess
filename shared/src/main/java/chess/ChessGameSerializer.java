package chess;

import com.google.gson.*;
import java.lang.reflect.Type;

public class ChessGameSerializer implements JsonSerializer<ChessGame> {

    @Override
    public JsonElement serialize(ChessGame chessGame, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        // Serialize teamTurn
        jsonObject.addProperty("teamTurn", chessGame.getTeamTurn().toString());

        // Serialize the board
        JsonElement boardElement = jsonSerializationContext.serialize(chessGame.getBoard());
        jsonObject.add("board", boardElement);

        return jsonObject;
    }
}
