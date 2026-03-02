package server.handlers.objects;

public record LoginResult(
        String username,
        String authToken,
        String message
) {
}
