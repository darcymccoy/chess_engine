package packageForChessEngine;

// Written by Darcy McCoy
// Starting November 27, 2023

// Custom exception that is thrown whenever an attempt
// is made to find moves when there are no legal moves to make

public class NoLegalMovesException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoLegalMovesException() {
		super();
	}

	public NoLegalMovesException(String message) {
		super(message);
	}
}
