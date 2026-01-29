package chess;

import chess.PieceMoveCalc.*;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private final static PieceType[] promotions = {PieceType.ROOK,PieceType.QUEEN,PieceType.BISHOP,PieceType.KNIGHT};
    private final static PieceType[] setup = {
            PieceType.ROOK,PieceType.KNIGHT,
            PieceType.BISHOP,PieceType.QUEEN,
            PieceType.KING,PieceType.BISHOP,
            PieceType.KNIGHT,PieceType.ROOK
        };



    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public static PieceType[] getPromotions() {
        return promotions;
    }

    public static PieceType[] getSetup() {
        return setup;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMoveCalc pieceMoveCalc;
        switch(type) {
            case BISHOP -> {
                pieceMoveCalc = new BishopMoveCalc(this,myPosition,board);
                return pieceMoveCalc.getPieceMoves();
            }
            case PAWN-> {
                pieceMoveCalc = new PawnMoveCalc(this,myPosition,board);
                return pieceMoveCalc.getPieceMoves();
            } case KNIGHT -> {
                pieceMoveCalc = new KnightMoveCalc(this,myPosition,board);
                return pieceMoveCalc.getPieceMoves();
            } case KING -> {
                pieceMoveCalc = new KingMoveCalc(this,myPosition,board);
                return pieceMoveCalc.getPieceMoves();
            } case ROOK -> {
                pieceMoveCalc = new RookMoveCalc(this,myPosition,board);
                return pieceMoveCalc.getPieceMoves();
            } case QUEEN -> {
                pieceMoveCalc = new QueenMoveCalc(this,myPosition,board);
                return pieceMoveCalc.getPieceMoves();
            }
        }
        return java.util.List.of();
    }
}
