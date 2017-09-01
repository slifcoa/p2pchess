package chess;

public class bPawn extends ChessPiece {

	public bPawn(Player player) {
		super(player);
	}

	public bPawn(Player player, Square square) {
		super(player, square);
	}

	public String type() {

		return "Pawn";
	}

	/*
	 * -------------------------------------------------------------------------
	 * A valid move for the Black Pawn must satisfy the following requirements:
	 * 1) A move for any chess piece in general; 2) The move must be restricted
	 * exclusively to down one spot or two if it's from its starting location;
	 * TODO: 3) Can go diagonal down one spot if the other player has a piece
	 * there; 4) The squares strictly between the "from" square and the "to"
	 * square must be empty (unoccupied).
	 * -------------------------------------------------------------------------
	 */

	public boolean isValidMove(Move move, IChessPiece[][] board) {
		return super.isValidMove(move, board)
				&& ((move.from.row == 1 && move.to.row - move.from.row == 2 && move.to.column == move.from.column && board[2][move.to.column] == null && board[3][move.to.column] == null)
						|| (move.to.row - move.from.row == 1 && board[move.to.row][move.to.column] == null && move.to.column == move.from.column))
				|| (move.to.row - move.from.row == 1 && Math.abs(move.from.column - move.to.column) == 1
						&& board[move.to.row][move.to.column] != null) && overEmptySquares(move, board);
	}

	public String toString() {
		return type() + square.toString();
	}
}