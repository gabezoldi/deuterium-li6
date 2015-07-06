/*
 * *********************************************************
 *  Copyright (c) 2015 Comcast Corp.  All rights reserved.
 * *********************************************************
 */
package com.comcast.interview.dilithium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gzoldi
 *
 */
public class OptimalMining {
	
	private static final Logger logger = LoggerFactory.getLogger(OptimalMining.class);
	
	private MineType type;
	private Integer[] mines;
	private int totalMines = 0;

	public OptimalMining(MineType type) {
		this.type = type;
	}
	
	/**
	 * Generates recommendation of mine configuration that will 
	 * generate the most profit.
	 * 
	 * @param discovered the mines detected on planet
	 */
	public Map<String, Integer> generateRecommendations(Integer[] discovered) {
		mines = discovered;
		totalMines = mines.length;
		
		// check number of mines are valid > 0
	    if (totalMines <= 0) {
	    	String err = "Invalid number of mines specified: " + totalMines;
	    	logger.error(err);
	    	//throw new RuntimeException(err);
	    	return new HashMap<String, Integer>();
	    }
	    return getMaximumProfit();
	}
	
	/**
     * Calculate the max profit from control pattern list of valid 
     * mine configurations with no neighbors.
     * 
     */
	public Map<String, Integer> getMaximumProfit() {
		Map<String, Integer> maxProfit = new HashMap<String, Integer>();
		
		// find max profits including any mines with duplicate or same max profit
		Map<String, Integer> opps = calcProfits( getPatternsWithoutNeighbors() );
        int maxTotal = Collections.max(opps.values());
        
        // iterate thru and find all patterns with max profits
        for (Entry<String, Integer> entry : opps.entrySet()) 
            if (entry.getValue() == maxTotal)
            	maxProfit.put(entry.getKey(), maxTotal);
        return maxProfit;
	}

	/**
	 * Create possible pattern combinations and removes any 
	 * patterns with neighboring mine.
	 * 
	 * @see method hasNeighbor()
	 */
	public ArrayList<String> getPatternsWithoutNeighbors() {	    
	    return removeNeighbors( createPatterns() );   // get rid of neighboring mines cause they go boom!
	}
	
	/**
     * Create all possible binar patterns
     * 
     */
	public ArrayList<String> createPatterns() {	    
	    // define placeholder based on number of bits specified
	    String placeholder = "";
	    for (int i = 0; i < totalMines; i++) 
	    	placeholder += "0";

	    // create possibilities based on number of bit permutations
	    int[] possible = new int[] {0, 1};  // binary: 2 possible values

	    for (int i = 1; i < totalMines; i++) {
	        int[] permutation = new int[possible.length * 2];
	        
	        // bit shift
	        for (int j = 0; j < possible.length; j++) {
	        	permutation[j * 2] = possible[j] << 1;
	        	permutation[j * 2 + 1] = possible[j] << 1 | 1;
	        }
	        possible = permutation;
	    }
	    
	    ArrayList<String> patterns = new ArrayList<String>();
	    
	    // create list of patterns based on binary
	    for (int value: possible) {
	    	String bin = Integer.toBinaryString(value);
	    	String padded = placeholder.substring(bin.length()) + bin;  // pad zeroes
	    	patterns.add(padded);
	    }
	    return patterns;
	}
	
	/**
	 * Removes all neighbor patterns. 
	 *
	 */
	protected ArrayList<String> removeNeighbors(ArrayList<String> patterns) {
		// iterate thru pattern list and add to list of elements for removal
		ArrayList<String> removeItems = new ArrayList<String>();
		for (String p : patterns) 
			if (hasNeighbor(p)) 
				removeItems.add(p);
		
		// iterate thru removal list and remove from original pattern list
		for (String remove : removeItems) 
			patterns = removeElement(patterns, remove);
		return patterns;
	}
	
	/**
     * Calculate the profit for each mine configuration.
     * 
     */
	public Map<String, Integer> calcProfits(ArrayList<String> patterns) {
		Map<String, Integer> profitable  = new HashMap<String, Integer>();
		
		for (String pattern : patterns) {
			logger.debug(pattern);
			
			int i = 0;
			String minePattern = "";
			int mineTotal = 0;
			
			for (char c : pattern.toCharArray() ) {
		        switch (c) {
		            case '0': 
		            	minePattern += "x";
		            	break;
		            case '1': 
		            	minePattern += mines[i];
		            	mineTotal += Integer.valueOf(mines[i]);
		            	break;
		            default: 
		            	throw new RuntimeException("Error: Unexpected bit value in pattern.");
		        }
		        
		        // add comma sep for each element except last element
		        int lastIndex = totalMines - 1;
		        if (i < lastIndex) 
		        	minePattern += ", "; 
		        i++;
			}
			profitable.put(minePattern, mineTotal);
		}
		return profitable;
	}
	
	/**
	 * Remove pattern from list. 
	 *
	 * @param input    the list
	 * @param deleteMe the pattern to remove
	 * @return         the updated list
	 */
	protected ArrayList<String> removeElement(ArrayList<String> input, String deleteMe) {
		@SuppressWarnings("unchecked")
		ArrayList<String> cleaned = (ArrayList<String>) input.clone();
	    for (String item : input)
	        if (!deleteMe.equals(item))
	        	cleaned.remove(deleteMe);
	    return cleaned;
	}
	
	/**
	 * Check if has a neighbor exists in a given pattern.
	 * 
	 * @param pattern the pattern to check
	 * @return        true if neighbor exists (to left or right), 
	 *                e.g., 10101, 101, 10001 has no neighbor
	 *                      11101, 11, 111, 11001 has neighbor
	 *                      1 means its a mine, 0 means not a mine
	 */
	protected boolean hasNeighbor(String pattern) {
		char[] token = pattern.toCharArray();
		for (int i = 0; i < token.length - 1; i++) {
			if (token[i] == '1') 
				if (token[i] == token[i + 1]) 
					return true;
		}
		return false;
	}
	
	
	public static void main(String [ ] args) {
		OptimalMining dilithiumMine = new OptimalMining(MineType.DILITHIUM);
		Map<String, Integer> mostProfitable = 
				dilithiumMine.generateRecommendations(new Integer[] { 147, 206, 52, 240, 300 });
		System.out.println( mostProfitable );
	}
}