package Platform;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public  class Utility {
	
	
	public static boolean isInteger(String s) {
		try { 
			Integer.parseInt(s); 
		} catch(NumberFormatException e) { 
			return false; 
		}
		// only got here if we didn't return false
		return true;
	}
	
	
	/**
	 * write to the results to the specified file name
	 * @param outputFileName
	 * @param txt
	 */
	public static void writeTextToFile( String outputFileName, String txt ) {
		//Make sure output file exists
		try {
			File outputFile = new File( outputFileName );
			if( !outputFile.exists() ) {
				outputFile.createNewFile();
			} 
		}catch ( IOException e ) {
			System.out.println( "Could not write to file: " + e.toString() );
		}
		//Write to file
		try ( PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( outputFileName, true ) ) ) ) {
			out.println( txt );
		}catch ( IOException e ) {
			System.out.println( "Could not write to file: " + e.toString() );
		}
	}

}
