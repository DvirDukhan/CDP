package ex1;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
	Queue<Coordinate> readyQueue = new LinkedList<Coordinate>();
	
	/**
	 * This queue will hold the coordinates of all the Tiles ready to be processed in the borders.
	 */
	Queue<Coordinate> bordersReadyQueue = new LinkedList<Coordinate>();
	
	
	/**
	 *This queue will hold the coordinates of all the Tiles not ready to be processed.
	 *<p> 
	 *Under the assumption that the first processed Tile is the first tile to be processed if the ready queue is empty.
	 *This assumption is a fail-safe mechanism because there is low to zero probability for this to happen.
	 */
	Queue<Coordinate> notReadyQueue = new LinkedList<Coordinate>();
	
	
	/**
	 * This queue will hold the coordinates of all the Tiles not ready to be processed in the borders.
	 */
	Queue<Coordinate> bordersNotReadyQueue = new LinkedList<Coordinate>();
	
	
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
	 * Represents the coordinate of the instance of this class in the conditions board.
	 */
	Coordinate myConditionCoordinate;
	
	/**
	 * Represents the number of vertical splits of the game board.
	 */
	int verticalSplits;
	
	
	/**
	 *  Represents the number of horizontal splits of the game board.
	 */
	int horizontalSplits;
	
	
	/**
	 * BoardProcessor constructor.
	 * @param inputI - the x value of the board processor coordinate in the conditions matrix.
	 * @param inputJ - the y value of the board processor coordinate in the conditions matrix.
	 * @param conditionVariables - A boolean hsplit*vsplit matrix which holds for each  board processor if it finished to initialize its boarders.
	 * @param gameBoard - The actual gameboard.
	 * @param initialField - The initial values for the board.
	 * @param resultsBoards - The results boards.
	 * @param hSplit - The number of horizontal splits.
	 * @param vSplit - The number of vertical splits.
	 * @param inputGenerations - The number of generations the game should play.
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
		
		TreeSet<Integer> rows = getPartitions(0, boardHeight, verticalSplits);
		TreeSet<Integer> cols =  getPartitions(0, boardWidth, horizontalSplits);
		
		
		
		
		Integer[] rowsSplits = new Integer[rows.size()];
		rowsSplits = rows.toArray(rowsSplits);
		Integer[] colsSplit = new Integer[cols.size()];
		colsSplit = cols.toArray(colsSplit);
		
		topLeft = new Coordinate(rowsSplits[inputI], colsSplit[inputJ]);
		
		if (inputI<verticalSplits-1)
		{
			miniBoardHeight  = rowsSplits[inputI +1] - rowsSplits[inputI];
		}
		else
		{
			miniBoardHeight = boardHeight - rowsSplits[inputI] ;
		}
		
		if (inputJ<horizontalSplits-1)
		{
			miniBoardWidth = colsSplit[inputJ +1] - colsSplit[inputJ];
		}
		else
		{
			miniBoardWidth = boardWidth - colsSplit[inputJ] ;
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
					conditionsMatrix.wait();
					//wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}	
	//	System.err.println(Thread.currentThread().getId() + " after wait" );
		initializeSafeZone();
		
		while(finished.size()<miniBoardHeight*miniBoardWidth)
		{
			processTile(getNextReadyTile());
		}	
		//System.err.println(Thread.currentThread().getId() + " end run" );
	}
	
	/**
	 * This function checks if the neighbors of this board processor finished their miniboard border initialization.
	 * @return if the board processor can start processing its mini board.
	 */
	boolean checkOnNeighbors()
	{
		
		
		int x = myConditionCoordinate.getX() + verticalSplits;
		int y = myConditionCoordinate.getY() + horizontalSplits;
		
		for (int i =(x -1) ; i < (x + 2) ; i++ )
		{
			for (int j =(y -1) ; j < (y + 2) ; j++ )
			{
				if (conditionsMatrix[i % verticalSplits][j % horizontalSplits] == false)
				{
					return false;
				}
			}
		}
		
		return true;
		
		
	}
	
	
	/**
	 * Initialize the borders of the miniboard.
	 */
	void initializeBorders()
	{
		Coordinate c;
		for (int i = topLeft.getX(); i<topLeft.getX()+miniBoardHeight;i++)
		{
			int j = topLeft.getY();
			board[i][j] = new Tile(i, j, inputBoard[i][j], boardHeight,  boardWidth, bordersNotReadyQueue);
			
			
			
			
			c = new Coordinate(i,j);
			if (!bordersReadyQueue.contains(c))
			{
				bordersReadyQueue.add(c);
				//System.err.println(Thread.currentThread().getId() + " initializing in border 1 " + i + " " + j );
			}
			
			
		
			j+=miniBoardWidth-1;
			board[i][j] = new Tile(i, j, inputBoard[i][j], boardHeight,  boardWidth, bordersNotReadyQueue);	
		
			
			c = new Coordinate(i,j);
			if (!bordersReadyQueue.contains(c))
			{
				bordersReadyQueue.add(c);
				//System.err.println(Thread.currentThread().getId() + " initializing in border 2" + i + " " + j );
			}
		}
		
		
		for (int j = topLeft.getY(); j<topLeft.getY()+miniBoardWidth;j++)
		{
			int i = topLeft.getX();
			board[i][j] = new Tile(i, j, inputBoard[i][j], boardHeight,  boardWidth, bordersNotReadyQueue);
			
			
			c = new Coordinate(i,j);
			if (!bordersReadyQueue.contains(c))
			{
				bordersReadyQueue.add(c);
				//System.err.println(Thread.currentThread().getId() + " initializing in border 3" + i + " " + j );
			}
			
			i+=miniBoardHeight-1;
			board[i][j] = new Tile(i, j, inputBoard[i][j], boardHeight,  boardWidth,bordersNotReadyQueue);	
			
			
			c = new Coordinate(i,j);
			if (!bordersReadyQueue.contains(c))
			{
				bordersReadyQueue.add(c);
				//System.err.println(Thread.currentThread().getId() + " initializing in border 4" + i + " " + j );
			}
		}
		
		
		synchronized (conditionsMatrix)
		{
			conditionsMatrix[myConditionCoordinate.getX()][myConditionCoordinate.getY()] = true;
			conditionsMatrix.notifyAll();
		}
		
		
	}
	
	/**
	 * Initializes the tiles which are not in the borders.
	 */
	void initializeSafeZone()
	{
		for (int i = topLeft.getX()+1; i < topLeft.getX()-1 + miniBoardHeight ; i++)
		{
			for (int j = topLeft.getY()+1; j < topLeft.getY()-1 + miniBoardWidth ; j++)
			{
				board[i][j] = new Tile(i, j, inputBoard[i][j], boardHeight,  boardWidth, bordersNotReadyQueue);	
				
				Coordinate c = new Coordinate(i,j);
				if (!readyQueue.contains(c))
				{
					readyQueue.add(new Coordinate(i,j));
					//System.err.println(Thread.currentThread().getId() + " initializing in safe zone " + i + " " + j );
				}
				
				
			}
		}
	}
	
	
	/**
	 * 
	 * @return The coordinate of the next ready tile.
	 */
	Coordinate getNextReadyTile()
	{
		//System.err.println(Thread.currentThread().getId() + " getting next ready tile in readyQueue " + readyQueue.size() );
		if (readyQueue.isEmpty()!= true)
		{
			return readyQueue.poll();
		}
		//System.err.println(Thread.currentThread().getId() + " getting next ready tile in bordersReadyQueue " + bordersReadyQueue.size() );
		if (bordersReadyQueue.isEmpty()!= true)
		{
			return bordersReadyQueue.poll();
		}
		
		
		
		
		
		Coordinate tmpCoordinate = null;
		synchronized(bordersNotReadyQueue)
		{
			while (tmpCoordinate == null)
			{
				
				for(Coordinate c : bordersNotReadyQueue)
				{
					Tile t = board[c.getX()][c.getY()];
					if (t==null)
					{
						//System.err.println("Tile is null in coordiante " + c.toString() + " board size is " + boardHeight + " X " + boardWidth);
					}
					
					
					
					boolean state;
					synchronized(t)
					{
						state = t.isReadyToProcess();
					}

					if (state ==true)
					{
						tmpCoordinate = c;
						break;
					}
					//refreshTile(t);
					
				}
				if (tmpCoordinate!= null)
				{
					bordersNotReadyQueue.remove(tmpCoordinate);	
					//System.err.println(Thread.currentThread().getId() + " outside the while loop");
					//return tmpCoordinate;
				}
				else
				{
					try {
						System.err.println(Thread.currentThread().getId() + " is waiting");
						bordersNotReadyQueue.wait();
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.err.println(Thread.currentThread().getId() + " back from wait");
			
			}
			/*
		//System.err.println(Thread.currentThread().getId() + " getting next ready tile in notReadyQueue " + notReadyQueue.size() );
		for(Coordinate coordinate : notReadyQueue)
		{
			if (board[coordinate.getX()][coordinate.getY()].isReadyToProcess()==true)
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
			
			//System.err.println(Thread.currentThread().getId() + " getting next ready tile in bordersNotReadyQueue " + bordersNotReadyQueue.size() );
		
			
			}
			
			*/
			
			
		}
		return tmpCoordinate;

		
		
	}
	
	/**
	 * Returns if a coordinate is on the border of a miniboard.
	 * @param coordinate - A Tile coordinate.
	 * @return If the tile is on the border of the miniboard.
	 */
	boolean isInBorder(Coordinate coordinate)
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
	boolean isOutOfMiniboard(Coordinate coordinate)
	{
		
		if ((coordinate.getX()>= topLeft.getX()) && (coordinate.getX() <= topLeft.getX() + miniBoardHeight -1) &&
			(coordinate.getY()>= topLeft.getY() )&& (coordinate.getY() <= topLeft.getY() + miniBoardWidth -1 ))
		{
			return false;
		}
		else
		{
			return true;
		}
		/*
		if (coordinate.getX() == (topLeft.getX() -1 + boardHeight) % boardHeight ||
			coordinate.getX() == (topLeft.getX()+miniBoardHeight + boardHeight) % boardHeight||
			coordinate.getY() == (topLeft.getY()-1 + boardWidth) % boardWidth ||
			coordinate.getY() == (topLeft.getY()+miniBoardWidth + boardWidth) % boardWidth)
			return true;
		
		return false;
		*/
	}
	
	
	
	
	/**
	 * This functions checks if the tile should be dead or alive in the next generation.
	 * <p>
	 * If the current generation is in the requested generations for results than the Tile's status will be written in the results.
	 * <p>
	 * If the Tile current generation is maximum generation than the tile will be moved to finished and will not be processed anymore.
	 * @param coordinate - The coordinate of tile to process
	 */
	void processTile(Coordinate coordinate)
	{
		
		
		
		Tile tile = board[coordinate.getX()][coordinate.getY()];
		/*
		if (tile.isReadyToProcess()==false)
		{
			//System.err.println(Thread.currentThread().getId() + " adde tile " + coordinate.toString() +   " to not ready from proces tile"  );
			makeNotReady(coordinate);
			return;
		}
		*/
		
		activateGameLogic(tile);
		
		
	
		
		
		
		for (Coordinate neighborCoordinate: tile.getNeighborsCoordinate())
		{
			updateAgeAtNeighbor(tile.getCoordinate(), neighborCoordinate, tile.getAge());
		}
		
				
		if (tile.getAge()==generations)
		{
			results[0][coordinate.getX()][coordinate.getY()] = tile.getPreviousState();
			results[1][coordinate.getX()][coordinate.getY()] = tile.getState();
			finished.add(tile.getCoordinate());
			//while(true)
			//System.err.println(Thread.currentThread().getId() + " finished  "  + tile.getCoordinate().toString() + " Age " + tile.getAge() );
		}
		else
		{
			//System.err.println(Thread.currentThread().getId() + " moving tile " + tile.getCoordinate().toString() + " Age " + tile.getAge() +  " to not ready"  );
			makeNotReady(tile.getCoordinate());
			
		}	
	}
	
	
	
	
	
	void activateGameLogic(Tile tile) 
	{
		
		Coordinate tileCoordinate = tile.getCoordinate();
		//System.err.println("calculation for tile  " + tile.getCoordinate().toString() + " state = " + tile.getState());
		
		int counter=0;
		
		if (tile.getState() == true)
		{
			counter = -1;
		}
		for (int i=(tileCoordinate.getX()-1 + boardHeight); i<(tileCoordinate.getX()+2 + boardHeight); ++i){
			for (int j=(tileCoordinate.getY()-1 + boardWidth); j<(tileCoordinate.getY()+2 + boardWidth); j++) {
				
				int x = i% boardHeight;
				int y = j % boardWidth;
				
				
				/*
				if (x==tileCoordinate.getX() && y == tileCoordinate.getY())
				{
					continue;
				}
				*/
				
				Coordinate neighborCoordinate = new Coordinate(x,y);
				boolean state = getTileState(neighborCoordinate, tile.getAge());
			//	System.err.println("in neighbor " + neighborCoordinate.toString() + "state = " + state);
				if (state == true)
				{
					counter++;
				}
				
				//counter+=(field[i%field.length][j%field[0].length]?1:0);
			}
		}
		/*
		int counter = 0;
		
		System.err.println("calculation for tile  " + tile.getCoordinate().toString());
		for (Coordinate neighborCoordinate: tile.getNeighborsCoordinate())
		{
			
			
			boolean state = getTileState(neighborCoordinate, tile.getAge());
			System.err.println("in neighbor " + neighborCoordinate.toString() + "state = " + state);
			if (state == true)
			{
				counter++;
			}
			
		}
		*/
		//System.err.println("setting tile state, counter = " + counter);
		if (tile.getState() == false && counter == 3)
		{
			//System.err.println("to true");
			setTileState(tile, true);
		}
		else 
			if (tile.getState() == true && (counter == 3 || counter == 2 ))
			{
				//System.err.println("to true");
				setTileState(tile, true);
			}
			else
			{
				//System.err.println("to false");
				setTileState(tile, false);
			}
		
	}

	/**
	 * 
	 * @param coordinate - The Tile's coordinate.
	 * @param age - The Tile's requsted age.
	 * @return The Tile's state in the given age.
	 */
	boolean getTileState(Coordinate coordinate, int age)
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
	void setTileState(Tile tile, boolean state)
	{
		if (isInBorder(tile.getCoordinate()))
		{
			synchronized (tile)
			{
				tile.setState(state);
				tile.increaseAge();
			}
		}
		else
		{
			tile.setState(state);
			tile.increaseAge();
		}
	}
	
	
	/**
	 * Updates the processed tile's age at its neighbor.
	 * @param currentTileCoordinate - The processed tile coordinate.
	 * @param neighborCoordinate - The neighbor to update.
	 * @param currentTileAge - The age of the processed tile's age.
	 */
	void updateAgeAtNeighbor(Coordinate currentTileCoordinate, Coordinate neighborCoordinate, int currentTileAge)
	{
		Tile neighbor = board[neighborCoordinate.getX()][neighborCoordinate.getY()];
		if (isOutOfMiniboard(neighborCoordinate))
		{
			synchronized(neighbor.bordersNotReadyQueue)
			{
				synchronized ( neighbor)
				{
					 neighbor.updateNeighborAge(currentTileCoordinate, currentTileAge);			
					 
				}
				neighbor.bordersNotReadyQueue.notifyAll();
				
			}
			return;
			
		}
		if (isInBorder(neighborCoordinate))
		{
			boolean isReady;
			synchronized ( neighbor)
			{
				 neighbor.updateNeighborAge(currentTileCoordinate, currentTileAge);
				 isReady = neighbor.isReadyToProcess();
				
			}
			
			if (!neighborCoordinate.equals(currentTileCoordinate))
			{
				 if (isReady == true)
				 {
					 makeReady(neighborCoordinate);
				 }
				 else
				 {
					 makeNotReady(neighborCoordinate);
				 }
			}
			
		}
		else
		{
			boolean isReady;
			neighbor.updateNeighborAge(currentTileCoordinate, currentTileAge);
			isReady = neighbor.isReadyToProcess();
			if (!neighborCoordinate.equals(currentTileCoordinate))
			{
				if (isReady == true)
				{
					 makeReady(neighborCoordinate);
				}
				 else
				 {
					 makeNotReady(neighborCoordinate);
				 }
			}
			
			
		}
	}
	
	
	
	/**
	 * Moves the tile coordinate from a "not ready queue" to a "ready queue".
	 * @param readyTileCoordinate - The coordinate of the ready tile.
	 */
	void makeReady(Coordinate readyTileCoordinate)
	{
		if (isInBorder(readyTileCoordinate))
		{
			if (bordersNotReadyQueue.contains(readyTileCoordinate))
			{
				bordersNotReadyQueue.remove(readyTileCoordinate);
				bordersReadyQueue.add(readyTileCoordinate);
				//System.err.println("adding coordinate" + readyTileCoordinate.toString() + " to bordersReadyQueue after it was found in not ready" );
			}
			else
			{
				if (!bordersReadyQueue.contains(readyTileCoordinate))
				{
					bordersReadyQueue.add(readyTileCoordinate);
					//System.err.println("adding coordinate" + readyTileCoordinate.toString() + " to bordersReadyQueue after it wasnt found in not ready" );
				}
			}
		}
		else
		{

			if (notReadyQueue.contains(readyTileCoordinate))
			{
				notReadyQueue.remove(readyTileCoordinate);
				readyQueue.add(readyTileCoordinate);
				//System.err.println("adding coordinate" + readyTileCoordinate.toString() + " to readyQueue after it was found in not ready" );
			}
			else
			{
				if (!readyQueue.contains(readyTileCoordinate))
				{
					readyQueue.add(readyTileCoordinate);
					//System.err.println("adding coordinate" + readyTileCoordinate.toString() + " to readyQueue after it wasnt found in not ready" );
				}
			}
		}
	}
	
	
	/**
	 * This function enqueue a not ready tile in the not ready queue, by its classification (border or not border).
	 * @param notReadyTileCoordinate - The no ready Tile's coordinate.
	 */
	void makeNotReady(Coordinate notReadyTileCoordinate)
	{
		if (isInBorder(notReadyTileCoordinate))
		{
			if (!bordersNotReadyQueue.contains(notReadyTileCoordinate))
			{
				bordersNotReadyQueue.add(notReadyTileCoordinate);
				//System.err.println(Thread.currentThread().getId() + " adde tile " + notReadyTileCoordinate.toString() +   " to not ready. size of queue " + bordersNotReadyQueue.size() );
				
				if (! bordersNotReadyQueue.contains(notReadyTileCoordinate))
				{
				//	System.err.println(Thread.currentThread().getId() + " problem with insertion" );
				}
			}
			if (bordersReadyQueue.contains(notReadyTileCoordinate))
			{
				bordersReadyQueue.remove(notReadyTileCoordinate);
			}
		}
		else
		{

			if (!notReadyQueue.contains(notReadyTileCoordinate))
			{
				notReadyQueue.add(notReadyTileCoordinate);

			}
			if (readyQueue.contains(notReadyTileCoordinate))
			{
				readyQueue.remove(notReadyTileCoordinate);
			}
		}
	}
	
	/**
	 * This function will return all the indices which represents margins of mini boards on the segment.
	 * @param left - The left most index of the segment.
	 * @param right - The right most index of the segment.
	 * @return A sorted set of indices which represents the segment's partition.
	 */
	TreeSet<Integer> getPartitions(int left, int right , int splits)
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
	
	
	
	/**
	 * 
	 * @return The miniboard height.
	 */
	int getMiniboardHeight()
	{
		return miniBoardHeight;
	}
	
	
	/**
	 * 
	 * @return  The miniboard width.
	 */
	int getMiniboardWidth()
	{
		return miniBoardWidth;
	}
	
	
	void refreshTile(Tile t)
	{
		for (Coordinate c : t.neighborsAgesMap.keySet())
		{
			Tile neighbor = board[c.getX()][c.getY()];
			int nAge =0;
			synchronized(neighbor)
			{
				nAge = neighbor.getAge();
			}
			
			//System.err.println("now updating tile " + t.getCoordinate().toString() + " tile age " + t.getAge() + " with neighbor " + c.toString() + " with age " + nAge);
			t.neighborsAgesMap.put(c, nAge);
		}
		
	}
	
	
	
	

}
