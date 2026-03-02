package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(String gameName);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    Collection<GameData> getGameStorage();

    void deleteGame(int gameId);

    void joinGame(int gameID, String username, String color);

    void clear();
}
