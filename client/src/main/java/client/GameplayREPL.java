package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import ui.DrawChessBoard;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class GameplayREPL implements NotificationHandler {
    private final String authToken;
    private final int gameID;
    private ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
    private final WebSocketFacade server;
    private static Scanner scanner = null;
    private static Serializer SERIALIZER = null;
    private ChessGame game;

    public GameplayREPL(Serializer serializer, Scanner scanner, String authToken, int gameID, ChessGame.TeamColor color, WebSocketFacade server) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.color = color;
        this.server = server;
        GameplayREPL.scanner = scanner;
        SERIALIZER = serializer;
    }

    @Override
    public void notify(ServerMessage message) {
        switch(message.getServerMessageType()) {
            case LOAD_GAME -> {
                game = message.getGame();
                new DrawChessBoard(game, color.toString());
            }
            case ERROR, NOTIFICATION -> System.out.println(message.getMessage());
        }
    }

    public void run() {
        System.out.print(menu());

        var result = "";
        while (!result.equals("5")) {
            String line = scanner.nextLine();
            try {
                result = eval(line, scanner);
            }   catch (Throwable e) {
                var msg = SERIALIZER.decrypt(e);
                System.out.println(msg.message());
                System.out.println(menu());
            }

            System.out.println(result);
            System.out.println();
            System.out.print(menu());
        }
    }


    public static String menu() {
        return """
                    1. Redraw Chessboard
                    2. Show Legal Moves
                    3. Make move
                    4. Resign
                    5. Leave
                    6. Help
                    
                    """;
    }

    public static String help() {
        return """
                    Make sure to only input the number of the command you're looking for!
                    
                    1. Redraw Chessboard - Redraws current game chessboard
                    2. Show Legal Moves - Highlights legal moves of a piece on the board
                    3. Make move - Makes a move on the board
                    4. Resign - Forfeits the win
                    5. Leave - Leave the game
                    6. Help - Show this menu again
                    
                    """;
    }

    private String eval(String line, Scanner scanner) throws Exception {
        try {
            String[] token = line.toLowerCase().split(" ");
            String cmd = (token.length > 0) ? token[0] : "3";
            return switch (cmd) {
                case "1" -> redrawBoard();
                case "2" -> legalMoves();
                case "3" -> makeMove();
                case "4" -> resign();
                case "5" -> leave();
                default -> help();
            };
            } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private String redrawBoard() {
        new DrawChessBoard(game,color.toString());
        return "";
    }

    private String legalMoves() {
        // ask which piece they're wanting to get legal moves for
        // get moves for that piece
        // pass into draw board a new constructor with a list of valid positions
        // modify draw board to accommodate that
        return "Not implemented";
    }

    private String makeMove() {
        // ask which piece they want to move ( make sure it's their color )
        // ask where they want to move it ( can be wrong since error will catch it in the server )
        // then do that :)
        return "Not implemented";
    }

    private String resign() {
        System.out.println("Are you sure you want to Resign? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if(response.equals("yes")) {
            server.sendResign(authToken, gameID);
            return "Resigned from game.";
        }
        return "Resign canceled.";
    }

    private String leave() {
        server.sendLeave(authToken,gameID);
        return "5";
    }
}
