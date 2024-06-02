package client;
import com.google.gson.Gson;
import model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;


public class ServerFacade {
    private final int port;
    private final String host;

    public ServerFacade(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public AuthData register(UserData user) throws IOException, URISyntaxException {
        String endpoint = "user";
        return getAuthData(user, endpoint);
    }

    public AuthData login(UserData user) throws IOException, URISyntaxException {
        String endpoint = "session";
        return getAuthData(user, endpoint);
    }

    public void logout(AuthData auth) throws IOException, URISyntaxException {
        String endpoint = "session";
        URI uri = new URI(String.format("http://%s:%d/%s", host, port,endpoint));
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("authorization", auth.authToken());
        int responseCode = connection.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            throw handleError(connection);
        }
    }

    public GameData createGame(AuthData auth, GameData game) throws IOException, URISyntaxException {
        String endpoint = "game";
        URI uri = new URI(String.format("http://%s:%d/%s", host, port,endpoint));
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Authorization", auth.authToken());
        connection.setDoOutput(true);
        try (var outputStream = connection.getOutputStream()) {
            var jsonBody = new Gson().toJson(game);
            outputStream.write(jsonBody.getBytes());
        }
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream respBody = connection.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                return new Gson().fromJson(inputStreamReader, GameData.class);
            }
        }else{
            throw handleError(connection);
        }
    }

    public GameWrapper listGames(AuthData auth) throws IOException, URISyntaxException {
        String endpoint = "game";
        URI uri = new URI(String.format("http://%s:%d/%s", host, port,endpoint));
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", auth.authToken());
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream respBody = connection.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                return new Gson().fromJson(inputStreamReader, GameWrapper.class);
            }
        }else{
            throw handleError(connection);
        }
    }

    public void joinGame(GameJoinUser gameInfo) throws IOException, URISyntaxException {
        String endpoint = "game";
        URI uri = new URI(String.format("http://%s:%d/%s", host, port,endpoint));
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", gameInfo.authToken());
        connection.setDoOutput(true);
        try (var outputStream = connection.getOutputStream()) {
            var jsonBody = new Gson().toJson(gameInfo);
            outputStream.write(jsonBody.getBytes());
        }
        int responseCode = connection.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            throw handleError(connection);
        }
    }

    private AuthData getAuthData(UserData user, String endpoint) throws IOException, URISyntaxException {
        URI uri = new URI(String.format("http://%s:%d/%s", host, port,endpoint));
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
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
            throw handleError(connection);
        }
    }

    private IOException handleError(HttpURLConnection connection) throws IOException {
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
