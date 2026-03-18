package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import httpobjs.CreateGameRequest;
import httpobjs.CreateGameResult;
import service.GameService;

public class CreateGameHandler implements Handler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        CreateGameRequest request = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
        CreateGameResult response = gameService.createGame(request, ctx.header("Authorization"));
        ctx.result(new Gson().toJson(response));
    }
}
