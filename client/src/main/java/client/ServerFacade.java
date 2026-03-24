package client;

import httpobjs.*;

public class ServerFacade {
    private final ClientCommunicator communicator;

    public ServerFacade(String url) {
        communicator = new ClientCommunicator(url);
    }

    public RegisterResult register(RegisterRequest request) {
        var buildReq = communicator.buildRequest("POST", "/user", request, "");
        var response = communicator.sendRequest(buildReq);
        return communicator.handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) {
        var buildReq = communicator.buildRequest("POST", "/session", request, "");
        var response = communicator.sendRequest(buildReq);
        return communicator.handleResponse(response, LoginResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        var buildReq = communicator.buildRequest("POST", "/game", request, authToken);
        var response = communicator.sendRequest(buildReq);
        return communicator.handleResponse(response, CreateGameResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest request, String authToken) {
        var buildReq = communicator.buildRequest("PUT", "/game", request, authToken);
        var response = communicator.sendRequest(buildReq);
        return communicator.handleResponse(response, JoinGameResult.class);
    }

    public ListGameResult listGames(String authToken) {
        var buildReq = communicator.buildRequest("GET", "/game", null, authToken);
        var response = communicator.sendRequest(buildReq);
        return communicator.handleResponse(response, ListGameResult.class);
    }

    public void logout(String authToken) {
        var buildReq = communicator.buildRequest("DELETE", "/session", null, authToken);
        communicator.sendRequest(buildReq);
    }

    public void clear() {
        var buildReq = communicator.buildRequest("DELETE", "/db", null, "");
        communicator.sendRequest(buildReq);
    }
}
