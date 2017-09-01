package chess;

public abstract class ChessPiece implements IChessPiece {

	private Player owner;
	public Square  square;		// added 3-10-2016 LJK

	protected ChessPiece(Player player)
	{
		this.owner  = player;
		this.square = new Square( -1, -1 );
	}

	protected ChessPiece(Player player, Square square )
	{
		this.owner  = player;
		this.square = square;
	}

	public void setSquare( int row, int column )		// added 3-10-2016, LJK
	{
		square.set(row, column);
	}	

	public abstract String type();

	public Player player()
	{
		return owner;
	}

	/* -------------------------------------------------------------------------
	 * A valid move for any chess piece, in general, must satisfy the following
	 * requirements:
	 *    1) Every move, in general, involves two distinctly different squares
	 *       on the board;
	 *    2) The "from" square must contain a chess piece;
	 *    3) The "to" square is either exclusively empty or it contains a chess
	 *       piece of the opposite color, which is subsequently taken, i.e.
	 *       removed from play, and no longer on the board.
	 * -------------------------------------------------------------------------
	 */	
	public boolean isValidMove(Move move, IChessPiece[][] board)
	{	
		return	move.isValid( ) &&
				board[move.from.row][move.from.column] != null &&
				(board[move.to.row][move.to.column] == null || moveToOpponentPiece( move, board ));
	}

	public boolean moveToOpponentPiece( Move move, IChessPiece[][] board )
	{
		return board[move.from.row][move.from.column].player() != board[move.to.row][move.to.column].player();
	}

	/* -------------------------------------------------------------------------
	 * The overEmptySquares returns :
	 *    1) true - if all squares exclusively between the "from" square and the
	 *              "to" square are empty.
	 *    2) false - if at least one square between the "from" square and the
	 *               "to" square is occupied.
	 * -------------------------------------------------------------------------
	 */	
	protected boolean overEmptySquares( Move move, IChessPiece[][] board)
	{
		Square unit = move.from.unitStepSquare(move.to);
		Square temp = new Square( move.from );
		
		temp.plus(unit);
		//if the piece has not yet reached its landing spot.
		while(!temp.equals(move.to)){
			if(board[temp.row][temp.column] != null){
				return false;
			}
			temp.plus(unit);
		}
		
		return true;	
	}

}