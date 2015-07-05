/*
 * *********************************************************
 *  Copyright (c) 2015 Comcast Corp.  All rights reserved.
 * *********************************************************
 */
package com.comcast.interview.dilithium;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author gzoldi
 *
 */
public class OptimalMiningTest {
	
	OptimalMining optimal = null;
	
	@BeforeMethod
	public void setUp() {
		optimal = new OptimalMining(MineType.DILITHIUM);
	}
	
	@AfterMethod
	public void tearDown() {
		optimal = null;
	}

	@DataProvider
	public Object[][] getData() {
		return new Object[][] {
				{ new Integer[] { 206, 140, 300, 52, 107 }, "206, x, 300, x, 107", 613 }, // single neighbor
				{ new Integer[] { 147, 206, 52, 240, 300 }, "x, 206, x, x, 300",   506 }  // mixed neighbor
		};
	}
	
	@Test(groups={ "fast" }, dataProvider="getData")
	public void validMineStrip(Integer[] discoveredMines, String expectedOptimalPattern, int expectedMaxProfit) {
		
		optimal.generateRecommendation(discoveredMines);
		Map<String, Integer> actual = optimal.getMaxProfit();
		
		// verify results
		Assert.assertEquals( actual.size(), 1, "Incorrect count of patterns found." );
		Assert.assertTrue( actual.containsKey(expectedOptimalPattern), "Incorrect mine strip pattern match.");
		Assert.assertTrue( actual.containsValue(expectedMaxProfit), "Incorrect max profit." );
	 }

	/*
	 * add testcases:
	 * 1. check mine strip that returns duplicate max profit
	 * 2. check mine strip with no mines [empty], 1 mine, 2 mines, 100 mines
	 * 3. check mine strip with values exceeding int range upper/lower bounds
	 */
}