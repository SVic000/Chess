package client;

import com.google.gson.Gson;
import io.javalin.http.HttpResponseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientCommunicator {
    private final String SERVER_URL;
    private final HttpClient CLIENT = HttpClient.newHttpClient();


    public ClientCommunicator(String serverUrl) {
        SERVER_URL = serverUrl;
    }

    HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + path))
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

    HttpResponse<String> sendRequest(HttpRequest request) throws HttpResponseException {
        try {
            return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new HttpResponseException(500, ex.getMessage());
        }
    }

    <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws HttpResponseException {
        var status = response.statusCode();

        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new HttpResponseException(status, body);
            }

            throw new HttpResponseException(status, "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
