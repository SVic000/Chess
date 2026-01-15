package chess.MoveCalc;
import chess.ChessMove;
import chess.ChessPiece;

import java.util.Collection;
import java.util.List;

//
 /*
 public interface PieceMovesCalc {
    public peicemoves;
    public default diag() {
        bishop and queen calls
    }
    public default plus() [
        rook and queen
    }
 }
  */
//

public class PieceMovesInterface {
    private List<ChessMove> possiblemoves;
    private ChessPiece piece;

    public PieceMovesInterface(ChessPiece piece){
        this.piece = piece;
    }
    public PieceMovesInterface {


    }
    public PieceMovesInterface {
        public Collection<ChessMove> pieceMoveSet(ChessBoard board, ChessPosition position){
            return List.of();
        }
    } // talk to a TA about this Like logistics
}
