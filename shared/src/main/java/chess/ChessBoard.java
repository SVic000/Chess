package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable{
    ChessPiece[][] grid = new ChessPiece[8][8];

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        grid[position.getRow()-1][position.getColumn()-1] = piece;
    }

    public boolean isOutOfBounds(ChessPosition position){
        return position.getRow() < 1 || position.getColumn() < 1 || position.getRow() > grid.length || position.getColumn() > grid.length;
    }


    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return grid[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessPiece.PieceType[] order = ChessPiece.getSetup();
        grid = new ChessPiece[8][8];
        for(int i = 0; i < grid.length; i++) {
            grid[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            grid[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            grid[0][i] = new ChessPiece(ChessGame.TeamColor.WHITE,order[i]);
            grid[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK,order[i]);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(grid, that.grid);
    }

    @Override
    public Object clone() {
        try { // making sure I copy everything inside too!
            ChessBoard copy = (ChessBoard) super.clone();

            ChessPiece[][] newLines = new ChessPiece[grid.length][];
            for(int i = 0; i < grid.length; i++) {
                newLines[i] = grid[i].clone();
            }

            copy.grid = newLines;
            return copy;
        } catch(CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "grid=" + Arrays.toString(grid) +
                '}';
    }
}
