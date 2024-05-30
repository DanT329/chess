package passoff.chess;
import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChessGameJsonTest {
    @Test
    public void jsonGame(){
        ChessGame game = new ChessGame();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer()) // Register custom deserializer
                .create();

        // Convert the ChessGame object to a pretty-printed JSON string
        String prettyJson = gson.toJson(game);
        System.out.println(prettyJson);

        // Deserialize the JSON string back into a ChessGame object
        ChessGame newGame = gson.fromJson(prettyJson, ChessGame.class);

        // Verify that the deserialization was successful
        assertEquals(game, newGame);
    }

}
