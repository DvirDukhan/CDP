package ex1;

import static org.junit.Assert.*;

import org.junit.Test;

public class ParallelGameOfLifeTest {


	boolean[][] goodToGoMatrix;
	boolean[][] initialField;
	
	@Test
	public void testInvoke() {
		for (int rows = 1; rows < 10; rows ++)
		{
			for (int cols = 1; cols < 10; cols ++)
			{
							
				for (int vsplits = 1; vsplits <= rows; vsplits++)
				{
					for (int hsplits = 1; hsplits <= cols; hsplits ++)
					{
						
						for (int generations =1; generations <= 10; generations++)
						{
							initBoardWithData(rows, cols);
							
							GameOfLife sGol=new SerialGameOfLife();
							GameOfLife pGol=new ParallelGameOfLife();
							
							boolean[][][] resultSerial=sGol.invoke(initialField,hsplits,vsplits, generations);	
							boolean[][][] resultParallel=pGol.invoke(initialField,hsplits,vsplits, generations);
							
							assertTrue(Ex1.compareArrays(resultParallel[0],resultSerial[0]) && Ex1.compareArrays(resultParallel[1],resultSerial[1]));
							System.out.println("ok for generation " + generations + ", rows " + rows + ", cols " + cols + ", hsplit " + hsplits + ", vsplit " + vsplits);
						}
						
																				
					}
				}
				
			}
		}
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
		
	}

}
