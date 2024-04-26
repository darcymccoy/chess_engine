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
	 * Returns true if this move is promotion.
	 * 
	 * @param piece character representing the piece making this move
	 * @return <code>true</code> if this move is a pawn promoting; <code>false</code>
	 *         otherwise.
	 */
	public boolean isPromotion(char piece) {
		return ((piece == Chess.WH_PAWN) && Chess.isRank8Sqr(endSqr)) ||
				((piece == Chess.BK_PAWN) && Chess.isRank1Sqr(endSqr));
	}
	
	/**
	 * Returns true if this move is castling.
	 * 
	 * @param piece character representing the piece making this move
	 * @return <code>true</code> if this move is a king castling; <code>false</code>
	 *         otherwise.
	 */
	public boolean isCastling(char piece) {
		return ((piece == Chess.WH_KING) || (piece == Chess.WH_KING_CASTLE_BOTH_SIDES)
				|| (piece == Chess.WH_KING_CASTLE_KINGSIDE) || (piece == Chess.WH_KING_CASTLE_QUEENSIDE)
				|| (piece == Chess.BK_KING) || (piece == Chess.BK_KING_CASTLE_BOTH_SIDES)
				|| (piece == Chess.BK_KING_CASTLE_KINGSIDE) || (piece == Chess.BK_KING_CASTLE_QUEENSIDE))
				&& ((startSqr == (endSqr + Chess.EAST_2)) || (startSqr == (endSqr + Chess.WEST_2)));
	}
	
	/**
	 * Returns true if this move is en passant.
	 * 
	 * @param piece character representing the piece
	 * @param north1SqrContents the character that is at the square 1 square to the north 
	 * of the ending square of this move
	 * @param south1SqrContents the character that is at the square 1 square to the south 
	 * of the ending square of this move
	 * @return <code>true</code> if this move is a pawn capturing en passant;
	 *         <code>false</code> otherwise.
	 */
	public boolean isEnPassant(char piece, char north1SqrContents, char south1SqrContents) {
		return ((piece == Chess.WH_PAWN) && (south1SqrContents == Chess.BK_PAWN_ENPASS)) || 
				((piece == Chess.BK_PAWN) && (north1SqrContents == Chess.WH_PAWN_ENPASS));
	}
	
	/**
	 * Returns true if the move puts a pawn into a position where it can be captured
	 * en passant.
	 * 
	 * @param piece character representing the piece
	 * @param east1SqrContents the character that is at the square 1 square to the east 
	 * of the ending square of this move
	 * @param west1SqrContents the character that is at the square 1 square to the west 
	 * of the ending square of this move
	 * @return <code>true</code> if the move is a pawn advancing 2 squares and
	 *         potentially allowing itself to be captured en passant;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAllowsEnPassant(char piece, char east1SqrContents, char west1SqrContents) {
		return ((piece == Chess.WH_PAWN) && ((startSqr + Chess.NORTH_2) == endSqr) 
				&& (east1SqrContents == Chess.BK_PAWN) || (west1SqrContents == Chess.BK_PAWN)) ||
				((piece == Chess.BK_PAWN) && ((startSqr + Chess.SOUTH_2) == endSqr) 
						&& (east1SqrContents == Chess.WH_PAWN) || (west1SqrContents == Chess.WH_PAWN));
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
