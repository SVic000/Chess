package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
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
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch(command.getCommandType()) {
            case MAKE_MOVE -> {
                MakeMoveCommand move = new Gson().fromJson(ctx.message(),MakeMoveCommand.class);
                handleMakeMove(ctx.session, move);
            }
            case LEAVE -> handleLeave(ctx.session, command);
            case RESIGN -> handleResign(ctx.session,command);
            case CONNECT -> handleJoin(ctx.session,command);
        }
    }

    public void handleMakeMove(Session session, MakeMoveCommand command) throws IOException {
        String message;
        ServerMessage notification;
        AuthData user;
        GameData game;
        GameData updated;
        ChessGame current;
        ChessMove move = command.getChessMove();
        try {
            user = authDAO.getAuth(command.getAuthToken());
            game = gameDAO.getGame(command.getGameID());

            if(!user.username().equals(game.whiteUsername()) && !user.username().equals(game.blackUsername())) {
                message = "Error: Observers can't make a move";
                notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null, message);
                connections.sendToSession(session,notification);
                return;
            }
        } catch (Exception e) {
            message = e.getMessage();
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null, message);
            connections.sendToSession(session,notification);
            return;
        }
        if(!game.game().isGameActive()) {
            System.out.println("Here6");
            message = "Error: game is over. Can't make moves.";
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null, message);
            connections.sendToSession(session,notification);
            return;
        }
        ChessGame.TeamColor color = user.username().equals(game.whiteUsername()) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        ChessGame.TeamColor enemy = color.equals(ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        try {
            if(!game.game().getCurrentTurn().equals(color)) {
                System.out.println("Here5");
                message = "Error: not your turn, you can't make a move.";
                notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null, message);
                connections.sendToSession(session,notification);
                return;
            }
            game.game().makeMove(command.getChessMove());
            if(game.game().isInCheckmate(enemy)
                    || game.game().isInStalemate(enemy)) {
                game.game().endGame();
            }
            updated = gameDAO.updateGame(command.getGameID(),game.game());
            current = updated.game();
        }  catch (InvalidMoveException e) {
            message = "Error: Invalid move.";
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null, message);
            connections.sendToSession(session,notification);
            return;

        }catch (Exception e ) {
            message = e.getMessage();
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null, message);
            connections.sendToSession(session,notification);
            return;
        }

        notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, current);
        connections.broadcast(null, game.gameID(), notification);

        message = String.format("%s moved %s to %s", user.username(), move.getStartPosition(), move.getEndPosition());
        notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null);
        connections.broadcast(session, command.getGameID(), notification);

        if (current.isInCheckmate(enemy)) {
            message = String.format("%s is in checkmate. Game over.", enemy);
            notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,message, null);
            connections.broadcast(null,command.getGameID(),notification);
        } else if(current.isInStalemate(enemy)) {
            message = String.format("%s is in stalemate. Game over.", enemy);
            notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,message, null);
            connections.broadcast(null,command.getGameID(),notification);
        } else if (current.isInCheck(enemy)) {
            message = String.format("%s is in check.", enemy);
            notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,message, null);
            connections.broadcast(null,command.getGameID(),notification);
        }
    }

    public void handleResign(Session session, UserGameCommand command) throws IOException {
        String message;
        ServerMessage notification;
        try {
            AuthData user = authDAO.getAuth(command.getAuthToken());
            GameData game = gameDAO.getGame(command.getGameID());

            if(user.username().equals(game.whiteUsername())) {
                message = String.format("%s resigns, Black Wins.", user.username());
            } else if(user.username().equals(game.blackUsername())) {
                message = String.format("%s resigns, White Wins.",user.username());
            } else {
                message = "Error: can't resign as an observer.";
                notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, message);
                try {
                    connections.sendToSession(session, notification);
                    return;
                } catch (IOException e) {
                    message = "Error: Unable to resign.";
                    notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null,  message);
                    connections.sendToSession(session,notification);
                    return;
                }

            }
            ChessGame board = game.game();
            if(!board.isGameActive()) {
                message = "Error: can't resign from a game that's over.";
                notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null, message);
                connections.sendToSession(session,notification);
                return;
            }
            board.endGame();
            gameDAO.updateGame(game.gameID(), board);

            notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,message, null);
            connections.broadcast(null,command.getGameID(),notification);
        } catch (DataAccessException | IOException e) {
            System.out.println("here2");
            message = e.getMessage();
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null, message);
            connections.sendToSession(session,notification);
        }
    }

    public void handleJoin(Session session, UserGameCommand command) throws IOException {
        String message;
        ServerMessage notification;
        try {
            AuthData user = authDAO.getAuth(command.getAuthToken());
            GameData game = gameDAO.getGame(command.getGameID());
            connections.add(game.gameID(), session);

            notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
            connections.sendToSession(session, notification);

            message = getMessageConnect(user.username(),game);
            notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null);
            connections.broadcast(session, game.gameID(), notification);
        } catch (Exception e) {
            System.out.println("here3");
            message = "Error: Unable to join the game. Try again.";
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null, message);
            connections.sendToSession(session, notification);
        }
    }

    public String getMessageConnect(String username, GameData game) {
        if(username.equals(game.whiteUsername())) {
            return String.format("%s joined as White Player.", username);
        } else if(username.equals(game.blackUsername())) {
            return String.format("%s joined as Black Player.", username);
        } else {
            return String.format("%s joined as an observer.", username);
        }
    }

    public void handleLeave(Session session, UserGameCommand command) throws IOException {
        String message;
        ServerMessage notification;
        try {
            AuthData user = authDAO.getAuth(command.getAuthToken());
            GameData game = gameDAO.getGame(command.getGameID());

            connections.remove(game.gameID(), session);

            if(user.username().equals(game.whiteUsername())) {
                gameDAO.joinGame(command.getGameID(), null,"WHITE");
            } else if(user.username().equals(game.blackUsername())) {
                gameDAO.joinGame(command.getGameID(),null, "BLACK");
            }

            message = String.format("%s left the game.", user.username());
            notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message, null);
            connections.broadcast(session, game.gameID(), notification);
        } catch (Exception e) {
            System.out.println("here4");
            message = "Error: Unable to leave game.";
            notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR,null, message);
            connections.sendToSession(session, notification);
        }
    }

}