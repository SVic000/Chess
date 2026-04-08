package client;

import chess.*;
import client.error.ResponseException;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import ui.DrawChessBoard;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayREPL implements NotificationHandler {
    private static Scanner scanner = null;
    private static Serializer serializer = null;
    private final String authToken;
    private final int gameID;
    private ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
    private WebSocketFacade server;
    private ChessGame game;
    private String role = "observer";
    private String boardColor = SET_BG_COLOR_DARKEST_GRAY;

    public GameplayREPL(Serializer serializer, Scanner scanner, String role, String authToken, int gameID,
                        ChessGame.TeamColor color, WebSocketFacade server) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.color = color;
        this.server = server;
        this.role = role;
        GameplayREPL.scanner = scanner;
        GameplayREPL.serializer = serializer;
    }

    public static String menu() {
        return """
                0. Change board color
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
                
                0. Change Board Color - Change your boards color options: grey, red, blue, orange, green, purple
                1. Redraw Chessboard - Redraws current game chessboard
                2. Show Legal Moves - Highlights legal moves of a piece on the board
                3. Make move - Makes a move on the board
                4. Resign - Forfeits the win
                5. Leave - Leave the game
                6. Help - Show this menu again
                
                """;
    }

    public void setServer(WebSocketFacade server) {
        this.server = server;
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                game = message.getGame();
                System.out.println();
                new DrawChessBoard(game, color.toString(),boardColor);
                System.out.println();
            }
            case ERROR, NOTIFICATION -> System.out.println(message.getMessage());
        }
    }

    public void run() {
        System.out.print(menu());

        String result = "";
        while (!result.equals("5")) {
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.println(result);

                if (line.matches("[0-4]")) {
                    System.out.print(menu());
                }

            } catch (Throwable e) {
                var msg = serializer.decrypt(e);
                System.out.println(msg.message());
                System.out.print(help());
            }
        }
    }

    private String eval(String line) throws Exception {
        String cmd = line.trim();

        return switch (cmd) {
            case "0" -> updateBoardColor();
            case "1" -> redrawBoard();
            case "2" -> legalMoves();
            case "3" -> makeMove();
            case "4" -> resign();
            case "5" -> leave();
            default -> help();
        };
    }

    private String updateBoardColor() {
        System.out.print("Enter the color of the board you'd like (grey, blue, red, orange, green, purple): ");
        String tokens = scanner.nextLine().trim().toLowerCase();
        switch (tokens) {
            case "blue" -> boardColor = SET_BG_COLOR_GOOD_BLUE;
            case "orange" -> boardColor = SET_BG_COLOR_BURNT_ORANGE;
            case "red" -> boardColor = SET_BG_COLOR_GOOD_RED;
            case "grey" -> boardColor = SET_BG_COLOR_DARKEST_GRAY;
            case "purple" -> boardColor = SET_BG_COLOR_PURPLE;
            case "green" -> boardColor = SET_BG_COLOR_DEEP_GREEN;
            default -> {
                boardColor = SET_BG_COLOR_DARKEST_GRAY;
                return "Error: Invalid color option, set board to default.";
            }
        }
        new DrawChessBoard(game,color.toString(),null,null,boardColor);
        System.out.println();
        return "Successfully updated your board!";
    }

    private String redrawBoard() {
        new DrawChessBoard(game, color.toString(),boardColor);
        return "";
    }

    private String legalMoves() {
        ChessPosition location;
        System.out.print("Enter the position of the piece you'd like to highlight - col(a-h) and row(1-8): ");
        String tokens = scanner.nextLine().toLowerCase();
        try {
            location = convertToPosition(tokens.substring(0, 1), tokens.substring(1));
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        ChessPiece boardPiece = game.getBoard().getPiece(location);
        if (boardPiece == null) {
            return "Error: Unable to highlight moves of an empty position.";
        }

        Collection<ChessPosition> highlightSquares = new ArrayList<>(convertToEndPosition(game.validMoves(location)));

        new DrawChessBoard(game, color.toString(), location, highlightSquares,boardColor);
        return "";
    }

    Collection<ChessPosition> convertToEndPosition(Collection<ChessMove> validMoves) {
        Collection<ChessPosition> result = new ArrayList<>();

        for (ChessMove move : validMoves) {
            result.add(move.getEndPosition());
        }
        return result;
    }


    private String makeMove() {
        ChessPosition start;
        ChessPosition end;
        ChessPiece.PieceType promotion = null;

        if (role.equals("observer")) {
            return "Error: can't make a move as an observer.";
        }
        System.out.print(
                "Enter the starting piece position" +
                        " - column (a-h) and row(1-8): ");
        String tokens = scanner.nextLine().toLowerCase();
        try {
            start = convertToPosition(tokens.substring(0, 1), tokens.substring(1));
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        ChessPiece boardPiece = game.getBoard().getPiece(start);
        if (boardPiece == null) {
            return "Error: no piece on that spot, try again.";
        }

        if (!color.equals(boardPiece.getTeamColor())) {
            return "Error: Unable to move a piece that's not on your team.";
        }
        System.out.print("Enter the ending position -  column (a-h) and row(1-8): ");
        tokens = scanner.nextLine().toLowerCase();
        try {
            end = convertToPosition(tokens.substring(0, 1), tokens.substring(1));
        } catch (Exception e) {
            return e.getMessage();
        }
        String piece = "";
        if (ChessPiece.PieceType.PAWN.equals(boardPiece.getPieceType())) {
            if (boardPiece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                if (end.getRow() == 8) {
                    System.out.println("What would you like to promote to?: ");
                    piece = scanner.nextLine().trim().toLowerCase();
                }
            } else {
                if (end.getRow() == 1) {
                    System.out.println("What would you like to promote to?: ");
                    piece = scanner.nextLine().trim().toLowerCase();
                }
            }
            try {
                promotion = convertToPieceType(piece);
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        ChessMove move = new ChessMove(start, end, promotion);
        ChessBoard old = (ChessBoard) game.getBoard().clone();
        server.sendMakeMove(authToken, gameID, move);
        if (old.equals(game.getBoard())) {
            return "";
        }

        return String.format("Moved %s to %s.", start, end);
    }

    private ChessPosition convertToPosition(String col, String row) {
        int rows;
        try {
            rows = Integer.parseInt(row);
        } catch (NumberFormatException e) {
            throw new ResponseException(403, "Error: Enter a valid row (1-8)");
        }
        int cols;
        if (rows > 8 | rows < 0) {
            throw new ResponseException(400, "Error: enter a valid row (1-8).");
        }
        switch (col) {
            case "a" -> cols = 1;
            case "b" -> cols = 2;
            case "c" -> cols = 3;
            case "d" -> cols = 4;
            case "e" -> cols = 5;
            case "f" -> cols = 6;
            case "g" -> cols = 7;
            case "h" -> cols = 8;
            default -> {
                throw new ResponseException(400, "Error: enter a valid column (a-h).");
            }
        }
        return new ChessPosition(rows, cols);
    }

    private ChessPiece.PieceType convertToPieceType(String piece) {
        switch (piece) {
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
            case "" -> {
                return null;
            }
            default -> {
                throw new ResponseException(401, "Error: Enter a valid promotion piece type (Knight, Rook, Bishop, Queen).");
            }
        }
    }

    private String resign() {
        if (role.equals("observer")) {
            return "Error: Can't resign as an observer";
        }
        System.out.println("Are you sure you want to Resign? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("yes")) {
            server.sendResign(authToken, gameID);
            return "";
        }
        return "Resign canceled.";
    }

    private String leave() {
        server.sendLeave(authToken, gameID);
        return "5";
    }
}
