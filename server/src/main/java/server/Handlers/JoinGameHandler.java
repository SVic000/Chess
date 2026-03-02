package server.Handlers;

import HandlerOBJs.JoinGameRequest;
import HandlerOBJs.JoinGameResult;
import Service.GameService;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class JoinGameHandler implements Handler {
    GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        JoinGameRequest request = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
        JoinGameResult result = gameService.joinGame(request, ctx.header("Authorization"));
        ctx.result(new Gson().toJson(result));
    }
}
