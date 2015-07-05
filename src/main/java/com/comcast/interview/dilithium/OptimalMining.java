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
	
	private ArrayList<String> patterns = new ArrayList<String>();
    Map<String, Integer> 	  results  = new HashMap<String, Integer>();

	public OptimalMining(MineType type) {
		this.type = type;
	}
	
	/**
	 * Generates recommendation for mine configuration that will
	 * create the maximum profit.
	 * 
	 * @param discovered the mines detected on the planet
	 */
	public void generateRecommendation(Integer[] discovered) {
		mines = discovered;
		totalMines = mines.length;
		
		// check number of mines are valid > 0
	    if (totalMines <= 0) 
	    	logger.error("Invalid number of mines specified: " + totalMines);
	    
	    // create pattern possibilities
	    createPatterns();
	    
	    // superimpose mine configuration onto patterns
	    // calc the total profits for each pattern without neighbors
	    calcProfits();
	}

	/**
     * Creates possible patterns without neighboring mines which 
     * can explode during extraction.
     * 
     */
	public void createPatterns() {	    
	    // define placeholder based on number of bits specified
	    String placeholder = "";
	    for (int i = 0; i < totalMines; i++) 
	    	placeholder += "0";

	    /*
	     *  create possibilities based on number of bit permutations
	     */
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
	    
	    // create list of possible patterns
	    for (int value: possible) {
	    	String bin = Integer.toBinaryString(value);
	    	String padded = placeholder.substring(bin.length()) + bin;  // pad zeroes
	    	patterns.add(padded);
	    }
	    
	    // get rid of neighboring mines cause they go boom!
	    removeNeighbors();
	}
	
	/**
     * Calculates the profit for each mine configuration 
     * of possible patterns.
     * 
     */
	public void calcProfits() {
		for (String pattern : getPatterns()) {
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
			results.put(minePattern, mineTotal);
		}
	}

	/**
     * Determines the max profit and pattern from list of valid mine 
     * configurations and prints it out.
     * 
     */
	public HashMap<String, Integer> getMaxProfit() {
		HashMap<String, Integer> maxProfits = new HashMap<String, Integer>();
		
		// print max profits including any mines with duplicate max profits
        int maxTotal = Collections.max(results.values());
        for (Entry<String, Integer> entry : results.entrySet()) 
            if (entry.getValue() == maxTotal)
            	maxProfits.put(entry.getKey(), maxTotal);
        return maxProfits;
	}
	
	/**
     * Returns all valid patterns without neighbors.
     * 
     * @return  list of possible patterns
     */
	public ArrayList<String> getPatterns() {
		return patterns;
	}
	
	/**
	 * Removes all patterns that have neighbors from the list. 
	 *
	 */
	protected void removeNeighbors() {
		ArrayList<String> removeItems = new ArrayList<String>();
		for (String pat : patterns) 
			if (hasNeighbor(pat)) 
				removeItems.add(pat);
		for (String remove : removeItems) 
			patterns = removeElement(patterns, remove);
	}
	
	/**
	 * Removes the element from the list. 
	 *
	 * @param input    the list
	 * @param deleteMe the element to remove
	 * @return         the updated list
	 */
	protected ArrayList<String> removeElement(ArrayList<String> input, String deleteMe) {
		ArrayList<String> cleaned = (ArrayList<String>) input.clone();
	    for (String item : input)
	        if (!deleteMe.equals(item))
	        	cleaned.remove(deleteMe);
	    return cleaned;
	}
	
	/**
	 * Given a pattern checks if there are neighboring mines.
	 * 
	 * @param pattern the pattern to verify
	 * @return        true if there is a neighbor to the left or right.
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
	
}