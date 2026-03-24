package client;

import httpobjs.*;

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
        return null;
    }

    public CreateGameResult createGame(CreateGameRequest request) {
        return null;
    }

    public JoinGameResult joinGame(JoinGameRequest request) {
        return null;
    }

    public ListGameResult listGames(ClearRequest request) {
        return null;
    }

    public boolean logout(LogoutRequest request) {
        return false;
    }

    public boolean clear(ClearRequest request) {
        return false;
    }
}
