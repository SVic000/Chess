package server;

import HandlerOBJs.RegisterRequest;
import Service.UserService;
import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import model.UserData;
import org.eclipse.jetty.security.authentication.AuthorizationService;

public class Server {
    private final UserService userService = new UserService();
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
