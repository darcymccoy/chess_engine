package PackageForChessEngine;

// Written by Darcy McCoy
// Starting November 27, 2023

// This is a chess engine designed to be able to play 
// an entire game of chess against a human

import java.util.Scanner;

//Driver class
public class testMain {

	public static void main(String[] args) {

		// Initializing variables
		Scanner userInput = new Scanner(System.in);
		Position currentPosition = new Position(true,
				"rnbq2bnr" + "pppppppp" + "--------" + "--------" + "--------" + "--------" + "PPPPPPPP" + "RNBQ5BNR");

		// Line testLine = new Line(51789, currentPosition.findPossibleMoves('P', 51));

		System.out.println(currentPosition);
		System.out.println(currentPosition.evaluatePosition());
		System.out.println(currentPosition.calculate(2));
		// System.out.println(testLine);

		// Closing the scanner
		userInput.close();

		// Termination confirmation
		System.out.println("\nThe program has terminated.");
	}

}
