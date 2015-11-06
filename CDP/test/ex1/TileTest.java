package ex1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TileTest {
	
	
	ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
	
	@Before
	public void initCoordinates()
	{
		for (int i =0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				if (i==1 && j==1)
				{
					continue;
				}
				Coordinate c = new Coordinate(i, j);
				coordinates.add(c);
			}
		}
	}

	@Test
	public void testTile() {
		
		Tile tile = null;
		tile = new Tile (0,0,true,1,1);
		assertNotNull(tile);
		assertTrue(tile instanceof Tile);

	}

	@Test
	public void testGetCoordinate() {
		Tile tile = new Tile (0,0,true,1,1);
		Coordinate c = new Coordinate(0,0);
		Coordinate b1 = new Coordinate(1,1);
		
		assertEquals(tile.getCoordinate(), c);
		assertNotEquals(tile.getCoordinate(), b1);
		
	}

	@Test
	public void testGetState() {

		Tile tile = new Tile (0,0,true,1,1);
		assertTrue(tile.getState());
		Tile tile2 = new Tile (0,0,false,1,1);
		assertFalse(tile2.getState());
		
		tile.setState(false);
		tile.increaseAge();
		assertFalse(tile.getState());
		
	}

	@Test
	public void testGetPreviousState() {
		Tile tile = new Tile (0,0,true,1,1);		
		
		tile.setState(false);
		tile.increaseAge();
		assertFalse(tile.getState());
		assertTrue(tile.getPreviousState());
	}

	@Test
	public void testGetStateInt() {
		Tile tile = new Tile (0,0,true,1,1);		
		
		tile.setState(false);
		tile.increaseAge();
		assertTrue(tile.getState(0));
		assertFalse(tile.getState(1));
	}

	@Test
	public void testGetAge() {
		Tile tile = new Tile (0,0,true,1,1);	
		assertEquals(tile.getAge(), 0);
		tile.increaseAge();
		assertEquals(tile.getAge(), 1);
		
	}
	
	
	@Test
	public void testGetNeighborsCoordinate() {
			
		Tile tile = new Tile (1,1,true,3,3);
		Set<Coordinate> neighbors = tile.getNeighborsCoordinate();
		assertEquals(coordinates.size(),neighbors.size());
		for(Coordinate c : coordinates)
		{
			assertTrue(neighbors.contains(c));
		}
		
	}
	

	
	
	
	@Test
	public void testIsReadyToProcess() {
		Tile tile = new Tile (1,1,true,3,3);
		Set<Coordinate> neighbors = tile.getNeighborsCoordinate();
		
		tile.increaseAge();
		assertFalse(tile.isReadyToProcess());
		
		for (int i =0; i< coordinates.size(); i++)
		{
			tile.updateNeighborAge(coordinates.get(i), tile.getAge());
			if (i==coordinates.size()-1)
			{
				assertTrue(tile.isReadyToProcess());
			}
			else
			{
				assertFalse(tile.isReadyToProcess());
			}
			
		}
						
	}
	
	
	
	

	


	


}
