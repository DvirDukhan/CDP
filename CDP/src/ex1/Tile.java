package ex1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;



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
	Coordinate coordinate;
	
	/**
	 * Represents the Tile's current state.
	 */
	boolean state;
	
	
	/**
	 * Represents the Tile's previous state.
	 */
	boolean previousState;
	

	
	
	/**
	 * Represents the Tile's generation. 
	 */
	int age = 0;
	
	
	/**
	 * holds the neighbors current ages.
	 */
	HashMap<Coordinate, Integer> neighborsAgesMap = new HashMap<Coordinate, Integer>();
	
	
	
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
		for (int i = x - 1 + boardHeight ; i < x + 2 + boardHeight; i++)
		{
			for(int j = y - 1 + boardWidth; j < y + 2 + boardWidth; j++)
			{
				
				/*
				if(i % boardHeight == x && j % boardWidth == y)
				{
					continue;
				}
				*/
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
	 * @return - The Tile's state.
	 */
	public Boolean getState()
	{
		return state;
	}
	
	
	/**
	 * 
	 * @return - The Tile's previous state.
	 */
	public Boolean getPreviousState()
	{
		return previousState;
	}
	
	
	
	/**
	 * 
	 * This function return a tile state by requested age.
	 * 
	 * @param otherAge - Other tile's age.
	 * @return The Tile's state in the desired age.
	 */
	public boolean getState(int otherAge)
	{
		if (age == otherAge)
		{
			return state;
		}
		return previousState;
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
	
	
	/**
	 * This function increases the Tile's age by 1.
	 */
	public void increaseAge()
	{
		age++;
	}
	
	/**
	 * 
	 * @return The Tile's neighbors coordinate.
	 */
	public Set<Coordinate> getNeighborsCoordinate()
	{
		return neighborsAgesMap.keySet();
	}
	
	
	/**
	 * Sets the state of the tile.
	 * @param newState - The new state of the tile.
	 */
	public void setState(boolean newState)
	{
		previousState = state;
		state = newState;
	}
	
	
}
