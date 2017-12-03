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

