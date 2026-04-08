package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;

public class DrawChessBoard {
    private ChessGame game = new ChessGame();
    private final String color;
    private List<String> letters;
    private List<String> numbers;
    private Collection<ChessPosition> highlights;
    private ChessPosition anchor;

    // for highlight pieces
    public DrawChessBoard(ChessGame game, String color, ChessPosition anchor, Collection<ChessPosition> highlights) {
        this.game = game;
        this.highlights = highlights;
        this.anchor = anchor;
        this.color = color;
        draw();
    }

    // for join game
    public DrawChessBoard(ChessGame game, String color) {
        this.color = color;
        this.game = game;
        draw();
    }

    private void draw() {
        updateLetterAndNumberOrder(color);
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        printHeaderTextRow(out);
        newRow(out);

        if (color.equals("WHITE")) {
            printWhiteBoard(out);
        } else {
            printBlackBoard(out);
        }

        printHeaderTextRow(out);

        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
    }


    private boolean wasPreviousColorDark(PrintStream out, boolean color, boolean highlight, boolean anchor) {
        if (color) {
            if(highlight) {
                out.print(SET_BG_COLOR_DARKER_GREEN);
            } else {
                out.print(SET_BG_COLOR_BURNT_ORANGE);
            }
        } else {
            if(highlight) {
                out.print(SET_BG_COLOR_LIGHT_GREEN);
            } else {
                out.print(SET_BG_COLOR_OFF_WHITE);
            }
        }
        if(anchor) {
            out.print(SET_BG_COLOR_GOOD_YELLOW);
        }
        return !color;
    }

    private void printHeaderTextRow(PrintStream out) {
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_BG_COLOR_LIGHT_GREY);

        int headerWidth = 10;
        for (int i = 0; i < headerWidth; i++) {
            out.print(letters.get(i));
        }
    }

    void printBlackBoard(PrintStream out) {
        boolean isColorDark = false;

        for (int i = 1; i < 9; i++) {
            printHeaderTextSingle(out, i - 1);
            for (int j = 8; j > 0; j--) {
                isColorDark = checkIsDark(i,j, isColorDark, out);
                printBoardPiece(out, i, j);
            }
            printHeaderTextSingle(out, i - 1);
            isColorDark = wasPreviousColorDark(out, isColorDark,false,false);
            newRow(out);
        }
    }

    void printWhiteBoard(PrintStream out) {
        boolean isColorDark = false;
        int index = 0;
        for (int i = 8; i > 0; i--) {
            printHeaderTextSingle(out, index);
            for (int j = 1; j < 9; j++) {
                isColorDark = checkIsDark(i,j,isColorDark,out);
                printBoardPiece(out, i, j);
            }
            printHeaderTextSingle(out, index);
            isColorDark = wasPreviousColorDark(out, isColorDark,false,false);
            index++;
            newRow(out);
        }
    }

    boolean isPositionAnchor(int row, int col){
        ChessPosition check = new ChessPosition(row,col);
        return check.equals(anchor);
    }

    boolean isPositionInHighlight(int row, int col) {
        ChessPosition check = new ChessPosition(row,col);
        if(highlights == null) {
            return false;
        }
        return highlights.contains(check);
    }

    boolean checkIsDark(int i, int j, boolean isColorDark, PrintStream out) {
        if(isPositionAnchor(i,j)) {
            isColorDark = wasPreviousColorDark(out,isColorDark,false,true);
        } else if (isPositionInHighlight(i,j)) {
            isColorDark = wasPreviousColorDark(out,isColorDark,true,false);
        } else {
            isColorDark = wasPreviousColorDark(out, isColorDark, false,false);
        }
        return isColorDark;
    }

    private void newRow(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private void printHeaderTextSingle(PrintStream out, int index) {
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_BG_COLOR_LIGHT_GREY);

        out.print(numbers.get(index));
    }

    private void updateLetterAndNumberOrder(String color) {
        String empty = "   ";
        if (color.equals("BLACK")) {
            letters = List.of(empty, " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", empty);
            numbers = List.of(" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ");
        } else {
            letters = List.of(empty, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", empty);
            numbers = List.of(" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 ");
        }
    }

    private void printBoardPiece(PrintStream out, int row, int col) {
        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
        if (piece == null) {
            out.print("   ");
            return;
        }
        if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            out.print(SET_TEXT_COLOR_WHITE);
        } else {
            out.print(SET_TEXT_COLOR_BLACK);
        }
        out.print(getChessPieceChar(piece));
    }

    private String getChessPieceChar(ChessPiece piece) {
        String value = " ";
        switch (piece.getPieceType()) {
            case PAWN -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
            case QUEEN -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
            case KING -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_KING : BLACK_KING;
            case BISHOP -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
            case ROOK -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK;
            case KNIGHT -> value = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
        }
        return value;
    }
}
