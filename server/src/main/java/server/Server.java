package server;

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
import io.javalin.http.HttpResponseException;

public class Server {
    private final UserDAO userStorage = new MemoryUserDAO();
    private final AuthDAO authStorage = new MemoryAuthDAO();
    private final UserService userService = new UserService(userStorage,authStorage);
    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::registerUser);

        // Register your endpoints and exception handlers here.


    }
    private void registerUser(Context ctx) {
        RegisterRequest userDataReq = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult userDataRes;
        try {
            userDataRes = userService.register(userDataReq);
            ctx.result(new Gson().toJson(userDataRes));
        } catch (DataAccessException e) {
            ctx.status(e.statusCode);
            ctx.result(new Gson().toJson(e.getMessage()));
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
