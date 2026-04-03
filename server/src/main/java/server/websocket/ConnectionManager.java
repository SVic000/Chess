package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {
    public final Map<Integer, Set<Session>> connections = new HashMap<>();


    // handle when someone joins a game
    // handle when someone makes a  move
    // handle when someone resigns
    // handle when someone leaves
}

