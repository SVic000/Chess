package server;

import Service.ClearService;
import Service.UserService;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.TempStorage.MemoryAuthDAO;
import dataaccess.TempStorage.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import server.Handlers.ClearHandler;
import server.Handlers.LoginHandler;
import server.Handlers.RegisterHandler;

import java.util.Map;
import java.util.Objects;

public class Server {
    private final Javalin javalin;

    public Server() {
        UserDAO userStorage = new MemoryUserDAO();
        AuthDAO authStorage = new MemoryAuthDAO();
        UserService userService = new UserService(userStorage, authStorage);
        ClearService clearService = new ClearService(userStorage, authStorage);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new RegisterHandler(userService))
                .delete("/db", new ClearHandler(clearService))
                .post("/session", new LoginHandler(userService))
                .exception(HttpResponseException.class, this::exceptionHandler)
                .exception(Exception.class, this::exceptionHandler);
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
