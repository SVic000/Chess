package dataaccess.TempStorage;

import chess.ChessGame;
import dataaccess.GameDAO;
import io.javalin.http.BadRequestResponse;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    Map<Integer, GameData> gameStorage = new HashMap<>();
    static int GameID = 1;

    @Override
    public GameData createGame(String gameName) {
        GameData gameData = new GameData(GameID, "","",gameName,new ChessGame());
        gameStorage.put(GameID, gameData);
        GameID += 1;
        return gameData;
    }

    @Override
    public GameData getGame(int gameID) {
        if(gameStorage.containsKey(gameID)) {
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
    public void updateGame(int gameID, GameData gameUpdate) {
        GameData current = gameStorage.get(gameID);
        if(current == null) {
            throw new BadRequestResponse("Error: Game ID not found");
        }
        gameStorage.put(gameID, gameUpdate);
    }

    @Override
    public void deleteGame(int gameId) {
        gameStorage.remove(gameId);
    }

    @Override
    public void joinGame(int gameID, String username, String requestedColor) {
        if(gameStorage.containsKey(gameID)) {
            GameData current = gameStorage.get(gameID);
            GameData updated;
            if(requestedColor.equals("WHITE")) {
                updated = new GameData(gameID, username, current.blackUsername(), current.gameName(), current.game());
            } else {
                updated = new GameData(gameID, current.whiteUsername(), username, current.gameName(), current.game());
            }
            gameStorage.put(gameID,updated );
        }
        throw new BadRequestResponse("Error: game doesn't exist");
    }

    @Override
    public void clear() {
        gameStorage = new HashMap<>();
    }
}
