package client;

import httpobjs.*;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.*;
import server.Server;

import javax.swing.tree.ExpandVetoException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        facade.clear();
    }

    @BeforeEach
    public void clear() {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        RegisterRequest test = new RegisterRequest("username","password","email");
        var authData = facade.register(test);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerDup() throws Exception {
        RegisterRequest test = new RegisterRequest("username","password","email");
        facade.register(test);

        assertThrows(Exception.class, () -> facade.register(test));
    }

    @Test
    void loginSuccess() {
        RegisterRequest test = new RegisterRequest("username","password","email");
        facade.register(test);

        LoginRequest req = new LoginRequest("username", "password");

        LoginResult res = facade.login(req);
        assertTrue(res.authToken().length() > 10);
        assertTrue(res.message().isEmpty());
    }

    @Test
    void loginNotRegistered() {
        assertThrows(Exception.class, () -> facade.login(new LoginRequest("name","pass")));
    }

    @Test
    void logoutSuccess() {
        var authData = facade.register(new RegisterRequest("name","pass","email"));
        assertDoesNotThrow(()-> facade.logout(authData.authToken()));
    }

    @Test
    void logoutUnauthorized() {
        assertThrows(Exception.class, ()-> facade.logout(null));
    }


    @Test
    void createGameSuccess() {
        var authData = facade.register(new RegisterRequest("name","pass","email"));
        CreateGameRequest test = new CreateGameRequest("n");
        assertDoesNotThrow(() -> facade.createGame(test, authData.authToken()));
    }

    @Test
    void createGameNoAuth() {
        CreateGameRequest test = new CreateGameRequest("n");
        assertThrows(Exception.class, ()-> facade.createGame(test,""));
    }

    @Test
    void joinGameSuccess() {
        var authData = facade.register(new RegisterRequest("name","pass","email"));
        CreateGameRequest game = new CreateGameRequest("n");
        CreateGameResult res = facade.createGame(game,authData.authToken());

        JoinGameRequest test = new JoinGameRequest("WHITE",res.gameID());
        assertDoesNotThrow(()->facade.joinGame(test,authData.authToken()));
        assertTrue(res.message().isEmpty());
    }

    @Test
    void listGamesSuccess() {
        var authData = facade.register(new RegisterRequest("name","pass","email"));
        assertDoesNotThrow(() -> facade.listGames(authData.authToken()));

        var gameData = facade.listGames(authData.authToken());
        assertEquals(0, gameData.games().size());
    }

    @Test
    void listGamesUnauthorized() {
        assertThrows(Exception.class, () -> facade.listGames(""));
    }

    @Test
    void clearSuccess() {
        assertDoesNotThrow(()->facade.clear());
    }
}
