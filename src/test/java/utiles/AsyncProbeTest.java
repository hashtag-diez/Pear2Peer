package test.java.utiles;

import static org.junit.Assert.*;

import org.junit.Test;

import main.java.utiles.AsyncCollector;

public class AsyncProbeTest {
	@Test
	public void testInvalidProbe() {
		try {
			new AsyncCollector(-1);
			fail("Should have thrown an exception");
		} catch (IllegalArgumentException e) {
			// OK
		}

	}

	@Test
	public void testProbe() {
		AsyncCollector dummyProbe = new AsyncCollector(0);
		assertTrue(dummyProbe.isComplete());
	}

	@Test
	public void testProbe2() {
		AsyncCollector dummyProbe = new AsyncCollector(1);
		assertFalse(dummyProbe.isComplete());
	}

	@Test
	public void testProbe3() {
		AsyncCollector dummyProbe = new AsyncCollector(1);
		dummyProbe.retrieve(null);
		assertTrue(dummyProbe.isComplete());
	}

	@Test
	public void testProbe4() {
		AsyncCollector dummyProbe = new AsyncCollector(3);
		dummyProbe.retrieve(null);
		assertFalse(dummyProbe.isComplete());
		for (int i = 0; i < 2; i++)
			dummyProbe.retrieve(null);
		assertTrue(dummyProbe.isComplete());
		try {
			dummyProbe.retrieve(null);
			fail("Should have thrown an exception");
		} catch (IllegalStateException e) {
			// OK
		}

	}
}
