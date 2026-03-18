package httpobjs;

public record LoginResult(
        String username,
        String authToken,
        String message
) {
}
