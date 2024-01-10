package packageForChessEngine;

//Written by Darcy McCoy
//Starting November 27, 2023

public class Line {
	int evaluation;
	int[] moves = new int[0];

	public Line() {
		// Default constructor
		this(0, new int[0]);
	}

	public Line(int evaluation, int[] moves) {
		// Parameterized constructor
		this.evaluation = evaluation;
		this.moves = moves;
	}

	public Line(Line otherLine) {
		// Copy constructor
		this(otherLine.evaluation, otherLine.moves);
	}

	public void setAMove(int index, int move) {
		// Sets a specific move in moves
		this.moves[index] = move;
	}

	public String toString() {
		// Returns a string with the evaluation and the moves arranged with commas
		String printLine = (double) this.evaluation / 100 + ": ";
		for (int i = 0; i < this.moves.length; i++) {
			printLine += this.moves[i];
			if (i != (this.moves.length - 1))
				printLine += ", ";
		}
		return printLine;
	}

	public int getEvaluation() {
		// Getter for evaluation
		return evaluation;
	}

	public void setEvaluation(int evaluation) {
		// Setter for evaluation
		this.evaluation = evaluation;
	}

	public int[] getMoves() {
		// Getter for moves
		return moves;
	}

	public void setMoves(int[] moves) {
		// Setter for moves
		this.moves = moves;
	}

}
