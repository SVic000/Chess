package client;

import httpobjs.*;
import io.javalin.http.HttpResponseException;

import java.net.http.HttpRequest;

public class ServerFacade {
    private final String SERVERURL;
    private final ClientCommunicator communicator;

    public ServerFacade(String url) {
        SERVERURL = url;
        communicator = new ClientCommunicator(url);
    }

    public RegisterResult register(RegisterRequest request) {
        var buildReq = communicator.buildRequest("POST", "/user", request);
        var response = communicator.sendRequest(buildReq);
        return communicator.handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) {
        var buildReq = communicator.buildRequest("POST", "/session", request);
        var response = communicator.sendRequest(buildReq);
        return communicator.handleResponse(response, LoginResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) {
        var buildReq = communicator.buildRequest("POST","/game",request);
        var response = communicator.sendRequest(buildReq);
        return communicator.handleResponse(response, CreateGameResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest request) {
        var buildReq = communicator.buildRequest("PUT", "/game", request);
        var response = communicator.sendRequest(buildReq);
        return communicator.handleResponse(response, JoinGameResult.class);
    }

    public ListGameResult listGames(ClearRequest request) {
        var buildReq = communicator.buildRequest("GET", "/game", request);
        var response = communicator.sendRequest(buildReq);
        return communicator.handleResponse(response, ListGameResult.class);
    }

    public void logout(LogoutRequest request) {
        var buildReq = communicator.buildRequest("DELETE", "/session", request);
        var response = communicator.sendRequest(buildReq);
    }

    public void clear(ClearRequest request) {
        var buildReq = communicator.buildRequest("DELETE", "/db", request);
        var response = communicator.sendRequest(buildReq);
    }
}
