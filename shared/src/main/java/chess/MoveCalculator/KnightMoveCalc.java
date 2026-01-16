package chess.MoveCalculator;

import chess.ChessMove;

import java.util.Collection;
import java.util.List;

public class KnightMoveCalc implements PieceMovesCalc{
    // directions this one can move:
    // 1,2 : 1,-2 : -1,2 : -1,-2 : 2,1 : 2,-1 : -2,1 : -2,-1

    @Override
    public Collection<ChessMove> getPieceMoves() {
        return List.of();
    }
}
