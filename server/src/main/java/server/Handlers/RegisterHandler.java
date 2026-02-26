package server.Handlers;

import HandlerOBJs.RegisterRequest;
import HandlerOBJs.RegisterResult;
import Service.UserService;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
        RegisterResult userDataRes;
        try {
            userDataRes = userService.register(userDataReq);
            ctx.result(new Gson().toJson(userDataRes));
        } catch (DataAccessException e) {
            ctx.status(e.statusCode);
            ctx.result(new Gson().toJson(e.getMessage()));
        }
    }
}
