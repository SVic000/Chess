package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMoveCalc implements PieceMovesCalc {
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition position;
    private final Collection<ChessMove> possibleMovement = new ArrayList<>();

    public QueenMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
    }

    @Override
    public Collection<ChessMove> getPieceMoves() {
        PieceMovesCalc bishopMove = new BishopMoveCalc(piece,board, position); // bishop
        PieceMovesCalc rookMove = new RookMoveCalc(piece,board,position); // rook
        possibleMovement.addAll(bishopMove.getPieceMoves());
        possibleMovement.addAll(rookMove.getPieceMoves());
        return possibleMovement;
    }
}
