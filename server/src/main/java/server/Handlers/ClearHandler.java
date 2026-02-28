package server.Handlers;

import HandlerOBJs.ClearResult;
import Service.ClearService;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class ClearHandler implements Handler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        ClearResult response;
        clearService.clear();
        response = new ClearResult("");
        ctx.result(new Gson().toJson(response));
    }
}
