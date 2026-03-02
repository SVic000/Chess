package server.handlers.objects;

public record LoginRequest(
        String username,
        String password
) {
}
