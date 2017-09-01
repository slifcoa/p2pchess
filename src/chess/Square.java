package chess;

public class Square
{
	public int row, column;

	public Square( int row, int column )
	{
		this.row    = row;
		this.column = column;
	}
	
	public Square( Square s )
	{
		this.row    = s.row;
		this.column = s.column;
	}
	
	public void set( int row, int column )
	{
		this.row    = row;
		this.column = column;
	}
	
	public void set( Square s )
	{
		this.row    = s.row;
		this.column = s.column;
	}
	
	public boolean equals( Square s )
	{
		return this.row == s.row && this.column == s.column;
	}
	
	/*
	 * The row and column values of a unitStepSquare are either -1, 0, or 1
	 */
	public Square unitStepSquare( Square s )
	{
		Square step = this.direction( s );
		//if it's going up or down
		if (step.row != 0)
		{
			step.row = step.row / Math.abs( step.row );
		}
		//if it's going left or right
		if (step.column != 0)
		{
			step.column = step.column / Math.abs( step.column );
		}
		return step;
	}
	
	public Square direction( Square s )
	{
		Square vector = new Square( 0, 0 );

		vector.row    = s.row    - this.row;
		vector.column = s.column - this.column;

		return vector;
	}
	
	public void plus( Square s )
	{	
		this.row    = this.row    + s.row;
		this.column = this.column + s.column;
	}
	
	public String toString( )
	{
		return  "( " + row + "," + column + " )";
	}
}