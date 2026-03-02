package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.storage.MemoryAuthDAO;
import dataaccess.storage.MemoryGameDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    static final GameDAO GAME_STORAGE = new MemoryGameDAO();
    static final AuthDAO AUTH_STORAGE = new MemoryAuthDAO();

    static final GameService SERVICE = new GameService(AUTH_STORAGE,GAME_STORAGE);

    @BeforeEach
    void clear() {
        GAME_STORAGE.clear();
        AUTH_STORAGE.clear();
    }

    @Test
    void createGameSuccess() {}

    @Test
    void createNoGivenGameName() {}

    @Test
    void joinGameSuccess() {}

    @Test
    void joinGameColorTaken(){}

    @Test
    void listGameSuccess() {}
}
