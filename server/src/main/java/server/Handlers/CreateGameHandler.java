package server.Handlers;

import HandlerOBJs.CreateGameRequest;
import HandlerOBJs.CreateGameResult;
import Service.GameService;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class CreateGameHandler implements Handler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        CreateGameRequest request = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
        CreateGameResult response = gameService.createGame(request);
        ctx.result(new Gson().toJson(response));
    }
}
