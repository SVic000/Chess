package server;

import HandlerOBJs.RegisterRequest;
import Service.UserService;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.TempStorage.MemoryAuthDAO;
import dataaccess.TempStorage.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {
    private final UserDAO userStorage = new MemoryUserDAO();
    private final AuthDAO authStorage = new MemoryAuthDAO();
    private final UserService userService = new UserService(authStorage,userStorage);
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::registerUser);

        // Register your endpoints and exception handlers here.

    }
    private void registerUser(Context ctx) {
        RegisterRequest userData = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        userData = UserService.register(userData);
        ctx.result(new Gson().toJson(userData));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
