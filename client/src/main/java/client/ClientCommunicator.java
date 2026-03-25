package client;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientCommunicator {
    private final String serverUrl;
    private final HttpClient client = HttpClient.newHttpClient();


    public ClientCommunicator(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        request.setHeader("Authorization", authToken);
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    <T> T handleResponse(HttpResponse<String> response, com.google.gson.reflect.TypeToken<T> typeToken) throws ResponseException {
        var status = response.statusCode();

        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new ResponseException(status, body);
            }
            throw new ResponseException(status, "other failure: " + status);
        }

        if (typeToken != null) {
            return new Gson().fromJson(response.body(), typeToken.getType());
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
