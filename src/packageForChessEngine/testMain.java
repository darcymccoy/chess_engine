package packageForChessEngine;

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
		Engine engine = new Engine();
		boolean insideLoop = true;
		
		while(insideLoop) {
		System.out.println(currentPosition);
		System.out.println(engine.evaluatePosition(currentPosition));
		System.out.println(engine.findTopMoveDepth1(currentPosition));
		System.out.println(engine.findTopMoveDepth2(currentPosition));
		System.out.println(currentPosition.calculate(2));
		System.out.print("Enter a move: ");
		int userMove = userInput.nextInt();
		System.out.println(currentPosition.isLegalMove(userMove));
		currentPosition.makeMove(userMove);
		}
		
		userInput.close();

		// Termination confirmation
		System.out.println("\nThe program has terminated.");
	}

}
