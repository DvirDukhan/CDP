package ex1;

/**
 * 
 * This class representing a 2D coordinate.
 * 
 * @author Dvir Dukhan
 * 
 *
 */

public class Coordinate {
	
	/**
	 *  x value of the coordinate.
	 */
	int x;
	
	/**
	 * y value of the coordinate.
	 */
	int y;
	
	
	/**
	 * 
	 * Coordinate class constructor
	 * 
	 * @param inputX - input for x value.
	 * @param inputY - input for y value.
	 */
	public Coordinate (int inputX, int inputY)
	{
		x = inputX;
		y = inputY;
	}
	
	/**
	 * 
	 * @return The x value of the coordinate.
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * 
	 * @return The y value of the coordinate.
	 */
	public int getY()
	{
		return y;
	}
	
	/**
	 *  returns the value in the format of 
	 *  "(x, y)"
	 */
	
	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
	
	@Override
	public boolean equals(Object other)
	{
		 if ( this == other ) 
		 {
			 return true;
		 }
		 
		 if ( other == null || !(other instanceof Coordinate) )
		 {
			 return false;
		 }
		 
		 Coordinate otherCoordinate = (Coordinate)other;
		 
		 return this.x==otherCoordinate .x && this.y == otherCoordinate .y;

	}
	
	
	/**
	 * Returns a unique value from two integers.
	 */
	
	@Override
	public int hashCode()
	{
		return y + ((x+y)*(x+y+1)/2);
	}
	


}
