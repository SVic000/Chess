package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import server.handlers.objects.ClearResult;
import server.handlers.objects.LogoutRequest;
import service.UserService;

public class LogoutHandler implements Handler {
    UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        LogoutRequest logoutReq = new LogoutRequest(ctx.header("Authorization"));
        userService.logout(logoutReq);
        ctx.result(new Gson().toJson(new ClearResult("")));
    }
}
