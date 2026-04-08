package server.handlers;

import com.google.gson.Gson;
import httpobjs.LoginRequest;
import httpobjs.LoginResult;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.UserService;

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
