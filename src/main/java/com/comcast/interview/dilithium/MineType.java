/*
 * *********************************************************
 *  Copyright (c) 2015 Comcast Corp.  All rights reserved.
 * *********************************************************
 */
package com.comcast.interview;

public enum MineType {
    DILITHIUM 	("Special",    2.4e8),
    COAL 		("Hard Rock",  6.0e6),
    NATURAL_GAS ("Fracturing", 6.3e6),
    GOLD 		("Panning",    3.3e6),
    OIL 		("Hydraulic",  7.1e6), 
    ANALYTICS 	("Data", 	   9.0e7);

    private final String method;  // extraction method
    private final double capital; // startup capital estimate, in millions, as of 2015
    
    MineType(String method, double capital) {
        this.method = method;
        this.capital = capital;
    }
    
	private String method()  { return method;  }
    private double capital() { return capital; }

    // universal rate of return coefficient
    public static final double ROI_COEFFICIENT = 1.67E-11;

    double roi(double assets, double liabilities) {
        return ROI_COEFFICIENT * liabilities / (capital - 1 * (assets/4));
    }
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java MineType <assets in mil> <liabilities in mil>");
            System.exit(-1);
        }
        
        // program arguments
        double totalAssets 		= Double.parseDouble(args[0]);
        double totalLiabilities = Double.parseDouble(args[1]);
        
        for (MineType type : MineType.values()) 
        	System.out.printf("Your ROI on %s is %f%n", type, type.roi(totalAssets, totalLiabilities));
    }
}