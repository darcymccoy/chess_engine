package PackageForChessEngine;

//Written by Darcy McCoy
//Starting November 27, 2023

public class Game {
	private Position currentPosition = new Position();
	private Line currentLine = new Line();
	private int[] movesMade = new int[0];

	public Game() {
		// Default constructor
		this(new Position(), new Line(), new int[0]);
	}

	public Game(Position currentPosition, Line currentLine, int[] movesMade) {
		// Parameterized constructor
		this.currentPosition = currentPosition;
		this.currentLine = currentLine;
		this.movesMade = movesMade;
	}

	public String toString() {
		// Returns a string with the current position and line, as well as the moves
		// made
		String printGame = currentPosition.toString() + "\n" + currentLine.toString() + "\n";

		for (int i = 0; i < this.movesMade.length; i++) {
			printGame += movesMade[i] + " ";
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

	public Line getCurrentLine() {
		// Getter for currentLine
		return currentLine;
	}

	public void setCurrentLine(Line currentLine) {
		// Setter for currentLine
		this.currentLine = currentLine;
	}

	public int[] getMovesMade() {
		// Getter for movesMade
		return movesMade;
	}

	public void setMovesMade(int[] movesMade) {
		// Setter for movesMade
		this.movesMade = movesMade;
	}

}