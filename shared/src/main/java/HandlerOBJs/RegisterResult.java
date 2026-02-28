package HandlerOBJs;

public record RegisterResult(
        String username,
        String authToken,
        String message
    ){
}
