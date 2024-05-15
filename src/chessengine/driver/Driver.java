package chessengine.driver;

import java.util.Scanner;
import chessengine.system.Engine;
import chessengine.system.EngineVsEngine;
import chessengine.system.Position;

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
	 * Runs the games or tests specified in args.
	 * 
	 * @param args arguments to be ran when the program is ran
	 */
	public static void main(String[] args) {

		Scanner userInput = new Scanner(System.in);
		Engine engine = new Engine();
		boolean insideLoop = true;
		
		
		EngineVsEngine game1 = new EngineVsEngine();
		game1.play();
		game1.closeScanner();
		
		userInput.close();

		// Termination confirmation
		System.out.println("\nThe program has terminated.");
	}

}
