package chess;

/* ---------------------------------------------------------------------------
 * A Queen can move as a Rook or as a Bishop. One way to accomplish this is
 * to define the Queen as an extension to a Rook, along with an encapsulation
 * of a Bishop.
 * ---------------------------------------------------------------------------
 */
public class Queen extends Rook {
	
	private Bishop bishop;

	/* -----------------------------------------------------------------------------
	 * The Queen occupies a single Square, whether it moves like a Rook or a Bishop. 
	 * -----------------------------------------------------------------------------
	 */
	public Queen(Player player)
	{
		super(player );
		bishop = new Bishop( player, this.square  );
	}
	
	/* -----------------------------------------------------------------------------
	 * The Queen occupies a single Square, whether it moves like a Rook or a Bishop. 
	 * -----------------------------------------------------------------------------
	 */
	public void setSquare( int row, int column )
	{
		super.setSquare(row, column);
		bishop.setSquare(row, column);
	}
	
	public String type()
	{	
		return "Queen";
	}
	
	/* --------------------------------------------------------------------------------
	 * A Queen can move as a Rook or as a Bishop.  
	 * ---------------------------------------------------------------------------------
	 */
	public boolean isValidMove(Move move, IChessPiece[][] board)
	{
//		>> TO DO
		
		return super.isValidMove(move, board) || bishop.isValidMove(move, board);
	}
	
	public String toString()
	{
		return type() + square.toString();
	}
}
