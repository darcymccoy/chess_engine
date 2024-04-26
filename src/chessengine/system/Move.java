package chessengine.system;

/**
 * Contains the information for a single chess move. Illegal, pseudo legal, 
 * and legal moves can be stored as a <code>Move</code> object.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public class Move {
	
	/** The square on the board that the piece originated from. */
	int startSqr;
	
	/** The square on the board that the piece ends on. */
	int endSqr;
	
	/** 
	 * The character representation of the piece that the pawn is promoting to.
	 * If the move isn't promotion, then this will be <code>'-'</code>.
	 */
	char promoteTo;
	
	/**
	 * 
	 * @param startSqr
	 * @param endSqr
	 */
	public Move(int startSqr, int endSqr) {
		this(startSqr, endSqr, Chess.EMPTY);
	}
	
	/**
	 * Parameterized constructor specifying the start and end squares, and the piece 
	 * being promoted to. This should only be called externally when used to
	 * construct moves that are pawns promoting.
	 * 
	 * @param startSqr integer square on the board that the piece originated on
	 * @param endSqr integer square on the board that the piece ends on
	 * @param promoteTo Character representing the piece that the pawn promotes to. If this move isn't 
	 * promotion, then this will be <code>'-'</code>.
	 */
	public Move(int startSqr, int endSqr, char promoteTo) {
		this.startSqr = startSqr;
		this.endSqr = endSqr;
		this.promoteTo = promoteTo;
	}

	
	/**
	 * Returns a string representation of the move. For promotion moves, a "=" and the 
	 * character representation of the move being promoted to will be added to the end of the string.
	 * 
	 * @return a string representation of this move
	 */
	@Override
	public String toString() {
		if (promoteTo == Chess.EMPTY) {
			return Integer.toString(startSqr) +  Integer.toString(endSqr);
		} else {
			return startSqr + endSqr + "=" + promoteTo;
		}
	}

	/**
	 * Tests if 2 moves are the same.
	 * 
	 * @param obj Object to be tested
	 * @return <code>true</code> if this move and the testing move are the same chess moves
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Move other = (Move) obj;
		return (endSqr == other.endSqr) && (promoteTo == other.promoteTo) && (startSqr == other.startSqr);
	}

	/**
	 * @return the startSqr
	 */
	public int getStartSqr() {
		return startSqr;
	}

	/**
	 * @param startSqr the startSqr to set
	 */
	public void setStartSqr(int startSqr) {
		this.startSqr = startSqr;
	}

	/**
	 * @return the endSqr
	 */
	public int getEndSqr() {
		return endSqr;
	}

	/**
	 * @param endSqr the endSqr to set
	 */
	public void setEndSqr(int endSqr) {
		this.endSqr = endSqr;
	}

	/**
	 * @return the promoteTo
	 */
	public char getPromoteTo() {
		return promoteTo;
	}

	/**
	 * @param promoteTo the promoteTo to set
	 */
	public void setPromoteTo(char promoteTo) {
		this.promoteTo = promoteTo;
	}
	
	
	
}
