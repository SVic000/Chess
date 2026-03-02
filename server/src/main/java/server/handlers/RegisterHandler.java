package server.handlers;

import server.handlers.objects.RegisterRequest;
import server.handlers.objects.RegisterResult;
import service.UserService;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class RegisterHandler implements Handler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        RegisterRequest userDataReq = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult userDataRes = userService.register(userDataReq);
        ctx.result(new Gson().toJson(userDataRes));
    }
}
