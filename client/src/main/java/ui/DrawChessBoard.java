package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static ui.EscapeSequences.*;

public class DrawChessBoard {
    private static ChessGame game = new ChessGame();
    private static String color;
    private static List<String> letters;
    private static List<String> numbers;
    private static final String EMPTY = "   ";
    private static final int HEADER_WIDTH = 10;

    // for observer
    public DrawChessBoard(ChessGame game) {
        DrawChessBoard.game = game;
        DrawChessBoard.color = "WHITE";
        draw();
    }

    // for join game
    public DrawChessBoard(ChessGame game, String color) {
        DrawChessBoard.color = color;
        DrawChessBoard.game = game;
        draw();
    }

    private static void draw() {
        updateLetterAndNumberOrder(color);
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        printHeaderTextRow(out);
        newRow(out);

        if(color.equals("WHITE")) {
            printWhiteBoard(out);
        } else {
            printBlackBoard(out);
        }

        printHeaderTextRow(out);

        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
    }


    private static boolean wasPreviousColorDark(PrintStream out, boolean color) {
        if(color) {
            out.print(SET_BG_COLOR_BURNT_ORANGE);
        } else {
            out.print(SET_BG_COLOR_OFF_WHITE);
        }
        return color ? FALSE : TRUE;
    }

    private static void printHeaderTextRow(PrintStream out) {
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_BG_COLOR_LIGHT_GREY);

        for(int i = 0; i < HEADER_WIDTH; i++) {
            out.print(letters.get(i));
        }
    }

    static void printBlackBoard(PrintStream out) {
        boolean isColorDark = FALSE;
        for(int i = 1; i < 9; i++) {
            printHeaderTextSingle(out, i-1);
            for(int j = 8; j > 0; j--) {
                isColorDark = wasPreviousColorDark(out, isColorDark);
                printBoardPiece(out,i,j);
            }
            printHeaderTextSingle(out, i-1);
            isColorDark = wasPreviousColorDark(out, isColorDark);
            newRow(out);
        }
    }

    static void printWhiteBoard(PrintStream out) {
        boolean isColorDark = FALSE;
        int index = 0;
        for(int i = 8; i > 0; i--) {
            printHeaderTextSingle(out, index);
            for(int j = 1; j < 9; j++) {
                isColorDark = wasPreviousColorDark(out, isColorDark);
                printBoardPiece(out,i,j);
            }
            printHeaderTextSingle(out, index);
            isColorDark = wasPreviousColorDark(out, isColorDark);
            index++;
            newRow(out);
        }
    }

    private static void newRow(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.println();
    }

    private static void printHeaderTextSingle(PrintStream out, int index) {
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_BG_COLOR_LIGHT_GREY);

        out.print(numbers.get(index));
    }

    private static void updateLetterAndNumberOrder(String color) {
        if(color.equals("BLACK")) {
            letters = List.of(EMPTY, " h "," g ", " f ", " e ", " d ", " c ", " b ", " a ", EMPTY);
            numbers = List.of(" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ");
        } else {
            letters = List.of(EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY);
            numbers = List.of(" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 ");
        }
    }

    private static void printBoardPiece(PrintStream out, int row, int col) {
        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row,col));
        if(piece == null) {
            out.print("   ");
            return;
        }
        if(piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            out.print(SET_TEXT_COLOR_WHITE);
        } else {
            out.print(SET_TEXT_COLOR_BLACK);
        }
        out.print(getChessPieceChar(piece));
    }

    private static String getChessPieceChar(ChessPiece piece) {
        String value = " ";
        switch (piece.getPieceType()) {
            case PAWN -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
            case QUEEN -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_QUEEN: BLACK_QUEEN;
            case KING -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_KING : BLACK_KING;
            case BISHOP -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
            case ROOK -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK;
            case KNIGHT -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
        }
        return value;
    }
}
