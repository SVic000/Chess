package chess;

import chess.MoveCalculator.*;

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
    private final Collection<PieceType> promotable = new ArrayList<>();
    private final static List<PieceType> promotion = List.of(PieceType.QUEEN, PieceType.ROOK,PieceType.BISHOP,PieceType.KNIGHT);


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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

    public static List<PieceType> promotionsList() {
        return promotion;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalc pieceMovesCalc;
        ChessPiece piece = board.getPiece(myPosition);

        return switch (type) {
            case BISHOP -> {
                pieceMovesCalc = new BishopMoveCalc(piece, board, myPosition);
                yield pieceMovesCalc.getPieceMoves();
            }
            case QUEEN -> {
                pieceMovesCalc = new QueenMoveCalc(piece, board, myPosition);
                yield pieceMovesCalc.getPieceMoves();
            }
            case ROOK -> {
                pieceMovesCalc = new RookMoveCalc(piece, board, myPosition);
                yield pieceMovesCalc.getPieceMoves();
            }
            case KING -> {
                pieceMovesCalc = new KingMoveCalc(piece, board, myPosition);
                yield pieceMovesCalc.getPieceMoves();
            }
            case KNIGHT -> {
                pieceMovesCalc = new KnightMoveCalc(piece, board, myPosition);
                yield pieceMovesCalc.getPieceMoves();
            }
            case PAWN -> {
                pieceMovesCalc = new PawnMoveCalc(piece, board, myPosition);
                yield pieceMovesCalc.getPieceMoves();
            }
        };
    }
}
