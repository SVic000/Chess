package server;

import Service.ClearService;
import Service.UserService;
import dataaccess.AuthDAO;
import dataaccess.TempStorage.MemoryAuthDAO;
import dataaccess.TempStorage.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.*;
import server.Handlers.ClearHandler;
import server.Handlers.RegisterHandler;

public class Server {
    private final Javalin javalin;

    public Server() {
        UserDAO userStorage = new MemoryUserDAO();
        AuthDAO authStorage = new MemoryAuthDAO();
        UserService userService = new UserService(userStorage, authStorage);
        ClearService clearService = new ClearService(userStorage, authStorage);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new RegisterHandler(userService))
                .delete("/db", new ClearHandler(clearService));
                //.post("/session", new LoginHandler(userService));


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
