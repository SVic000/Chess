package HandlerOBJs;

public record LoginResult(
        String username,
        String authToken,
        String message
    ){
}
