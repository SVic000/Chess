package httpobjs;

public record RegisterResult(
        String username,
        String authToken,
        String message
) {
}
