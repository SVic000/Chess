package server.handlers.objects;

public record RegisterResult(
        String username,
        String authToken,
        String message
) {
}
