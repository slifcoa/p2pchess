package chess;

public class King extends ChessPiece {

	public King(Player player) {
		super(player);
	}

	public King(Player player, Square square) {
		super(player, square);
	}

	public String type() {
		return "King";
	}
	/* -------------------------------------------------------------------------
	 * A valid move for King must satisfy the following requirements:
	 *    1) A move for any chess piece in general;
	 *    2) The move must be restricted exclusively to either a row or a column;
	 *    3) The squares strictly between the "from" square and the "to" square
	 *       must be empty (unoccupied).
	 * -------------------------------------------------------------------------
	 */	

	public boolean isValidMove(Move move, IChessPiece[][] board)
	{		
		return 
				super.isValidMove(move, board) && 
				(Math.abs(move.to.row - move.from.row) <=1 && Math.abs(move.to.column - move.from.column) <=1) &&
				overEmptySquares( move, board );	
	}
	
	public String toString()
	{
		return type() + square.toString();
	}
}

