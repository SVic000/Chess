package server.handlers;

import service.GameService;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class ListGamesHandler implements Handler {
    GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        ctx.result(new Gson().toJson(gameService.listGames(ctx.header("Authorization"))));
    }
}
