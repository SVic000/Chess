package server;

import HandlerOBJs.LoginRequest;
import HandlerOBJs.RegisterRequest;
import HandlerOBJs.RegisterResult;
import Service.UserService;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.TempStorage.MemoryAuthDAO;
import dataaccess.TempStorage.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import server.Handlers.RegisterHandler;

public class Server {
    private final UserDAO userStorage = new MemoryUserDAO();
    private final AuthDAO authStorage = new MemoryAuthDAO();
    private final UserService userService = new UserService(userStorage,authStorage);
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new RegisterHandler(userService));
                //.post("/session", new LoginHandler(userService));
        // Register your endpoints and exception handlers here.


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
