package chess;

import java.util.*;


public class ChessModel implements IChessModel {
	private IChessPiece[][] board;
	private Player player;
	private IChessPiece[][] boardPattern;

	private IChessPiece bKing;
	private IChessPiece wKing;

	private ArrayList<IChessPiece> bPieces;
	private ArrayList<IChessPiece> wPieces;

	private int messageCode;

	public ChessModel() {

		createChessPieces();

		board = new IChessPiece[8][8];
		player = Player.WHITE;
		reset();
	}

	private void createChessPieces() {
		Player b = Player.BLACK;
		Player w = Player.WHITE;

		IChessPiece[][] boardPattern = {
				{ new Rook(b), new Knight(b), new Bishop(b), new Queen(b), new King(b), new Bishop(b), new Knight(b),
						new Rook(b) },
				{ new bPawn(b), new bPawn(b), new bPawn(b), new bPawn(b), new bPawn(b), new bPawn(b), new bPawn(b),
						new bPawn(b) },
				{ null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null },
				{ null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null },
				{ new wPawn(w), new wPawn(w), new wPawn(w), new wPawn(w), new wPawn(w), new wPawn(w), new wPawn(w),
						new wPawn(w) },
				{ new Rook(w), new Knight(w), new Bishop(w), new Queen(w), new King(w), new Bishop(w), new Knight(w),
						new Rook(w) }, };
		this.boardPattern = boardPattern;

		bKing = boardPattern[0][4];
		bPieces = new ArrayList<IChessPiece>();
		for (int r = 0; r < 2; r++) {
			for (int c = 0; c < 8; c++) {
				boardPattern[r][c].setSquare(r, c);
				bPieces.add(boardPattern[r][c]);
			}
		}
		wKing = boardPattern[7][4];
		wPieces = new ArrayList<IChessPiece>();
		for (int r = 7; r > 5; r--) {
			for (int c = 0; c < 8; c++) {
				boardPattern[r][c].setSquare(r, c);
				wPieces.add(boardPattern[r][c]);
			}
		}
	}

	private void reset() {
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				board[r][c] = boardPattern[r][c];
			}
		}
	}

	public boolean promote(Square s) {
		if (pieceAt(s).type().equals("Pawn")) {
			if (s.row == 0 || s.row == 7) {
				pieceAt(s).setSquare(-1, -1);
				Queen queen = new Queen(player);
				setPiece(s, queen);
				if (player == Player.WHITE) {
					wPieces.add(queen);
				} else {
					bPieces.add(queen);
				}

				return true;
			}
		}
		return false;
	}

	public void move(Move move) {
		move.fromPiece = pieceAt(move.from); // remembers the fromPiece
		move.toPiece = pieceAt(move.to); // remembers the toPiece

		if (move.toPiece != null) {
			move.toPiece.setSquare(-1, -1); // Removes the move.toPiece from the
											// board
		}

		setPiece(move.to, move.fromPiece); // moves the "moveFrom" piece to the
											// "move.to" location
		setPiece(move.from, null); // the board at the "moveFrom" location no
									// longer contains a piece.
	}

	public void undo(Move move) {
		setPiece(move.from, move.fromPiece);
		setPiece(move.to, move.toPiece);

	}

	public boolean isValidMove(Move m) {
		boolean inCheck1;
		boolean inCheck2;
		boolean valid = false;

		IChessPiece fromPiece = pieceAt(m.from);

		if (fromPiece.isValidMove(m, board)) {
			valid = true;
			inCheck1 = inCheck(player); // Checks to see if the
										// player's in check before
										// the move
			if (inCheck1) { // In check before move
				valid = false;
				messageCode = 1;
			}
			this.move(m);
			inCheck2 = inCheck(player);
			if (inCheck2) { // In check after move
				valid = false;
				messageCode = 2;
			} else {
				valid = true;
				messageCode = 0;
			}
			this.undo(m);
		} else {
			messageCode = 3;
		}

		return valid;
	}

	private boolean inCheck(IChessPiece king, ArrayList<IChessPiece> pieces) {
		Move move;
		Square square;
		Square kingSquare;
		ChessPiece cp;
		// The current square the king is on
		kingSquare = ((ChessPiece) king).square;
		// checks each piece in the Array list
		for (IChessPiece icp : pieces) {
			// gets the individual piece
			cp = (ChessPiece) icp;
			// sets the square of the individual piece
			if (cp.square.row != -1) {
				square = cp.square;

				// creates hypothetical move from each piece to king square
				move = new Move(square, kingSquare);
				// if the king is in another piece's striking distance.
				if (cp.isValidMove(move, board)) {
					return true;
				}
			}

		}
		return false;
	}

	public boolean inCheck(Player p) {
		IChessPiece king;
		ArrayList<IChessPiece> pieces;

		if (p == Player.BLACK) {
			king = bKing;
			pieces = wPieces;
		} else {
			king = wKing;
			pieces = bPieces;
		}

		return inCheck(king, pieces);
	}

	public boolean isCheckMate() {
		Move move;
		ChessPiece piece;
		ArrayList<IChessPiece> pieces;

		Square fromSquare = new Square(-1, -1);
		Square toSquare = new Square(-1, -1);

		// if current player is in check
		if (inCheck(player)) {

			if (player == Player.BLACK) {
				pieces = bPieces;
			} else {
				pieces = wPieces;
			}

			// Check for any possible valid move by any player piece to any
			// place on the board.

			for (IChessPiece icp : pieces) // An array list of the player's
											// pieces
			{
				piece = (ChessPiece) icp;

				if (piece.square.row != -1) // i.e. the chess piece is still on
											// the board
				{
					fromSquare.set(piece.square); // set the from square to the
													// piece

					for (int r = 0; r < 8; r++) { // Goes through each square on
													// the board
						for (int c = 0; c < 8; c++) {
							toSquare.set(r, c);
							move = new Move(fromSquare, toSquare);
							if (isValidMove(move)) {
								return false;
							}
						}
					}
				}
			}
		}

		return true;
	}

	public IChessPiece pieceAt(Square s) {
		return board[s.row][s.column];
	}

	public void setPiece(Square s, IChessPiece piece) {
		board[s.row][s.column] = piece;
		if (piece != null) {
			piece.setSquare(s.row, s.column);
		}
	}

	public Player currentPlayer() {
		return player;
	}

	public void setNextPlayer() {
		player = player.next();
	}

	public int getMessage() {
		return messageCode;
	}
}