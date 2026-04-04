package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {
    public final Map<Integer, Set<Session>> connections = new HashMap<>();

    // this class's only job is to keep track of who's where and when to send a message!
    // key = gameid
    // session = active players (including observers)
}

