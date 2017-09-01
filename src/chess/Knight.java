package chess;

public class Knight extends ChessPiece {

	public Knight(Player player) {
		super(player);
	}

	public Knight(Player player, Square square) {
		super(player, square);
	}

	public String type() {

		return "Knight";
	}

	/*
	 * -------------------------------------------------------------------------
	 * A valid move for Knight must satisfy the following requirements: 1) A
	 * move for any chess piece in general; 2) The move must be restricted
	 * exclusively to going (up or down) 2 and side 1 or side 2 and (up or down)
	 * 1 ; 
	 * -------------------------------------------------------------------------
	 */

	public boolean isValidMove(Move move, IChessPiece[][] board) {
		return super.isValidMove(move, board) && ((Math.abs(move.to.row - move.from.row) == 2
				&& Math.abs(move.to.column - move.from.column) == 1)
				|| (Math.abs(move.to.row - move.from.row) == 1 && Math.abs(move.to.column - move.from.column) == 2));
	}

	public String toString() {
		return type() + square.toString();
	}
}