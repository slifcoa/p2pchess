package chess;


public class Bishop extends ChessPiece {

	public Bishop(Player player)
	{
		super(player);
	}

	public Bishop(Player player, Square square )
	{
		super( player, square );
	}

	public String type() {

		return "Bishop";	
	}

	/* -------------------------------------------------------------------------
	 * A valid move for Bishop must satisfy the following requirements:
	 *    1) A move for any chess piece in general;
	 *    2) The move must be restricted exclusively to going diagonal;
	 *    3) The squares strictly between the "from" square and the "to" square
	 *       must be empty (unoccupied).
	 * -------------------------------------------------------------------------
	 */	

	public boolean isValidMove(Move move, IChessPiece[][] board)
	{		
		return 
				super.isValidMove(move, board) && 
				(Math.abs(move.to.row - move.from.row) == Math.abs(move.to.column - move.from.column)) &&
				overEmptySquares( move, board );	
	}
	
	public String toString()
	{
		return type() + square.toString();
	}
}