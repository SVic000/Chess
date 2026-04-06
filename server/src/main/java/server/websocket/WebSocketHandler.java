package server.websocket;

import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final UserDAO userService;
    private final GameDAO gameService;

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(UserDAO userService, GameDAO gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        // this will be where you handle all the messages
        // make move, resign, join (observer and player), leave...?
    }
    // will send notif, error, or load game

    public void handleMakeMove(Session session) {
        // will need to check if the game is playable!
        // will get game from service

    }

    public void handleResign() {

    }

    public void handleJoin() {

    }

    public void handleLeave(){

    }
}