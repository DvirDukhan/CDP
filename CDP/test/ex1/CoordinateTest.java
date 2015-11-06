package ex1;

import static org.junit.Assert.*;

import org.junit.Test;

public class CoordinateTest {

	
	@Test
	public void testCoordinate() {
		Coordinate c1 = new Coordinate(1,2);
		assertNotNull(c1);
	}
	
	@Test
	public void testHashCode() {
		Coordinate c1 = new Coordinate(1, 1);
		Coordinate c1_1 = new Coordinate(1, 1);
		
		
		
		assertEquals(c1.hashCode(), c1_1.hashCode());

	}

	

	@Test
	public void testGetX() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetY() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testEqualsObject() {
		fail("Not yet implemented");
	}

}
