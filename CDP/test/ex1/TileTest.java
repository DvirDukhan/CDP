package ex1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TileTest {
	
	HashMap<Coordinate, ArrayList<Coordinate>> coordinateMap = new HashMap<Coordinate, ArrayList<Coordinate>>();
	
	
	
	@Before
	public void initCoordinatesMap()
	{
		for (int i =0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				ArrayList<Coordinate> coordinates = initCoordinates();
				Coordinate c = new Coordinate(i, j);
				//coordinates.remove(c);
				coordinateMap.put(c, coordinates);
			}
		}
	}
	
	
	
	public ArrayList<Coordinate> initCoordinates()
	{
		ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
		for (int i =0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				Coordinate c = new Coordinate(i, j);
				coordinates.add(c);
			}
		}
		return coordinates;
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
		
		for (int i =0; i <3; i++)
		{
			for (int j =0; j<3; j++)
			{
				Tile tile = new Tile (i,j,true,3,3);
				Set<Coordinate> neighbors = tile.getNeighborsCoordinate();
				ArrayList<Coordinate> coordinates = coordinateMap.get(tile.getCoordinate());
				assertEquals(coordinates.size(),neighbors.size());
				for(Coordinate c : coordinates)
				{
					assertTrue(neighbors.contains(c));
				}
			}
		}		
		
	}
	

	
	
	
	@Test
	public void testIsReadyToProcess() {

		for (int i =0; i <3; i++)
		{
			for (int j =0; j<3; j++)
			{
				Tile tile = new Tile (i,j,true,3,3);
				ArrayList<Coordinate> coordinates = coordinateMap.get(tile.getCoordinate());
				
				assertTrue(tile.isReadyToProcess());
				tile.increaseAge();
				assertFalse(tile.isReadyToProcess());
				for (int k =0; k< coordinates.size(); k++)
				{
					tile.updateNeighborAge(coordinates.get(k), tile.getAge());
					if (k==coordinates.size()-1)
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
		
		
		
		
		
		
		
	
						
	}
	
	
	
	

	


	


}
