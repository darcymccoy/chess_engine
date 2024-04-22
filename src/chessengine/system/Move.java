package chessengine.system;

/**
 * Contains the information for a single chess move. Illegal, pseudo legal, 
 * and legal moves can be stored as a <code>Move</code> object.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.1
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
		this(startSqr, endSqr, '-');
	}
	
	/**
	 * Parameterized constructor specifying the start and end squares, and the piece 
	 * being promoted to. This should only be used to called externally when used to
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
