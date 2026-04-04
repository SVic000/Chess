package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import httpobjs.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import model.AuthData;
import model.GameData;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        new ValidateAuthorization(authDAO, authToken);

        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new BadRequestResponse("Error: bad request");
        }
        GameData game = gameDAO.createGame(request.gameName());
        return new CreateGameResult(game.gameID(), "");
    }

    public UpdateGameResult updateGame(UpdateGameRequest request, String authToken) throws DataAccessException {
        new ValidateAuthorization(authDAO, authToken);
        GameData current = gameDAO.getGame(request.gameID());
        if(request.color() == null || request.move() == null) {
            // observer can't update the game
            // and game only updates when a move is made
            throw new RuntimeException("Error: Game cannot be updated.");
        }

        // make the updated move on the chessGame, send that up in the update
        // then send back the updated board and a message if it didn't work

        try {
            gameDAO.updateGame(current.gameID(),);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public JoinGameResult joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        new ValidateAuthorization(authDAO, authToken);

        AuthData authData = authDAO.getAuth(authToken);
        GameData gameData = gameDAO.getGame(request.gameID());
        if (!isRequestedColorValid(request)) {
            throw new BadRequestResponse("Error: bad request");
        }
        if (request.playerColor().equals("WHITE")) {
            if (gameData.whiteUsername() == null) {
                gameDAO.joinGame(request.gameID(), authData.username(), request.playerColor());
                return new JoinGameResult("");
            }
        } else {
            if (gameData.blackUsername() == null) {
                gameDAO.joinGame(request.gameID(), authData.username(), request.playerColor());
                return new JoinGameResult("");
            }
        }
        throw new ForbiddenResponse("Error: already taken");
    }

    private boolean isRequestedColorValid(JoinGameRequest request) {
        if (request.playerColor() == null || request.playerColor().isEmpty()) {
            return false;
        }
        return !(!request.playerColor().equals("WHITE") & !request.playerColor().equals("BLACK"));
    }

    public ListGameResult listGames(String auth) throws DataAccessException {
        new ValidateAuthorization(authDAO, auth);
        return new ListGameResult(gameDAO.listGames().stream().toList(), "");
    }

}
