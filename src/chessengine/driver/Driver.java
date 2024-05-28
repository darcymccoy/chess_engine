package chessengine.driver;

import java.util.Scanner;
import chessengine.system.EngineVsEngine;
import chessengine.system.UserVsEngine;

/**
 * Driver class for a chess engine designed to be able to play 
 * an entire game of chess against a human
 * 
 * @author Darcy McCoy
 * @version "%I%"
 * @since 1.0
 */
public class Driver {
	/**
	 * @param args 
	 */
	public static void main(String[] args) {
		Scanner userInput = new Scanner(System.in);
		
		EngineVsEngine game1 = new EngineVsEngine();
		game1.play();
		game1.closeScanner();
		
		userInput.close();

		System.out.println("\nThe program has terminated.");
	}

}
