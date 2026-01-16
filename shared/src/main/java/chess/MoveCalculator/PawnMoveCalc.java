package chess.MoveCalculator;

import chess.ChessMove;

import java.util.Collection;
import java.util.List;

public class PawnMoveCalc implements PieceMovesCalc{
    // directions this one can move
    // [0,1] or [0,2] if position = 1 for White or position = 7 for Black (not for board)

    @Override
    public Collection<ChessMove> getPieceMoves() {
        return List.of();
    }
}
