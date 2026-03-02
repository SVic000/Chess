package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import server.handlers.objects.ClearResult;
import service.ClearService;

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
