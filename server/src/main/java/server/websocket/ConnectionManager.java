package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {
    public final Map<Integer, Set<Session>> connections = new HashMap<>();

    public void add(int gameID, Session session) {
        Set<Session> current = connections.get(gameID);
        if(current == null) {
            current = new HashSet<>();
        }
        current.add(session);
        connections.put(gameID,current);
    }

    public void remove(int gameID, Session session) {
        Set<Session> current = connections.get(gameID);
        if(current == null) {
            return;
        }
        current.remove(session);
        connections.put(gameID, current);
    }

    public void broadcast(Session excludeSession, int gameID, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        Set<Session> current = connections.get(gameID);
        if(current == null) {
            return;
        }
        for (Session c : current) {
            if(c.isOpen()) {
                if(!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}

