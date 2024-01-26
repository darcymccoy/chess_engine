package packageForChessEngine;

//Written by Darcy McCoy
//Starting November 27, 2023

import java.util.Scanner;

public class Game {
	private Position currentPosition;
	private int[] movesMade;
	protected boolean inGame;
	static private Scanner scanner = new Scanner(System.in);
	static private Engine engine = new Engine();

	public Game() {
		// Default constructor
		this(new Position(), new int[0], false);
	}

	public Game(Position currentPosition, int[] movesMade, boolean inGame) {
		// Parameterized constructor
		this.currentPosition = currentPosition;
		this.movesMade = movesMade;
		this.inGame = inGame;
	}
	
	public Game(Game otherGame) {
		// Copy constructor
		this(otherGame.currentPosition, otherGame.movesMade, otherGame.inGame);
	}

	public void startGame() {
		// Sets inGame to true
		inGame = true;
	}

	public void stopGame() {
		// Sets inGame to false
		inGame = false;
	}
	
	public void letEngineMakeMove() {
		// Finds the best move according to the engine and 
		// makes that move on the currentPosition
		int engineMove = engine.findTopMoveDepth3(currentPosition);
		if (foundLegalMove(engineMove)) {
			currentPosition.makeMove(engineMove);
			addMoveToMovesMade(engineMove);
		} else {
			stopGame();
		}
	}

	public void letUserMakeMove() {
		// Prompts the user for a move and makes that move on the currentPosition
		int userMove = 0;
		
		while (true) {
			System.out.print("Enter your move: ");
			try {
				userMove = scanner.nextInt();
			} catch(Exception e) {
				System.out.println("This isn't a legal move. Moves must be a 4 digit integer. Try again.");
				continue;
			}
			
			if (currentPosition.isLegalMove(userMove)) {
				currentPosition.makeMove(userMove);
				addMoveToMovesMade(userMove);
				break;
			} else {
				System.out.println("This isn't a legal move. Try again.");
			}
		}
	}
	
	public void addMoveToMovesMade(int move) {
		// Adds the move to the end of movesMade
		int[] newMovesMade = new int[movesMade.length + 1];
		
		for (int i = 0; i < movesMade.length; i++) {
			newMovesMade[i] = movesMade[i];
		}
		newMovesMade[newMovesMade.length - 1] = move;
		movesMade = newMovesMade;
	}
	
	public boolean foundLegalMove(int move) {
		// Returns true as long as the move isn't -1
		// **Can only assess engine moves**
		return move != -1;
	}
	
	public void closeScanner() {
		// Closes the scanner object
		scanner.close();
	}
	
	public String toString() {
		// Returns a string with the current position and the moves made
		String printGame = currentPosition.toString() + "\n";

		for (int i = 0; i < this.movesMade.length; i++) {
			printGame += movesMade[i] + " ";
			if ((i % 2) == 0)
				printGame += "\n";
		}
		return printGame;
	}

	public Position getCurrentPosition() {
		// Getter for currentPosition
		return currentPosition;
	}

	public void setCurrentPosition(Position currentPosition) {
		// Setter for currentPosition
		this.currentPosition = currentPosition;
	}

	public int[] getMovesMade() {
		// Getter for movesMade
		return movesMade;
	}

	public void setMovesMade(int[] movesMade) {
		// Setter for movesMade
		this.movesMade = movesMade;
	}

	public boolean isInGame() {
		// Getter for inGame
		return inGame;
	}

	public void setInGame(boolean inGame) {
		// Setter for inGame
		this.inGame = inGame;
	}
	
}