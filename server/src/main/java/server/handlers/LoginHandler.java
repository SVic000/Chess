package server.handlers;

import server.handlers.objects.LoginRequest;
import server.handlers.objects.LoginResult;
import Service.UserService;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class LoginHandler implements Handler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        LoginRequest loginReq = new Gson().fromJson(ctx.body(), LoginRequest.class);
        LoginResult loginRes = userService.login(loginReq);
        ctx.result(new Gson().toJson(loginRes));
    }
}
