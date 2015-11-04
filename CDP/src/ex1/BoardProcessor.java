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
	 * 
	 */
	ArrayList<Coordinate> finished = new ArrayList<Coordinate>();
	
	
	/**
	 * 
	 */
	Coordinate topLeft;
	
	/**
	 * 
	 */
	int miniBoardHeight;
	
	/**
	 * 
	 */
	int miniBoardWidth;
	
	/**
	 * 
	 */
	boolean[][] inputBoard;

	
	/**
	 * 
	 */
	Tile[][] board;
	
	/**
	 * 
	 */
	int boardHeight;

	/**
	 * 
	 */
	int boardWidth;
	
	int generations;
	boolean[][][] results;
	
	
	/**
	 * 
	 */
	public BoardProcessor()
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
	

}
