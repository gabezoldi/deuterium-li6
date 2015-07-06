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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author gzoldi
 *
 */
public class OptimalMiningTest {
	
	OptimalMining dilithiumMine = null;
	
	@BeforeMethod
	public void setUp() {
		dilithiumMine = new OptimalMining(MineType.DILITHIUM);
	}
	
	@AfterMethod
	public void tearDown() {
		dilithiumMine = null;
	}

	@DataProvider
	public Object[][] getGoodData() {
		return new Object[][] {
				{ new Integer[] { 206, 140, 300,  52, 107 }, "206, x, 300, x, 107", 613 },  // single neighbor
				{ new Integer[] { 147, 206,  52, 240, 300 }, "x, 206, x, x, 300",   506 },  // mixed neighbor
				{ new Integer[] { 145, 145, 145, 145, 145 }, "145, x, 145, x, 145", 435 }, 
				{ new Integer[] { 206, 140, 300,  52 }, 	 "206, x, 300, x", 		506 }, 	// 4 mines
				{ new Integer[] { 206, 140, 300}, 	 		 "206, x, 300", 		506 }, 	// 3 mines
				{ new Integer[] { 206, 140}, 				 "206, x", 				206 }, 	// 2 mines
				{ new Integer[] { 206}, 					 "206", 				206 }	// 1 mine
		};
	}
	
	@DataProvider
	public Object[][] getBadData() {
		return new Object[][] {
				{ new Integer[] { }, "", "" }   // empty
		};
	}
	
	@Test(groups = { "fast", "positive" }, dataProvider = "getGoodData")
	public void validMineStrip(Integer[] discoveredMines, String expectedOptimalPattern, int expectedMaxProfit) {
		
		// get most profitable mines
		dilithiumMine.generateRecommendations(discoveredMines);
		Map<String, Integer> mostProfitable = dilithiumMine.getMaximumProfit();
		
		// verify actual results
		//Assert.assertEquals( mostProfitable.size(), 1, "Incorrect count of patterns found." );  // nothing wrong with testng assertion
		assertThat("Incorrect count of patterns found.", mostProfitable.size(), equalTo(1)); 	  // I prefer hamcrest more readable
		 
		Assert.assertTrue( mostProfitable.containsKey(expectedOptimalPattern), "Incorrect mine strip pattern match.");
		Assert.assertTrue( mostProfitable.containsValue(expectedMaxProfit), "Incorrect max profit." );
	 }
	
//	@Test(groups = { "fast", "negative" }, dataProvider = "getBadData")
//	public void invalidMineStrip(Integer[] discoveredMines, String expectedOptimalPattern, int expectedMaxProfit) {
//		
//		// get most profitable mines
//		dilithiumMine.generateRecommendations(discoveredMines);
//		Map<String, Integer> mostProfitable = dilithiumMine.getMaximumProfit();
//		
//		// verify actual results
//		Assert.assertEquals( mostProfitable.size(), 1, "Incorrect count of patterns found." );
//		Assert.assertTrue( mostProfitable.containsKey(expectedOptimalPattern), "Incorrect mine strip pattern match.");
//		Assert.assertTrue( mostProfitable.containsValue(expectedMaxProfit), "Incorrect max profit." );
//	 }

	/*
	 * add testcases:
	 * 1. check mine strip that returns duplicate max profit
	 * 2. check mine strip with no mines [empty], 1 mine, 2 mines, 100 mines
	 * 3. check mine strip with values exceeding int range upper/lower bounds
	 */
}