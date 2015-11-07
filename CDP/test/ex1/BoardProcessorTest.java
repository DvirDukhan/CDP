package ex1;

import static org.junit.Assert.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;

public class BoardProcessorTest {
	
	boolean[][] goodToGoMatrix;
	boolean[][] initialField;
	Tile[][] board;
	boolean[][][] results; 
	
	
	boolean[][] initiailFeild1 = new boolean[][]{ {false, false, false}, 
													 {false, true, false}, 
													 {false, false, false}};
													 
	boolean[][] finalFeild1 = new boolean[][]{ {false, false, false}, 
										       {false, false, false}, 
										       {false, false, false}};	
										       
	boolean[][] initialFeild2 = new boolean[][]{ {true, true, true}, 
		 										 {false, false, false}, 
		 										 {false, false, false}};
	
    boolean[][] finalFeild2 = new boolean[][]{ {true, true, true}, 
			 										 {true, true, true}, 
			 										 {true, true, true,}};
	
	
	@Before
	public void initTest()
	{
	}
	
	
	
	
	void initGoodToGoMatrix(int rows, int cols)
	{
		goodToGoMatrix = new boolean[rows][cols];
		for (int i=0; i< rows; i++)
		{
			for (int j = 0; j< cols; j++)
			{
				goodToGoMatrix[i][j] = false;
			}
		}
	}
	
	void initBoard(int rows, int cols)
	{
		initialField = new boolean[rows][cols];
		board = new Tile[rows][cols];
		results = new boolean[2][rows][cols];
	}
	

	void initBoardWithData(int rows, int cols)
	{
		initialField = new boolean[rows][cols];
		for (int i =0; i < rows; i++)
		{
			for (int j=0; j<cols; j++)
			{
				if ((i+j)%2 ==0)
				{
					initialField[i][j] = true;
				}
				else
				{
					initialField[i][j] = false;
				}
			}
		}
		
		board = new Tile[rows][cols];
		results = new boolean[2][rows][cols];
	}

	@Test
	public void testBoardProcessor() {
		initGoodToGoMatrix(3,3);
		initBoard(1, 1);
		BoardProcessor bp = null;
		bp = new BoardProcessor(0, 0, goodToGoMatrix, board, initialField, results, 1, 1, 1);
		assertNotNull(bp);
		assertTrue(bp instanceof BoardProcessor);
	}

	

	@Test
	public void testCheckOnNeighbors() {
		
		
		for (int i = 0; i < 3 ; i++)
		{
			for (int j=0; j <3 ; j++)
			{
				initGoodToGoMatrix(3,3);
				initBoard(3, 3);
				BoardProcessor bp = new BoardProcessor(i, j, goodToGoMatrix, board, initialField, results, 3, 3, 1);
				
				for (int m = 0; m <3; m++)
				{
					for (int n = 0; n < 3; n++)
					{
						assertFalse(bp.checkOnNeighbors());
						goodToGoMatrix[m][n] = true;
					}
				}
				assertTrue(bp.checkOnNeighbors());								
			}
		}
		
		
	
		
	}

	@Test
	public void testInitializeBorders() {
		
		
		for (int rows = 1; rows < 10; rows ++)
		{
			for (int cols = 1; cols < 10; cols ++)
			{
				initGoodToGoMatrix(1,1);
				initBoard(rows, cols);
				BoardProcessor bp = new BoardProcessor(0, 0, goodToGoMatrix, board, initialField, results, 1, 1, 1);
				for (int i = 0 ; i < rows; i++)
				{
					for (int j = 0; j < cols; j++)
					{
						assertNull(board[i][j]);
					}
				}
				bp.initializeBorders();
				
				for (int i = 0 ; i < rows; i++)
				{
					for (int j = 0; j < cols; j++)
					{
						if (i==0 || i == bp.getMiniboardHeight()-1)
						{
							assertNotNull("for i = " + i + " j = " + j, board[i][j]);
						}
						else
						if (j==0 || j == bp.getMiniboardWidth()-1)
						{
							assertNotNull(board[i][j]);
						}
						else
							assertNull(board[i][j]);
						
					}
				}
				
				
				
				for (int vsplits = 1; vsplits <= rows; vsplits++)
				{
					for (int hsplits = 1; hsplits <= cols; hsplits ++)
					{
						initBoard(rows, cols);
					
						for (int i = 0 ; i < rows; i++)
						{
							for (int j = 0; j < cols; j++)
							{
								assertNull(board[i][j]);
							}
						}
						initGoodToGoMatrix(vsplits,hsplits);
						
						for (int m = 0; m < vsplits; m ++)
						{
							for (int n = 0; n < hsplits; n++)
							{
							
								bp = new BoardProcessor(m, n, goodToGoMatrix, board, initialField, results,  hsplits, vsplits, 1);
								bp.initializeBorders();
								assertNotEquals(0, bp.miniBoardHeight);
								assertNotEquals(0, bp.miniBoardWidth);
								for (int i = bp.topLeft.getX(); i<bp.topLeft.getX()+bp.miniBoardHeight;i++)
								{
									for (int j = bp.topLeft.getY(); j<bp.topLeft.getY()+bp.miniBoardWidth;j++)
									{
										if (i==bp.topLeft.getX() || i == bp.topLeft.getX()+bp.miniBoardHeight-1)
										{
											assertNotNull("for i = " + i + " j = " + j, board[i][j]);
										}
										else
										if (j == bp.topLeft.getY() || j == bp.topLeft.getY()+bp.miniBoardWidth-1)
										{
											assertNotNull( "for i = " + i + " j = " + j, board[i][j]);
										}
										else
											assertNull("for i = " + i + " j = " + j, board[i][j]);
									}
								}		
							}
						}
																					
					}
					
				}
				
			}
		}		
	}

	@Test
	public void testInitializeSafeZone() {

		for (int rows = 1; rows < 10; rows ++)
		{
			for (int cols = 1; cols < 10; cols ++)
			{
				initGoodToGoMatrix(1,1);
				initBoard(rows, cols);
				BoardProcessor bp = new BoardProcessor(0, 0, goodToGoMatrix, board, initialField, results, 1, 1, 1);
				for (int i = 0 ; i < rows; i++)
				{
					for (int j = 0; j < cols; j++)
					{
						assertNull(board[i][j]);
					}
				}
				bp.initializeSafeZone();;

				for (int i = 0 ; i < rows; i++)
				{
					for (int j = 0; j < cols; j++)
					{
						if (i==0 || i == bp.getMiniboardHeight()-1)
						{
							assertNull("for i = " + i + " j = " + j, board[i][j]);
						}
						else
						if (j==0 || j == bp.getMiniboardWidth()-1)
						{
							assertNull(board[i][j]);
						}
						else
							assertNotNull(board[i][j]);
						
					}
				}
				
				
				
				for (int vsplits = 1; vsplits <= rows; vsplits++)
				{
					for (int hsplits = 1; hsplits <= cols; hsplits ++)
					{
						initBoard(rows, cols);
						for (int i = 0 ; i < rows; i++)
						{
							for (int j = 0; j < cols; j++)
							{
								assertNull(board[i][j]);
							}
						}
						initGoodToGoMatrix(vsplits,hsplits);
						
						for (int m = 0; m < vsplits; m ++)
						{
							for (int n = 0; n < hsplits; n++)
							{
								
								bp = new BoardProcessor(m, n, goodToGoMatrix, board, initialField, results, hsplits, vsplits, 1);
								bp.initializeSafeZone();
								assertNotEquals(0, bp.miniBoardHeight);
								assertNotEquals(0, bp.miniBoardWidth);
								for (int i = bp.topLeft.getX(); i<bp.topLeft.getX()+bp.miniBoardHeight;i++)
								{
									for (int j = bp.topLeft.getY(); j<bp.topLeft.getY()+bp.miniBoardWidth;j++)
									{
										if (i==bp.topLeft.getX() || i == bp.topLeft.getX()+bp.miniBoardHeight-1)
										{
											assertNull("for i = " + i + " j = " + j, board[i][j]);
										}
										else
										if (j == bp.topLeft.getY() || j == bp.topLeft.getY()+bp.miniBoardWidth-1)
										{
											assertNull( "for i = " + i + " j = " + j, board[i][j]);
										}
										else
											assertNotNull("for i = " + i + " j = " + j, board[i][j]);
									}
								}		
							}
						}
																					
					}
				}
				
			}
		}
	}
	
	@Test
	public void testBoardInit()
	{
		for (int rows = 1; rows < 10; rows ++)
		{
			for (int cols = 1; cols < 10; cols ++)
			{
							
				for (int vsplits = 1; vsplits <= rows; vsplits++)
				{
					for (int hsplits = 1; hsplits <= cols; hsplits ++)
					{
						initBoard(rows, cols);
						initGoodToGoMatrix(vsplits,hsplits);
						
						for (int m = 0; m < vsplits; m ++)
						{
							for (int n = 0; n < hsplits; n++)
							{
								
								BoardProcessor bp = new BoardProcessor(m, n, goodToGoMatrix, board, initialField, results,  hsplits, vsplits,1);
								bp.initializeBorders();
								bp.initializeSafeZone();
								assertNotEquals(0, bp.getMiniboardHeight());
								assertNotEquals(0, bp.getMiniboardWidth());
							}
						}
						
						for (int i=0; i< rows; i++)
						{
							for (int j =0 ;j < cols; j++ )
							{
								assertNotNull("in board [" + i + "][" + j + "]",board[i][j]);
								assertEquals(board[i][j].getCoordinate(), new Coordinate(i, j));
							}
						}
																					
					}
				}
				
			}
		}
	}

	@Test
	public void testGetNextReadyTile() {

		for (int rows = 1; rows < 10; rows ++)
		{
			for (int cols = 1; cols < 10; cols ++)
			{
				initGoodToGoMatrix(1, 1);
				initBoard(rows,cols);		
				
				BoardProcessor bp = new BoardProcessor(0, 0, goodToGoMatrix, board, initialField, results, 1, 1, 1);
				bp.initializeBorders();
				bp.initializeSafeZone();
				ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
				Coordinate c;
				
				for (int i = 1; i< rows-1; i++)
				{
					for (int j = 1; j< cols-1; j++)
					{
						c = new Coordinate(i,j);
						if (!coordinates.contains(c))
						{
							coordinates.add(c);
							assertEquals(c, bp.getNextReadyTile());
						}	
					}
				}
				
				
				for (int i = 0; i < rows; i++)
				{
					int j = 0;
					
					c = new Coordinate(i,j);
					if (!coordinates.contains(c))
					{
						coordinates.add(c);
						assertEquals(c, bp.getNextReadyTile());
					}	
					
					
					j+=cols-1;					
					c = new Coordinate(i,j);
					if (!coordinates.contains(c))
					{
						coordinates.add(c);
						assertEquals(c, bp.getNextReadyTile());
					}
					
				}
				
				for (int j = 0; j < cols; j++)
				{
					int i = 0;
					
					c = new Coordinate(i,j);
					if (!coordinates.contains(c))
					{
						coordinates.add(c);
						assertEquals(c, bp.getNextReadyTile());
					}	
					
					
					i+=rows-1;					
					c = new Coordinate(i,j);
					if (!coordinates.contains(c))
					{
						coordinates.add(c);
						assertEquals(c, bp.getNextReadyTile());
					}				
				}				
			}		
		}
	}

	@Test
	public void testIsInBorder() {
		
		
		for (int rows = 1; rows < 10; rows ++)
		{
			for (int cols = 1; cols < 10; cols ++)
			{
							
				for (int vsplits = 1; vsplits <= rows; vsplits++)
				{
					for (int hsplits = 1; hsplits <= cols; hsplits ++)
					{
						initBoard(rows, cols);
						initGoodToGoMatrix(vsplits,hsplits);
						
						for (int m = 0; m < vsplits; m ++)
						{
							for (int n = 0; n < hsplits; n++)
							{
								
								BoardProcessor bp = new BoardProcessor(m, n, goodToGoMatrix, board, initialField, results,  hsplits,vsplits, 1);
								bp.initializeBorders();
								bp.initializeSafeZone();
								Coordinate c;
								for (int i = bp.topLeft.getX(); i< bp.topLeft.getX()+ bp.getMiniboardHeight(); i++)
								{
									for (int j = bp.topLeft.getY(); j< bp.topLeft.getY()+ bp.getMiniboardWidth(); j++)
									{
										c = new Coordinate(i,j);
										if (i==bp.topLeft.getX() || i == bp.topLeft.getX()+bp.getMiniboardHeight() -1 || 
												j==bp.topLeft.getY() || j== bp.topLeft.getY()+ bp.getMiniboardWidth() -1)
										{
											assertTrue(bp.isInBorder(c));
										}
										else
										{
											assertFalse(bp.isInBorder(c));
										}
									}
								}
							}
						}
																					
					}
				}
				
			}
		}
	}

	@Test
	public void testIsOutOfMiniboard() {
		for (int rows = 3; rows < 10; rows ++)
		{
			for (int cols = 3; cols < 10; cols ++)
			{
							
				for (int vsplits = 1; vsplits <= rows; vsplits++)
				{
					for (int hsplits = 1; hsplits <= cols; hsplits ++)
					{
						initBoard(rows, cols);
						initGoodToGoMatrix(vsplits,hsplits);
						
						for (int m = 0; m < vsplits; m ++)
						{
							for (int n = 0; n < hsplits; n++)
							{
								
								BoardProcessor bp = new BoardProcessor(m, n, goodToGoMatrix, board, initialField, results,  hsplits, vsplits,1);
								bp.initializeBorders();
								bp.initializeSafeZone();
								Coordinate c;
								for (int i = bp.topLeft.getX(); i< bp.topLeft.getX()+ bp.getMiniboardHeight(); i++)
								{
									for (int j = bp.topLeft.getY(); j< bp.topLeft.getY()+ bp.getMiniboardWidth(); j++)
									{
										c = new Coordinate(i,j);
										assertFalse(bp.isOutOfMiniboard(c));
									}
								}
								
								if (hsplits ==1)
								{
									continue;
								}
								else
								{	
									for (int i = bp.topLeft.getX(); i< bp.topLeft.getX()+ bp.getMiniboardHeight(); i++)
									{
										int j = (bp.topLeft.getY() - 1  + bp.boardWidth) % bp.boardWidth;
										c = new Coordinate(i,j);
										assertTrue(bp.isOutOfMiniboard(c));
										
										 j = (bp.topLeft.getY() + bp.miniBoardWidth + bp.boardWidth) % bp.boardWidth;
										 c = new Coordinate(i,j);
										assertTrue(bp.isOutOfMiniboard(c));
									}
									
									
								
								}
								if (vsplits ==1)
								{
									continue;
								}
								else
								{
									for (int j = bp.topLeft.getY(); j< bp.topLeft.getY()+ bp.getMiniboardWidth(); j++)
									{
										
										int i = (bp.topLeft.getX() - 1  + bp.boardHeight) % bp.boardHeight;
										c = new Coordinate(i,j);
										assertTrue(bp.isOutOfMiniboard(c));
										
										 i = (bp.topLeft.getX() + bp.miniBoardHeight + bp.boardHeight) % bp.boardHeight;
										 c = new Coordinate(i,j);
										assertTrue(bp.isOutOfMiniboard(c));
										
									}
								}
							
								
								
							}
						}
																					
					}
				}
				
			}
		}
	}

	

	@Test
	public void testGetSetTileState() {
		for (int rows = 1; rows < 10; rows ++)
		{
			for (int cols = 1; cols < 10; cols ++)
			{
							
				for (int vsplits = 1; vsplits <= rows; vsplits++)
				{
					for (int hsplits = 1; hsplits <= cols; hsplits ++)
					{
						initBoard(rows, cols);
						initGoodToGoMatrix(vsplits,hsplits);
						
						for (int m = 0; m < vsplits; m ++)
						{
							for (int n = 0; n < hsplits; n++)
							{
								
								BoardProcessor bp = new BoardProcessor(m, n, goodToGoMatrix, board, initialField, results,  hsplits,vsplits, 1);
								bp.initializeBorders();
								bp.initializeSafeZone();
								Coordinate c;
								for (int i = bp.topLeft.getX(); i< bp.topLeft.getX()+ bp.getMiniboardHeight(); i++)
								{
									for (int j = bp.topLeft.getY(); j< bp.topLeft.getY()+ bp.getMiniboardWidth(); j++)
									{
										c = new Coordinate(i,j);
										assertFalse(bp.getTileState(c, 0));
										Tile t = board[i][j];
										bp.setTileState(t, true);
										assertTrue(bp.getTileState(c, 1));
										assertFalse(bp.getTileState(c, 0));																													
									}
								}
							}
						}
																					
					}
				}
				
			}
		}
	}


	@Test
	public void testUpdateAgeAtNeighbor() {
		for (int rows = 1; rows < 10; rows ++)
		{
			for (int cols = 1; cols < 10; cols ++)
			{
							
				for (int vsplits = 1; vsplits <= rows; vsplits++)
				{
					for (int hsplits = 1; hsplits <= cols; hsplits ++)
					{
						initBoard(rows, cols);
						initGoodToGoMatrix(vsplits,hsplits);
						
						for (int m = 0; m < vsplits; m ++)
						{
							for (int n = 0; n < hsplits; n++)
							{
								
								BoardProcessor bp = new BoardProcessor(m, n, goodToGoMatrix, board, initialField, results,  hsplits, vsplits, 1);
								bp.initializeBorders();
								bp.initializeSafeZone();								
							}
						}
						
						for (int m = 0; m < vsplits; m ++)
						{
							for (int n = 0; n < hsplits; n++)
							{
								
								BoardProcessor bp = new BoardProcessor(m, n, goodToGoMatrix, board, initialField, results,  hsplits, vsplits, 1);
								Coordinate c;
								for (int i = bp.topLeft.getX(); i< bp.topLeft.getX()+ bp.getMiniboardHeight(); i++)
								{
									for (int j = bp.topLeft.getY(); j< bp.topLeft.getY()+ bp.getMiniboardWidth(); j++)
									{
										c = new Coordinate(i,j);
										Tile tile = board[i][j];
										for (Coordinate neighborCoordinate: tile.getNeighborsCoordinate())
										{
											Tile neighbor = board[neighborCoordinate.getX()][neighborCoordinate.getY()];
											assertEquals((int)neighbor.neighborsAgesMap.get(c), tile.getAge());
											
											
										}
										bp.setTileState(tile, true);
										for (Coordinate neighborCoordinate: tile.getNeighborsCoordinate())
										{
											Tile neighbor = board[neighborCoordinate.getX()][neighborCoordinate.getY()];
											bp.updateAgeAtNeighbor(tile.getCoordinate(), neighborCoordinate, tile.getAge());
											assertEquals((int)neighbor.neighborsAgesMap.get(c), tile.getAge());
										}
																																						
									}
								}
							}
						}
																					
					}
				}
				
			}
		}
	}

	@Test
	public void testMakeReady() {
		for (int rows = 1; rows < 10; rows ++)
		{
			for (int cols = 1; cols < 10; cols ++)
			{
							
				for (int vsplits = 1; vsplits <= rows; vsplits++)
				{
					for (int hsplits = 1; hsplits <= cols; hsplits ++)
					{
						initBoard(rows, cols);
						initGoodToGoMatrix(vsplits,hsplits);
						
						for (int m = 0; m < vsplits; m ++)
						{
							for (int n = 0; n < hsplits; n++)
							{
								
								BoardProcessor bp = new BoardProcessor(m, n, goodToGoMatrix, board, initialField, results, hsplits,vsplits, 1);
								bp.initializeBorders();
								bp.initializeSafeZone();
								Queue<Coordinate> copyReadyQueue = ((ArrayDeque<Coordinate>)bp.readyQueue).clone();
								Queue<Coordinate> copyBorderReadyQueue = ((ArrayDeque<Coordinate>)bp.bordersReadyQueue).clone();
								
								assertEquals(copyReadyQueue.size(), bp.readyQueue.size());
								assertEquals(copyBorderReadyQueue.size(), bp.bordersReadyQueue.size());
								bp.notReadyQueue.addAll(bp.readyQueue);
								bp.bordersNotReadyQueue.addAll(bp.bordersReadyQueue);
								
								
								assertEquals(bp.notReadyQueue.size(), bp.readyQueue.size());
								assertEquals(bp.bordersNotReadyQueue.size(), bp.bordersReadyQueue.size());
								
								
								bp.readyQueue.clear();
								bp.bordersReadyQueue.clear();
								

								assertEquals(0, bp.readyQueue.size());
								assertEquals(0, bp.bordersReadyQueue.size());

								for (int i = bp.topLeft.getX(); i< bp.topLeft.getX()+ bp.getMiniboardHeight(); i++)
								{
									for (int j = bp.topLeft.getY(); j< bp.topLeft.getY()+ bp.getMiniboardWidth(); j++)
									{
										
										Tile tile = board[i][j];
										assertTrue(tile.isReadyToProcess());
										if (tile.isReadyToProcess())
										{
											bp.makeReady(tile.getCoordinate());
										}
																																						
									}
								}
								
								assertEquals(copyReadyQueue.size(), bp.readyQueue.size());
								assertEquals(copyBorderReadyQueue.size(), bp.bordersReadyQueue.size());
								
								
								for (Coordinate c : copyReadyQueue)
								{
									assertTrue(bp.readyQueue.contains(c));
								}
								for (Coordinate c: copyBorderReadyQueue)
								{
									assertTrue(bp.bordersReadyQueue.contains(c));
								}
								
							}
						}
																					
					}
				}
				
			}
		}
	}

	@Test
	public void testMakeNotReady() {
		for (int rows = 1; rows < 10; rows ++)
		{
			for (int cols = 1; cols < 10; cols ++)
			{
							
				for (int vsplits = 1; vsplits <= rows; vsplits++)
				{
					for (int hsplits = 1; hsplits <= cols; hsplits ++)
					{
						initBoard(rows, cols);
						initGoodToGoMatrix(vsplits,hsplits);
						
						for (int m = 0; m < vsplits; m ++)
						{
							for (int n = 0; n < hsplits; n++)
							{
								
								BoardProcessor bp = new BoardProcessor(m, n, goodToGoMatrix, board, initialField, results, hsplits,vsplits,  1);
								bp.initializeBorders();
								bp.initializeSafeZone();
								Queue<Coordinate> copyReadyQueue = ((ArrayDeque<Coordinate>)bp.readyQueue).clone();
								Queue<Coordinate> copyBorderReadyQueue = ((ArrayDeque<Coordinate>)bp.bordersReadyQueue).clone();
								
								assertEquals(copyReadyQueue.size(), bp.readyQueue.size());
								assertEquals(copyBorderReadyQueue.size(), bp.bordersReadyQueue.size());
								
								
								
								while (!bp.readyQueue.isEmpty())
								{
									Coordinate c = bp.getNextReadyTile();
									bp.makeNotReady(c);
								}
								
								assertEquals(0, bp.readyQueue.size());
								assertEquals(copyReadyQueue.size(), bp.notReadyQueue.size());
								
								while (!bp.bordersReadyQueue.isEmpty())
								{
									Coordinate c = bp.getNextReadyTile();
									bp.makeNotReady(c);
								}


								
								
								assertEquals(0, bp.bordersReadyQueue.size());
								
								assertEquals(copyBorderReadyQueue.size(), bp.bordersNotReadyQueue.size());
								
								
								for (Coordinate c : copyReadyQueue)
								{
									assertTrue(bp.notReadyQueue.contains(c));
								}
								for (Coordinate c: copyBorderReadyQueue)
								{
									assertTrue(bp.bordersNotReadyQueue.contains(c));
								}
								
							}
						}
																					
					}
				}
				
			}
		}
	}
	
	@Test
	public void testActivateGameLogic()
	{
		initGoodToGoMatrix(1, 1);
		initBoard(3,3);
		BoardProcessor bp = new BoardProcessor(0, 0, goodToGoMatrix, board, initialField, results, 1, 1, 1);
		bp.initializeBorders();
		bp.initializeSafeZone();
		while (bp.finished.size()!=9)
		{
			bp.processTile(bp.getNextReadyTile());
		}
		for (int i=0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				Tile tile = board[i][j];
				assertEquals(tile.getAge(), 1);
				assertFalse(tile.getState());
				assertFalse(tile.getPreviousState());
				
			}
		}
		
		
		initGoodToGoMatrix(1, 1);
		initBoard(3,3);
		for (int i=0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				initialField[i][j] = true;
				
			}
		}
		bp = new BoardProcessor(0, 0, goodToGoMatrix, board, initialField, results, 1, 1, 1);
		bp.initializeBorders();
		bp.initializeSafeZone();
		while (bp.finished.size()!=9)
		{
			bp.processTile(bp.getNextReadyTile());
		}
		for (int i=0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				Tile tile = board[i][j];
				assertEquals(tile.getAge(), 1);
				assertEquals(tile.getState(), false);
				assertEquals(tile.getPreviousState(), true);
					
				
				
			}
		}
		
		initGoodToGoMatrix(1, 1);
		initBoard(3,3);
		initialField = initiailFeild1;
		bp = new BoardProcessor(0, 0, goodToGoMatrix, board, initialField, results, 1, 1, 1);
		bp.initializeBorders();
		bp.initializeSafeZone();
		while (bp.finished.size()!=9)
		{
			bp.processTile(bp.getNextReadyTile());
		}
		for (int i=0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				Tile tile = board[i][j];
				assertEquals(tile.getAge(), 1);
				assertEquals(tile.getState(), finalFeild1[i][j]);
				assertEquals(tile.getPreviousState(), initiailFeild1[i][j]);
					
				
				
			}
		}
		
		initGoodToGoMatrix(1, 1);
		initBoard(3,3);
		initialField = initialFeild2;
		bp = new BoardProcessor(0, 0, goodToGoMatrix, board, initialField, results, 1, 1, 1);
		bp.initializeBorders();
		bp.initializeSafeZone();
		while (bp.finished.size()!=9)
		{
			bp.processTile(bp.getNextReadyTile());
		}
		for (int i=0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				Tile tile = board[i][j];
				assertEquals(tile.getAge(), 1);
				assertEquals(tile.getState(), finalFeild2[i][j]);
				assertEquals(tile.getPreviousState(), initialFeild2[i][j]);
					
				
				
			}
		}
		
		
		initGoodToGoMatrix(1, 1);
		initBoard(3,3);
		initialField = initialFeild2;
		bp = new BoardProcessor(0, 0, goodToGoMatrix, board, initialField, results, 1, 1, 2);
		bp.initializeBorders();
		bp.initializeSafeZone();
		while (bp.finished.size()!=9)
		{
			bp.processTile(bp.getNextReadyTile());
		}
		for (int i=0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				Tile tile = board[i][j];
				assertEquals(tile.getAge(), 2);
				assertEquals(tile.getState(), false);
				assertEquals(tile.getPreviousState(), true);
					
				
				
			}
		}
		
	}
	
	
	@Test
	public void testRun() {
		for (int rows = 1; rows < 10; rows ++)
		{
			for (int cols = 1; cols < 10; cols ++)
			{
				int generations;
				for (generations=1; generations <=9; generations ++)
				{
					
					initBoardWithData(rows, cols);
					initGoodToGoMatrix(1,1);
					System.err.println("new board " + " generations = " + generations);
					GameOfLife sGol=new SerialGameOfLife();
					
					boolean[][][] resultSerial=sGol.invoke(initialField,1,1, generations);

					BoardProcessor bp = new BoardProcessor(0, 0, goodToGoMatrix, board, initialField, results, 1, 1, generations);
					bp.run();
					
					
					
					for (int i=0; i<2; i++)
					{
						for (int j =0; j<rows; j++)
						{
							for (int k =0; k<cols; k++)
							{
								assertEquals(i + ", " + j + ", " + k, results[i][j][k],resultSerial[i][j][k] );
							}
						}
					}
					
					
					
				}
				
				
			}
		}
	}

}
