package ex1;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * An instance of this class can process a mini board given to it.
 * The process is done in parallel.
 * 
 * 
 * @author Dvir Dukhan
 *
 */
public class BoardProcessor implements Runnable 
{
	/**
	 * This queue will hold the coordinates of all the Tiles ready to be processed.
	 */
	Queue<Coordinate> readyQueue = new ArrayDeque<Coordinate>();
	
	
	/**
	 *This queue will hold the coordinates of all the Tiles not ready to be processed.
	 *<p> 
	 *Under the assumption that the first processed Tile is the first tile to be processed if the ready queue is empty.
	 *This assumption is a fail-safe mechanism because there is low to zero probability for this to happen.
	 */
	Queue<Coordinate> notReadyQueue = new ArrayDeque<Coordinate>();
	
	
	/**
	 * This array holds the coordinates of all the tiles that are in maximum generation.
	 */
	ArrayList<Coordinate> finished = new ArrayList<Coordinate>();
	
	
	/**
	 * Represents the top left coordinate of the miniboard.
	 */
	Coordinate topLeft;
	
	/**
	 * Represents the miniboard height.
	 */
	int miniBoardHeight;
	
	/**
	 * Represents the miniboard width.
	 */
	int miniBoardWidth;
	
	/**
	 * The original input board. Used for parallel initialization.
	 */
	boolean[][] inputBoard;

	
	/**
	 * The actual game board.
	 */
	Tile[][] board;
	
	/**
	 * Represents the game board height.
	 */
	int boardHeight;

	/**
	 * Represents the game board width.
	 */
	int boardWidth;
	
	
	/**
	 * The number of the desired generations the game should run;
	 */
	int generations;
	
	/**
	 * The output for the game.
	 */
	boolean[][][] results;
	
	
	/**
	 * 
	 */
	public BoardProcessor( boolean[][] intialField,
							Tile[][] gameBoard,
							boolean[][][] resultsBoards,
							Coordinate topLeftPosition,
							int miniboardHeight,
							int miniboardwidth,
							int generations)
	{
		
	}
	
	/**
	 * 
	 */
	@Override
	public void run()
	{
		initializeSafeZone();
		addBordersToReadyQueue();
		
	}
	
	/**
	 * 
	 */
	private void initializeSafeZone()
	{
		
	}
	
	
	/**
	 * 
	 */
	private void addBordersToReadyQueue()
	{
		
	}
	
	/**
	 * Returns if a coordinate is on the border of a miniboard.
	 * @param coordinate - A Tile coordinate.
	 * @return If the tile is on the border of the miniboard.
	 */
	private boolean isInBorder(Coordinate coordinate)
	{
		if (coordinate.getX() == topLeft.getX() ||
			coordinate.getX() == topLeft.getX()+miniBoardHeight-1 ||
			coordinate.getY() == topLeft.getY() ||
			coordinate.getY() == topLeft.getY()+miniBoardWidth-1)
			return true;
		
		return false;
	}
	
	/**
	 * Returns if a coordinate is outside the miniboard.
	 * @param coordinate - A Tile coordinate.
	 * @return If the tile is outside the miniboard.
	 */
	private boolean isOutOfMiniboard(Coordinate coordinate)
	{
		if (coordinate.getX() == topLeft.getX() -1 ||
			coordinate.getX() == topLeft.getX()+miniBoardHeight ||
			coordinate.getY() == topLeft.getY()-1 ||
			coordinate.getY() == topLeft.getY()+miniBoardWidth)
			return true;
		
		return false;
	}
	
	
	
	
	/**
	 * This functions checks if the tile should be dead or alive in the next generation.
	 * <p>
	 * If the current generation is in the requested generations for results than the Tile's status will be written in the results.
	 * <p>
	 * If the Tile current generation is maximum generation than the tile will be moved to finished and will not be processed anymore.
	 * @param coordinate - The coordinate of tile to process
	 */
	private void processTile(Coordinate coordinate)
	{
		Tile tile = board[coordinate.getX()][coordinate.getY()];
		
		//do the stuff
		int counter = 0;
		for (Coordinate neighborCoordinate: tile.getNeighborsCoordinate())
		{
			
			Tile neighbor = board[neighborCoordinate.getX()][neighborCoordinate.getY()];
			
			if (isOutOfMiniboard(neighborCoordinate))
			{
				synchronized (neighbor)
				{
					if (neighbor.getAge() == tile.getAge())
					//counter+=neighbor.getState();
				}
			}
			
		}
		//TODO:implement!!!
		
		
		
		tile.increaseAge();
		
		//check if need to write to results or 
		if (tile.getAge()==generations-1)
		{
			results[0][coordinate.getX()][coordinate.getY()] = tile.getState(tile.getAge());
		}
		
		if (tile.getAge()==generations)
		{
			results[1][coordinate.getX()][coordinate.getY()] = tile.getState(tile.getAge());
			finished.add(tile.getCoordinate());
		}
		else
		{
			notReadyQueue.add(tile.getCoordinate());
		}	
	}
	
	
	/**
	 * 
	 * @param coordinate - The Tile's coordinate.
	 * @param age - The Tile's requsted age.
	 * @return The Tile's state in the given age.
	 */
	private boolean getTileState(Coordinate coordinate, int age)
	{
		Tile tile = board[coordinate.getX()][coordinate.getY()];
		if (isOutOfMiniboard(tile.getCoordinate()))
		{
			synchronized (tile) {
				return tile.getState(age);
			}
		}
		else
		{
			return tile.getState(age);
		}
	}
	

}
