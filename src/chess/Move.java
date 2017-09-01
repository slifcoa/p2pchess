package chess;

import javax.swing.ImageIcon;

public class Move {
	public Square from;
	public Square to;

	public IChessPiece fromPiece;
	public IChessPiece toPiece;

	public ImageIcon fromPieceIcon;
	public ImageIcon toPieceIcon;

	public Move(Square from, Square to) {
		this.from = from;
		this.to = to;
	}

	public boolean isValid() {
		return !from.equals(to);
	}

	public String toString() {
		return "from " + from.toString() + " to " + to.toString();
	}
}