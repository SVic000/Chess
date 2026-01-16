package chess.MoveCalculator;

import chess.ChessMove;

import java.util.Collection;
import java.util.List;

public class KingMoveCalc implements PieceMovesCalc{
   // directions this one can move
    // 1,1 : 1,0 : 1,-1 : 0,1 : 0,-1 : -1,1 : -1,0 : -1,-1
    @Override
    public Collection<ChessMove> getPieceMoves() {
        return List.of();
    }
}
