package chessengine.system;

import java.util.LinkedList;

public class DrawRules {
	
	/** After 100 plies a game is drawn. In chess this is known as the 50 move rule. */
	private static final int PLY_MAX_FOR_50_MOVE_RULE = 100;
	
	/** If 2 other position are exactly the same as the current one, a game is drawn by threefold repetition */
	private static final int THREEFOLD_LIMIT = 2;
	
	/** The message to display the draw type */
	private String message;
	
	/**
	 * Returns true if the game is drawn by threefold repetition, 50 move, or insufficient material rules.
	 * 
	 * @param movesMade moves that have been made in this game
	 * @param position current position in this game
	 * @return <code>true</code> if this game is a draw by one of the special rules;
	 *         <code>false</code> otherwise.
	 */
	public boolean isDraw(LinkedList<Move> movesMade, Position position) {
		return isInsufficientMaterialDraw(position) || isThreefoldDraw(movesMade, position) || is50MoveDraw(movesMade);
	}
	
	/**
	 * Returns true if the game is drawn by the insufficient material rule.
	 * 
	 * @param position current position in this game
	 * @return <code>true</code> if neither color has enough material to checkmate;
	 *         <code>false</code> otherwise.
	 */
	private boolean isInsufficientMaterialDraw(Position position) {
		return false;// To be written
	}
	
	/**
	 * Returns true if the game is drawn by threefold repetition.
	 * 
	 * @param movesMade moves that have been made in this game
	 * @param position current position in this game
	 * @return <code>true</code> if the same position has been reached 3 times;
	 *         <code>false</code> otherwise.
	 */
	private boolean isThreefoldDraw(LinkedList<Move> movesMade, Position position) {
		int equalPositionsCount = 0;
		Position tempPosition = new Position();
		for (Move move : movesMade) {
			if (tempPosition.equals(position))
				equalPositionsCount++;
			tempPosition.makeMove(move);
		}
		return equalPositionsCount >= THREEFOLD_LIMIT;
	}
	
	/**
	 * Returns true if the game is drawn by the 50 move rule.
	 * 
	 * @param movesMade 
	 * @return <code>true</code> if 50 moves without a capture or pawn move have been made;
	 *         <code>false</code> otherwise.
	 */
	private boolean is50MoveDraw(LinkedList<Move> movesMade) {
		if (movesMade.size() < PLY_MAX_FOR_50_MOVE_RULE)
			return false;
		for (int i = 1; i < PLY_MAX_FOR_50_MOVE_RULE; i++) {
			Move move = movesMade.get(movesMade.size() - i);
			if (move.isCapture() || (move.getPiece() == Chess.WH_PAWN) || (move.getPiece() == Chess.BK_PAWN))
				return false;
		}
		return true;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
