package ex1;

import static org.junit.Assert.*;

import org.junit.Test;

public class CoordinateTest {

	
	@Test
	public void testCoordinate() {
		Coordinate c1 = null;
		c1 = new Coordinate(1,2);
		assertNotNull(c1);
		assertTrue(c1 instanceof Coordinate);
	}
	
	@Test
	public void testHashCode() {
		Coordinate c1 = new Coordinate(1, 1);
		Coordinate c1_1 = new Coordinate(1, 1);
		
		
		assertEquals(c1.hashCode(), c1_1.hashCode());
		
		Coordinate c2 = new Coordinate (0,1);
		Coordinate c3 = new Coordinate (1,0);
		
		assertNotEquals(c2.hashCode(), c1 .hashCode());
		assertNotEquals(c3.hashCode(), c1 .hashCode());
		assertNotEquals(c3.hashCode(), c2 .hashCode());

	}

	

	@Test
	public void testGetX() {
		Coordinate c1 = new Coordinate(1, 2);
		assertEquals(c1.getX(), 1);
		assertNotEquals(c1.getX(), 2);
		
	}

	@Test
	public void testGetY() {
		Coordinate c1 = new Coordinate(1, 2);
		assertEquals(c1.getY(), 2);
		assertNotEquals(c1.getY(), 1);
	}

	@Test
	public void testToString() {
		Coordinate c1 = new Coordinate(1, 2);
		assertEquals(c1.toString(), "(1, 2)");
		assertNotEquals(c1.toString(), "(2, 1");
	}

	@Test
	public void testEqualsObject() {
		Coordinate c1 = new Coordinate(1, 1);
		Coordinate c1_1 = new Coordinate(1, 1);
		
		assertEquals(c1, c1);
		assertEquals(c1, c1_1);
		
		Coordinate c2 = new Coordinate (0,1);
		Coordinate c3 = new Coordinate (1,0);
		
		assertNotEquals(c2, c1);
		assertNotEquals(c3, c1);
		assertNotEquals(c3, c2);
		assertNotEquals(c1, null);
		

	}

}
