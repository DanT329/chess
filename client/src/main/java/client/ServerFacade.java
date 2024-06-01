package client;
import com.google.gson.Gson;
import model.UserData;
import model.AuthData;
import model.GameData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

public class ServerFacade {
    private final int port;
    private final String host;
    private Gson gson = new Gson();

    public ServerFacade(String host, int port) {
        this.port = port;
        this.host = host;
    }

    //Login

    //Register
    public AuthData register(UserData user) throws IOException {
        String endpoint = "user";
        URL url = new URL(String.format("http://%s:%d/%s", host, port,endpoint));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setDoOutput(true);

        try (var outputStream = connection.getOutputStream()) {
            var jsonBody = new Gson().toJson(user);
            outputStream.write(jsonBody.getBytes());
        }
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream respBody = connection.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                return new Gson().fromJson(inputStreamReader, AuthData.class);
            }
        }else{
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                throw new IOException(stringBuilder.toString());
            }
        }

    }
}
