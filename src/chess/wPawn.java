package chess;

public class wPawn extends ChessPiece {

	public wPawn(Player player) {
		super(player);
	}

	public wPawn(Player player, Square square) {
		super(player, square);
	}

	public String type() {

		return "Pawn";
	}

	/*
	 * -------------------------------------------------------------------------
	 * A valid move for the White Pawn must satisfy the following requirements:
	 * 1) A move for any chess piece in general; 2) The move must be restricted
	 * exclusively to up one spot or two if it's from its starting location;
	 * TODO: 3) Can go diagonal up one spot if the other player has a piece
	 * there; 4) The squares strictly between the "from" square and the "to"
	 * square must be empty (unoccupied).
	 * -------------------------------------------------------------------------
	 */

	public boolean isValidMove(Move move, IChessPiece[][] board)
	{		
		return 
				super.isValidMove(move, board) && 
				((move.from.row == 6 && move.from.row - move.to.row == 2 && move.from.column == move.to.column && board[4][move.to.column] == null && board[5][move.to.column] == null) || 
						(move.from.row - move.to.row == 1 && board[move.to.row][move.to.column] == null && move.from.column == move.to.column) || 
 (move.from.row - move.to.row == 1 && Math.abs(move.from.column - move.to.column) == 1	&& board[move.to.row][move.to.column] != null) &&
				overEmptySquares( move, board ));	
	}

	public String toString() {
		return type() + square.toString();
	}
}