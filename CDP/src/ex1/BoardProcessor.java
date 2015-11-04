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
	 * This queue will hold the coordinates of all the Tiles ready to be processed in the borders.
	 */
	Queue<Coordinate> bordersReadyQueue = new ArrayDeque<Coordinate>();
	
	
	/**
	 *This queue will hold the coordinates of all the Tiles not ready to be processed.
	 *<p> 
	 *Under the assumption that the first processed Tile is the first tile to be processed if the ready queue is empty.
	 *This assumption is a fail-safe mechanism because there is low to zero probability for this to happen.
	 */
	Queue<Coordinate> notReadyQueue = new ArrayDeque<Coordinate>();
	
	
	/**
	 * This queue will hold the coordinates of all the Tiles not ready to be processed in the borders.
	 */
	Queue<Coordinate> bordersNotReadyQueue = new ArrayDeque<Coordinate>();
	
	
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
	 * a global matrix which represents when a thread can start to work.
	 */
	boolean[][] conditionsMatrix;
	
	/**
	 * 
	 */
	public BoardProcessor(  int i,
							int j,
							boolean[][] conditionVariables,
							Tile[][] gameBoard,
							boolean[][] intialField,							
							boolean[][][] resultsBoards,
							int hSplit,
							int vSplit,
							int generations
							)
	{
		
	}
	
	/**
	 * 
	 */
	@Override
	public void run()
	{
		initializeBorders();
		initializeSafeZone();
		addBordersToReadyQueue();
		
	}
	
	
	private void initializeBorders()
	{
		
	}
	
	/**
	 * 
	 */
	private void initializeSafeZone()
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
			
			
			boolean state = getTileState(neighborCoordinate, tile.getAge());
			if (state == true)
			{
				counter++;
			}
			
		}
		
		//game's logic
		
		if (tile.getState() == false && counter == 3)
		{
			setTileState(tile, true);
		}
		else 
			if (tile.getState() == true && (counter == 3 || counter == 2 ))
			{
			//do nothing
			}
			else
			{
				setTileState(tile, false);
			}
		//TODO:implement!!!
		
		
		tile.increaseAge();
		
		for (Coordinate neighborCoordinate: tile.getNeighborsCoordinate())
		{
			updateAgeAtNeighbor(tile.getCoordinate(), neighborCoordinate, tile.getAge());
		}
		
				
		if (tile.getAge()==generations)
		{
			results[0][coordinate.getX()][coordinate.getY()] = tile.getPreviousState();
			results[1][coordinate.getX()][coordinate.getY()] = tile.getState();
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
	
	/**
	 * Update a tile's state.
	 * @param tile - The tile to be update.
	 * @param state - The Tile's new state.
	 */
	private void setTileState(Tile tile, boolean state)
	{
		if (isInBorder(tile.getCoordinate()))
		{
			synchronized (tile)
			{
				tile.setState(state);
			}
		}
		else
		{
			tile.setState(state);
		}
	}
	
	
	/**
	 * Updates the processed tile's age at its neighbor.
	 * @param currentTileCoordinate - The processed tile coordinate.
	 * @param neighborCoordinate - The neighbor to update.
	 * @param currentTileAge - The age of the processed tile's age.
	 */
	private void updateAgeAtNeighbor(Coordinate currentTileCoordinate, Coordinate neighborCoordinate, int currentTileAge)
	{
		Tile neighbor = board[neighborCoordinate.getX()][neighborCoordinate.getY()];
		if (isOutOfMiniboard(neighborCoordinate))
		{
			synchronized ( neighbor)
			{
				 neighbor.updateNeighborAge(currentTileCoordinate, currentTileAge);
			}
		}
		if (isInBorder(neighborCoordinate))
		{
			boolean isReady;
			synchronized ( neighbor)
			{
				 neighbor.updateNeighborAge(currentTileCoordinate, currentTileAge);
				 isReady = neighbor.isReadyToProcess();
				
			}
			 if (isReady == true)
			 {
				 makeReady(neighborCoordinate);
			 }
		}
		else
		{
			boolean isReady;
			neighbor.updateNeighborAge(currentTileCoordinate, currentTileAge);
			isReady = neighbor.isReadyToProcess();
			if (isReady == true)
			{
				 makeReady(neighborCoordinate);
			}
		}
	}
	
	
	
	/**
	 * Moves the tile coordinate from a "not ready queue" to a "ready queue".
	 * @param readyTileCoordinate - The coordinate of the ready tile.
	 */
	private void makeReady(Coordinate readyTileCoordinate)
	{
		if (isInBorder(readyTileCoordinate))
		{
			if (bordersNotReadyQueue.contains(readyTileCoordinate))
			{
				bordersNotReadyQueue.remove(readyTileCoordinate);
				bordersReadyQueue.add(readyTileCoordinate);
			}
		}
		else
		{

			if (notReadyQueue.contains(readyTileCoordinate))
			{
				notReadyQueue.remove(readyTileCoordinate);
				readyQueue.add(readyTileCoordinate);
			}
		}
	}

}
