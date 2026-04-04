package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.dbstorage.MySqlAuthDataAccess;
import dataaccess.dbstorage.MySqlGameDataAccess;
import dataaccess.dbstorage.MySqlUserDataAccess;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import server.handlers.*;
import server.websocket.WebSocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Map;
import java.util.Objects;

public class Server {
    private final Javalin javalin;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        UserDAO userStorage;
        GameDAO gameStorage;
        AuthDAO authStorage;
        try {
            userStorage = new MySqlUserDataAccess();
            gameStorage = new MySqlGameDataAccess();
            authStorage = new MySqlAuthDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        UserService userService = new UserService(userStorage, authStorage);
        ClearService clearService = new ClearService(userStorage, authStorage, gameStorage);
        GameService gameService = new GameService(authStorage, gameStorage);

        webSocketHandler = new WebSocketHandler(userService,gameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new RegisterHandler(userService))
                .delete("/db", new ClearHandler(clearService))
                .post("/session", new LoginHandler(userService))
                .delete("/session", new LogoutHandler(userService))
                .post("/game", new CreateGameHandler(gameService))
                .put("/game", new JoinGameHandler(gameService))
                .get("/game", new ListGamesHandler(gameService))
                .exception(HttpResponseException.class, this::exceptionHandler)
                .exception(Exception.class, this::exceptionHandler)
                .ws("/ws", ws -> {
                    ws.onConnect(webSocketHandler);
                    ws.onMessage(webSocketHandler);
                    ws.onClose(webSocketHandler);
                });
    }

    private void exceptionHandler(Exception e, Context ctx) {
        int status;
        if (Objects.requireNonNull(e) instanceof HttpResponseException b) {
            status = b.getStatus();
        } else {
            status = 500;
        }
        ctx.status(status);
        ctx.result(new Gson().toJson(Map.of("message", e.getMessage(), "status", status)));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
