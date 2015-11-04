package ex1;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * This class represents a tile on the board
 * @author Dvir Dukhan
 *
 *
 */

public class Tile 
{
	
	/**
	 * Represents the Tile's coordinate.
	 */
	private Coordinate coordinate;
	
	/**
	 * Represents the Tile's current state.
	 */
	private Boolean state;
	
	/**
	 *  Represents the Tile's state in the past generations.
	 */
	ArrayList<Boolean> previusStates = new ArrayList<Boolean>();
	
	
	/**
	 * Represents the Tile's generation. 
	 */
	private int age = 0;
	
	
	/**
	 * holds the neighbors current ages.
	 */
	private HashMap<Coordinate, Integer> neighborsAgesMap = new HashMap<Coordinate, Integer>();
	
	
	
	/**
	 * 
	 * Tile class constructor.
	 * 
	 * @param x - The Tile's x coordinate.
	 * @param y - The Tile's y coordinate.
	 * @param inputState - The initials state of the tile.
	 * @param boardHeight - The height of the board.
	 * @param boardWidth - The width of the board.
	 */
	public Tile (int x, int y, Boolean inputState, int boardHeight, int boardWidth)
	{
		coordinate = new Coordinate(x, y);
		state = inputState;	
		for (int i = x - 1 + boardHeight ; i < x + 1 + boardHeight; i++)
		{
			for(int j = y - 1 + boardWidth; j < y + 1 + boardWidth; j++)
			{
				if(i % boardHeight == x && j % boardWidth == y)
				{
					continue;
				}
				Coordinate c = new Coordinate(i % boardHeight,j % boardWidth);
				neighborsAgesMap.put(c,0);
			}
		}
	}
	
	
	/**
	 * 
	 * @return The Tile's coordinate.
	 */
	public Coordinate getCoordinate()
	{
		return coordinate;
	}
	
	
	/**
	 * 
	 * @return The Tile's current state.
	 */
	public Boolean getState()
	{
		return state;
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param age - The desired age of the tile.
	 * @return The state of the tile in the desired age.
	 */
	public Boolean getPreviusState(int age)
	{
		return previusStates.get(age);
	}
	
	
	/**
	 * 
	 * 
	 * @return The age of the tile.
	 */
	public int getAge()
	{
		return age;
	}
	
	/**
	 * 
	 * Updates a neighbor's age
	 * 
	 * @param c - the neighbor's coordinate.
	 * @param age - the neighbor's age.
	 */
	
	public void updateNeighborAge(Coordinate c, int age)
	{
		neighborsAgesMap.put(c, age);
	}
	
	
	/**
	 * This function goes over all the Tile's neighbors ages and if their ages are equal or 
	 * equal to the Tile's age +1 then the tile is ready to be processed.
	 * @return if the tile is ready to be processed.
	 */
	public Boolean isReadyToProcess()
	{
		for (Coordinate neighborCoordinate : neighborsAgesMap.keySet())
		{
			int neighborAge = neighborsAgesMap.get(neighborCoordinate);
			if (neighborAge < age)
			{
				return false;
			}
			
			if (neighborAge > age+1)
			{	
				System.err.println("neighbor in coordinate " + neighborCoordinate.toString() + " age = " + neighborAge + 
						"\nthis coordiante " + coordinate.toString() + " age = " + age);
				return false;
			}
		}
		return true;
	}
	
	
}
