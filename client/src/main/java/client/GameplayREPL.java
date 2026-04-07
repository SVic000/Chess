package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.error.ResponseException;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import kotlin.sequences.DropWhileSequence;
import ui.DrawChessBoard;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
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
    private String role = "observer";

    public GameplayREPL(Serializer serializer, Scanner scanner, String role, String authToken, int gameID, ChessGame.TeamColor color, WebSocketFacade server) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.color = color;
        this.server = server;
        this.role = role;
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
        ChessPosition start;
        ChessPosition end;
        ChessPiece.PieceType promotion = null;

        if(role.equals("observer")) {
            return "Error: can't make a move as an observer.";
        }
        System.out.print("Enter the starting column (a-h) and row(1-8): ");
        String[] tokens = scanner.nextLine().toLowerCase().split("");
        try {
            start = convertToPosition(tokens[0], tokens[1]);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        ChessPiece boardPiece = game.getBoard().getPiece(start);
        if(boardPiece == null) {
            return "Error: no piece on that spot, try again.";
        }

        if(!color.equals(boardPiece.getTeamColor())) {
            return "Error: Unable to move a piece that's not on your team.";
        }
        System.out.print("Enter the end column (a-h) and row(1-8): ");
        tokens = scanner.nextLine().toLowerCase().split("");
        try {
            end = convertToPosition(tokens[0], tokens[1]);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        String piece = "";
        if(ChessPiece.PieceType.PAWN.equals(boardPiece.getPieceType())) {
            if(boardPiece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                if(end.getRow() == 8) {
                    System.out.println("What would you like to promote to?: ");
                    piece = scanner.nextLine().trim().toLowerCase();
                }
            } else {
                if(end.getRow() == 1) {
                    System.out.println("What would you like to promote to?: ");
                    piece = scanner.nextLine().trim().toLowerCase();
                }
            }
            try {
                promotion = convertToPieceType(piece);
            } catch (RuntimeException e) {
                return e.getMessage();
            }
        }
        ChessMove move = new ChessMove(start,end,promotion);

        server.sendMakeMove(authToken,gameID,move);
        return String.format("Moved %s to %s.", start,end);
    }

    private ChessPosition convertToPosition(String col, String row) {
        int rows = Integer.parseInt(row);
        int cols;
        if(rows > 8 | rows < 0) {
            throw new RuntimeException("Error: enter a valid row (1-8).");
        }
        switch(col) {
            case "a" -> cols = 1;
            case "b" -> cols = 2;
            case "c" -> cols = 3;
            case "d" -> cols = 4;
            case "e" -> cols = 5;
            case "f" -> cols = 6;
            case "g" -> cols = 7;
            case "h" -> cols = 8;
            default -> {
                throw new RuntimeException("Error: enter a valid column (a-h).");
            }
        }
        return new ChessPosition(rows,cols);
    }

    private ChessPiece.PieceType convertToPieceType(String piece) {
        switch(piece) {
            case "queen" -> {
                return ChessPiece.PieceType.QUEEN;
            }
            case "rook" -> {
                return ChessPiece.PieceType.ROOK;
            }
            case "bishop" -> {
                return ChessPiece.PieceType.BISHOP;
            }
            case "knight" -> {
                return ChessPiece.PieceType.KNIGHT;
            }
            default -> {
                throw new ResponseException(401, "Error: Enter a valid promotion piece type (Knight, Rook, Bishop, Queen)");
            }
        }
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
