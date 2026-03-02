package dataaccess.storage;

import chess.ChessGame;
import dataaccess.GameDAO;
import io.javalin.http.BadRequestResponse;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    static int gameID = 1;
    private final Map<Integer, GameData> gameStorage = new HashMap<>();

    @Override
    public GameData createGame(String gameName) {
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        gameStorage.put(gameID, gameData);
        gameID += 1;
        return gameData;
    }

    @Override
    public Collection<GameData> getGameStorage() {
        return gameStorage.values();
    }


    @Override
    public GameData getGame(int gameID) {
        if (gameStorage.containsKey(gameID)) {
            return gameStorage.get(gameID);
        } else {
            throw new BadRequestResponse("Error: Game not found");
        }
    }

    @Override
    public Collection<GameData> listGames() {
        return gameStorage.values();
    }

    @Override
    public void joinGame(int gameID, String username, String requestedColor) {
        if (gameStorage.containsKey(gameID)) {
            GameData current = gameStorage.get(gameID);
            GameData updated;
            if (requestedColor.equals("WHITE")) {
                updated = new GameData(gameID, username, current.blackUsername(), current.gameName(), current.game());
            } else {
                updated = new GameData(gameID, current.whiteUsername(), username, current.gameName(), current.game());
            }
            gameStorage.put(gameID, updated);
        } else {
            throw new BadRequestResponse("Error: bad request");
        }
    }

    @Override
    public void clear() {
        gameStorage.clear();
    }
}
