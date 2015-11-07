package ex1;

import java.util.TreeSet;

/**
 * 
 * @author Dvir Dukhan
 *
 */

public class ParallelGameOfLife implements GameOfLife {
	
	
	/**
	 * Represents the board's height.
	 */
	int boardHeight;
	
	
	/**
	 * Represents the number of vertical splits.
	 */
	int verticalSplits;
		
	
	/**
	 * Represents the board's width.
	 */
	int boardWidth;
	
	/**
	 * Represents the number of horizontal splits
	 */
	int horizontalSplit;
	
	
	/**
	 * Results board for maximum generation states.
	 */
	boolean[][] maxGenBoard;
	
	/**
	 * Results board for maximum generation -1 states.
	 */
	boolean[][] maxGenMinusOneBoard;
	
	/**
	 * The actual game board.
	 */
	Tile[][] board;
	
	
	/**
	 * The input board.
	 */
	boolean[][] inputBoard;
	
	/**
	 * The board processors matrix.
	 */
	BoardProcessor[][] boardProcessors;
	
	/**
	 * The threads matrix.
	 */
	Thread[][] threads;
	
	
	public boolean[][][] invoke(boolean[][] initalField, int hSplit, int vSplit,
			int generations)
	{
		
		
		int columns=initalField[0].length;
		int rows=initalField.length;
		boolean[][][] results = new boolean[2][rows][columns];
		
		
		//Does a Worker finish his initialization phase.
		boolean[][] goodToGoMatrix = new boolean[vSplit][hSplit];
		//Arrays.fill(workerState, false);


		//Shared board of Cells [will be initialized in parallel by Workers]
		board = new Tile[rows][columns];

		//Create Workers
		boardProcessors  = new BoardProcessor[vSplit][hSplit];
		for (int i=0; i < vSplit; i++)
		{
			for (int j=0; j < hSplit; j++)
			{
				boardProcessors[i][j] = new BoardProcessor(i, j, goodToGoMatrix, board, initalField, results, hSplit, vSplit, generations);
				threads[i][j] = new Thread(boardProcessors[i][j]);
				threads[i][j].start();
			}	
		}
		
		//Wait for worker threads to finish
		for (int i=0; i < vSplit; i++)
		{
			for (int j=0; j < hSplit; j++)
			{
				try {
					threads[i][j].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}

		return results;
		
		
		
		/*
		boardHeight = initalField.length;
		boardHeight = initalField[0].length;
		maxGenBoard = new boolean[boardHeight][boardWidth];
		maxGenMinusOneBoard = new boolean[boardHeight][boardWidth];
		board = new Tile[boardHeight][boardWidth];
		inputBoard = initalField;
		
		bordersInitialize();
		
		//prepare data for spawn
		//TEST
		
		
		//spawn
		
		
		
		
		//retrun something
		return null;
		*/
	}
	
	/**
	 * This function will create the borders of the board, since parallel initialization is risky due to null objects
	 */
	public void bordersInitialize()
	{
		TreeSet<Integer> rows = getPartitions(0, boardHeight-1, verticalSplits);
		TreeSet<Integer> cols =  getPartitions(0, boardWidth-1, horizontalSplit);
		
		/*
		 *initialize each row of the borders and the row above it (cyclic). 
		 *this is because we are giving the parallel part the top left corner of a zone.
		 */

		for (Integer i: rows)
		{
			for (int j = 0; j < boardWidth; j++ )
			{
				int x = (i + boardHeight) % boardHeight;
				int y = j;
				if (board[x][y]==null)
				{
					board[x][y] = new Tile(x, y, inputBoard[x][y], boardHeight, boardWidth);
				}
				
				x = (i - 1 + boardHeight) % boardHeight;
				if (board[x][y]==null)
				{
					board[x][y] = new Tile(x, y, inputBoard[x][y], boardHeight, boardWidth);
				}
			}
		}
		
		/*
		 * initialize each column of the boarders and the column to its left (cyclic).
		 * this is because we are giving the parallel part the top left corner of a zone.
		 */
		
		
		for (Integer j: cols)
		{
			for(int i = 0; i<boardHeight; i++ )
			{
				int x = i;
				int y = (j + boardWidth) % boardWidth;
				if (board[x][y]==null)
				{
					board[x][y] = new Tile(x, y, inputBoard[x][y], boardHeight, boardWidth);
				}
				y = (j -1 + boardWidth) % boardWidth;
				if (board[x][y]==null)
				{
					board[x][y] = new Tile(x, y, inputBoard[x][y], boardHeight, boardWidth);
				}
				 
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
