package ex1;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.TreeSet;

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
	Coordinate myConditionCoordinate;
	
	/**
	 * 
	 */
	int verticalSplits;
	
	
	/**
	 * 
	 */
	int horizontalSplits;
	
	/**
	 * 
	 */
	public BoardProcessor(  int inputI,
							int inputJ,
							boolean[][] conditionVariables,
							Tile[][] gameBoard,
							boolean[][] initialField,							
							boolean[][][] resultsBoards,
							int hSplit,
							int vSplit,
							int inputGenerations
							)
	{
		myConditionCoordinate = new Coordinate(inputI, inputJ);
		conditionsMatrix = conditionVariables;
		board = gameBoard;
		inputBoard = initialField;
		results = resultsBoards;
		horizontalSplits = hSplit;
		verticalSplits = vSplit;
		
		generations = inputGenerations;
		
		boardHeight = initialField.length;
		boardWidth = initialField[0].length;
		
		TreeSet<Integer> rows = getPartitions(0, boardHeight-1, verticalSplits);
		TreeSet<Integer> cols =  getPartitions(0, boardWidth-1, horizontalSplits);
		
		Integer[] rowsSplits = (Integer[])rows.toArray();
		Integer[] colsSplit = (Integer[])cols.toArray();
		
		topLeft = new Coordinate(rowsSplits[inputI], colsSplit[inputJ]);
		
		if (inputI<horizontalSplits-1)
		{
			miniBoardHeight  = rowsSplits[inputI +1] - rowsSplits[inputI];
		}
		else
		{
			miniBoardHeight = boardHeight - rowsSplits[inputI] -1;
		}
		
		if (inputJ<verticalSplits-1)
		{
			miniBoardWidth = colsSplit[inputJ +1] - colsSplit[inputJ];
		}
		else
		{
			miniBoardWidth = boardWidth - rowsSplits[inputJ] -1;
		}
		
		
		
	}
	
	/**
	 * 
	 */
	@Override
	public void run()
	{
		initializeBorders();
		synchronized (conditionsMatrix)
		{
			while(checkOnNeighbors() == false)
			{
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}	
		initializeSafeZone();
		
		while(finished.size()<miniBoardHeight*miniBoardWidth)
		{
			processTile(getNextReadyTile());
		}	
		
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean checkOnNeighbors()
	{
		
		
		int x = myConditionCoordinate.getX() + horizontalSplits;
		int y = myConditionCoordinate.getY() + verticalSplits;
		
		for (int i =(x -1) % horizontalSplits; i < (x + 1) % horizontalSplits; i++ )
		{
			for (int j =(y -1) % verticalSplits; j < (y + 1) % verticalSplits; j++ )
			{
				if (conditionsMatrix[i][j] == false)
				{
					return false;
				}
			}
		}
		
		return true;
		
		
	}
	
	
	/**
	 * 
	 */
	private void initializeBorders()
	{
		for (int i = topLeft.getX(); i<topLeft.getX()+miniBoardHeight;i++)
		{
			int j = topLeft.getY();
			board[i][j] = new Tile(i, j, inputBoard[i][j], boardHeight,  boardWidth);
			
			bordersReadyQueue.add(new Coordinate(i,j));
		
			j+=miniBoardWidth-1;
			board[i][j] = new Tile(i, j, inputBoard[i][j], boardHeight,  boardWidth);	
			
			bordersReadyQueue.add(new Coordinate(i,j));
		}
		
		
		for (int j = topLeft.getY(); j<topLeft.getY()+miniBoardWidth;j++)
		{
			int i = topLeft.getX();
			board[i][j] = new Tile(i, j, inputBoard[i][j], boardHeight,  boardWidth);
			
			bordersReadyQueue.add(new Coordinate(i,j));
			
			i+=miniBoardHeight-1;
			board[i][j] = new Tile(i, j, inputBoard[i][j], boardHeight,  boardWidth);	
			
			bordersReadyQueue.add(new Coordinate(i,j));
		}
		
		
		synchronized (conditionsMatrix)
		{
			conditionsMatrix[myConditionCoordinate.getX()][myConditionCoordinate.getY()] = true;
			notifyAll();
		}
		
		
	}
	
	/**
	 * 
	 */
	private void initializeSafeZone()
	{
		for (int i = topLeft.getX()+1; i < topLeft.getX()-1 + miniBoardHeight ; i++)
		{
			for (int j = topLeft.getY()+1; j < topLeft.getY()-1 + miniBoardWidth ; j++)
			{
				board[i][j] = new Tile(i, j, inputBoard[i][j], boardHeight,  boardWidth);	
				
				readyQueue.add(new Coordinate(i,j));
			}
		}
	}
	
	
	private Coordinate getNextReadyTile()
	{
		if (readyQueue.isEmpty()!= true)
		{
			return readyQueue.poll();
		}
		if (bordersReadyQueue.isEmpty()!= true)
		{
			return bordersReadyQueue.poll();
		}
		
		
		//fail safe - will never execute
		Coordinate tmpCoordinate = null;
		for(Coordinate coordinate : notReadyQueue)
		{
			if (board[coordinate.getX()][coordinate.getX()].isReadyToProcess()==true)
			{
				tmpCoordinate = coordinate;
				break;
			}
			
		}
		if (tmpCoordinate != null)
		{
			notReadyQueue.remove(tmpCoordinate);
			return tmpCoordinate;
		}
		else
		{
			for(Coordinate coordinate : bordersNotReadyQueue)
			{
				if (board[coordinate.getX()][coordinate.getX()].isReadyToProcess()==true)
				{
					tmpCoordinate = coordinate;
					break;
				}
				
			}
			bordersNotReadyQueue.remove(tmpCoordinate);
			return tmpCoordinate;
			
		}
		
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
	
	/**
	 * This function will return all the indices which represents margins of mini boards on the segment.
	 * @param left - The left most index of the segment.
	 * @param right - The right most index of the segment.
	 * @return A sorted set of indices which represents the segment's partition.
	 */
	public TreeSet<Integer> getPartitions(int left, int right , int splits)
	{
		TreeSet<Integer> results = new TreeSet<Integer>();
		if (splits==1){			
			results.add(left);
			return results;
		}
		int topPart = (int)Math.ceil(splits/2.0);
		int lastPart = splits/2;
		int middle = (int)Math.ceil((left + right)/2.0);
		results.addAll(getPartitions(left, middle, topPart));
		results.addAll(getPartitions(middle, right, lastPart));
		return results;
		
		
	}

}
