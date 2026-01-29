package chess.PieceMoveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveCalc implements pieceMoveCalc {
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition position;
    private final Collection<ChessMove> possibleMovement = new ArrayList<>();

    public QueenMoveCalc(ChessPiece piece, ChessPosition position, ChessBoard board) {
        this.piece = piece;
        this.board = board;
        this.position = position;
    }

    @Override
    public Collection<ChessMove> getPieceMoves() {
        pieceMoveCalc bishopMove = new BishopMoveCalc(piece, position, board); // bishop
        pieceMoveCalc rookMove = new RookMoveCalc(piece, position, board); // rook
        possibleMovement.addAll(bishopMove.getPieceMoves());
        possibleMovement.addAll(rookMove.getPieceMoves());
        return possibleMovement;
    }
}
