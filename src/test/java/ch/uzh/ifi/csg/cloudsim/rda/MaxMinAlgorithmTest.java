package ch.uzh.ifi.csg.cloudsim.rda;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Log;
import org.junit.Before;
import org.junit.Test;

public class MaxMinAlgorithmTest {

	private MaxMinAlgorithm algorithm;

	@Before
	public void setUp() throws Exception {
		algorithm = new MaxMinAlgorithm();
	}

	@Test
	public void testNoShortage() {

		HashMap<String, Double> requestedResources = new HashMap<String, Double>();

		requestedResources.put("customer_01", 50.0d);
		requestedResources.put("customer_02", 20.0d);

		Map<String, Double> allocatedResources = algorithm.evaluate(
				requestedResources, 100.0d);

		Log.printLine(allocatedResources);
		assertEquals(50, allocatedResources.get("customer_01"), 0);
		assertEquals(20, allocatedResources.get("customer_02"), 0);

	}

	@Test
	public void testShortage1() {
		HashMap<String, Double> requestedResources = new HashMap<String, Double>();

		requestedResources.put("customer_01", 50.0d);
		requestedResources.put("customer_02", 20.0d);
		requestedResources.put("customer_03", 55.0d);

		Map<String, Double> allocatedResources = algorithm.evaluate(
				requestedResources, 100.0d);

		Log.printLine(allocatedResources);
		assertEquals(40, allocatedResources.get("customer_01"), 0);
		assertEquals(20, allocatedResources.get("customer_02"), 0);
		assertEquals(40, allocatedResources.get("customer_03"), 0);
	}

	@Test
	public void testShortage2() {
		HashMap<String, Double> requestedResources = new HashMap<String, Double>();

		requestedResources.put("customer_01", 110.0d);
		requestedResources.put("customer_02", 20.0d);
		requestedResources.put("customer_03", 45.0d);

		Map<String, Double> allocatedResources = algorithm.evaluate(
				requestedResources, 100.0d);

		Log.printLine(allocatedResources);
		assertEquals(40, allocatedResources.get("customer_01"), 0);
		assertEquals(20, allocatedResources.get("customer_02"), 0);
		assertEquals(40, allocatedResources.get("customer_03"), 0);
	}

	@Test
	public void testShortage3() {
		HashMap<String, Double> requestedResources = new HashMap<String, Double>();

		requestedResources.put("customer_01", 50.0d);
		requestedResources.put("customer_02", 50.0d);

		Map<String, Double> allocatedResources = algorithm.evaluate(
				requestedResources, 100.0d);

		Log.printLine(allocatedResources);
		assertEquals(50, allocatedResources.get("customer_01"), 0);
		assertEquals(50, allocatedResources.get("customer_02"), 0);
	}

	@Test
	public void testShortage4() {
		HashMap<String, Double> requestedResources = new HashMap<String, Double>();

		requestedResources.put("customer_01", 50.0d);
		requestedResources.put("customer_02", 70.0d);

		Map<String, Double> allocatedResources = algorithm.evaluate(
				requestedResources, 100.0d);

		Log.printLine(allocatedResources);
		assertEquals(50, allocatedResources.get("customer_01"), 0);
		assertEquals(50, allocatedResources.get("customer_02"), 0);
	}

	@Test
	public void testShortage5() {
		HashMap<String, Double> requestedResources = new HashMap<String, Double>();

		requestedResources.put("customer_01", 73.0d);
		requestedResources.put("customer_02", 84.0d);
		requestedResources.put("customer_03", 44.0d);
		requestedResources.put("customer_04", 10.0d);
		requestedResources.put("customer_05", 0d);
		
		Map<String, Double> allocatedResources = algorithm.evaluate(
				requestedResources, 100.0d);

		Log.printLine(allocatedResources);
		assertEquals(30, allocatedResources.get("customer_01"), 0);
		assertEquals(30, allocatedResources.get("customer_02"), 0);
		assertEquals(30, allocatedResources.get("customer_03"), 0);
		assertEquals(10, allocatedResources.get("customer_04"), 0);
		assertEquals(0, allocatedResources.get("customer_05"), 0);
	}

	@Test
	public void testShortage6() {
		HashMap<String, Double> requestedResources = new HashMap<String, Double>();

		requestedResources.put("customer_01", 73.0d);
		requestedResources.put("customer_03", 44.0d);
		requestedResources.put("customer_04", 13.0d);
		requestedResources.put("customer_05", 9.0d);
		requestedResources.put("customer_06", 10.0d);
		requestedResources.put("customer_07", 11.0d);
		requestedResources.put("customer_08", 12.0d);
		
		Map<String, Double> allocatedResources = algorithm.evaluate(
				requestedResources, 100.0d);

		Log.printLine(allocatedResources);
		assertEquals(22.5, allocatedResources.get("customer_01"), 1);
		assertEquals(22.5, allocatedResources.get("customer_03"), 1);
		assertEquals(13, allocatedResources.get("customer_04"), 0);
		assertEquals(9, allocatedResources.get("customer_05"), 0);
		assertEquals(10, allocatedResources.get("customer_06"), 0);
		assertEquals(11, allocatedResources.get("customer_07"), 0);
		assertEquals(12, allocatedResources.get("customer_08"), 0);
	}
}
