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
		threads = new Thread[vSplit][hSplit];
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
	

	


}
