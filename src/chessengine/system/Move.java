package chessengine.system;

/**
 * Contains the information for a single chess move. Illegal, pseudo legal, and
 * legal moves can be stored as a <code>Move</code> object.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public class Move {

	/** The character representation of the piece that is making this move. */
	private char piece;

	/** The square on the board that the piece originated from. */
	private int startSqr;

	/** The square on the board that the piece ends on. */
	private int endSqr;

	/**
	 * The character representation of the piece that the pawn is promoting to. If
	 * the move isn't promotion, then this will be <code>'-'</code>.
	 */
	private char promoteTo;

	/**
	 * Class constructor specifying the piece, start and end squares of this move.
	 * This can construct every move except for pawns promoting.
	 * 
	 * @param piece    character representing the piece that is making this move
	 * @param startSqr integer square on the board that the piece originated on
	 * @param endSqr   integer square on the board that the piece ends on
	 */
	public Move(char piece, int startSqr, int endSqr) {
		this(piece, startSqr, endSqr, Chess.EMPTY);
	}

	/**
	 * Parameterized constructor specifying the start and end squares, and the piece
	 * being promoted to. This should only be called externally when used to
	 * construct moves that are pawns promoting.
	 * 
	 * @param piece     character representing the piece that is making this move
	 * @param startSqr  integer square on the board that the piece originated on
	 * @param endSqr    integer square on the board that the piece ends on
	 * @param promoteTo character representing the piece that the pawn promotes to.
	 *                  If this move isn't promotion, then this will be
	 *                  <code>'-'</code>.
	 */
	public Move(char piece, int startSqr, int endSqr, char promoteTo) {
		this.piece = piece;
		this.startSqr = startSqr;
		this.endSqr = endSqr;
		this.promoteTo = promoteTo;
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param otherMove the <code>Move</code> to copy
	 */
	public Move(Move otherMove) {
		this(otherMove.piece, otherMove.startSqr, otherMove.endSqr, otherMove.promoteTo);
	}

	/**
	 * Returns a copy of this move.
	 * 
	 * @return <code>Move</code> that is a copy of this move
	 */
	@Override
	public Move clone() {
		return new Move(this);
	}
	
	/**
	 * Returns true if this move is promotion.
	 * 
	 * @return <code>true</code> if this move is a pawn promoting;
	 *         <code>false</code> otherwise.
	 */
	public boolean isPromotion() {
		return ((piece == Chess.WH_PAWN) && Chess.isRank8Sqr(endSqr))
				|| ((piece == Chess.BK_PAWN) && Chess.isRank1Sqr(endSqr));
	}

	/**
	 * Returns true if this move is castling.
	 * 
	 * @return <code>true</code> if this move is a king castling; <code>false</code>
	 *         otherwise.
	 */
	public boolean isCastling() {
		return isKingsideCastling() || isQueensideCastling();
	}
	
	/**
	 * Returns true if this move is castling on the kingside of the board.
	 * 
	 * @return <code>true</code> if this move is a king castling on the kingside; <code>false</code>
	 *         otherwise.
	 */
	public boolean isKingsideCastling() {
		return (Chess.isWhiteKing(piece) || Chess.isBlackKing(piece)) && (startSqr == (endSqr + Chess.WEST_2));
	}
	
	/**
	 * Returns true if this move is castling on the queenside of the board.
	 * 
	 * @return <code>true</code> if this move is a king castling on the queenside; <code>false</code>
	 *         otherwise.
	 */
	public boolean isQueensideCastling() {
		return (Chess.isWhiteKing(piece) || Chess.isBlackKing(piece)) && (startSqr == (endSqr + Chess.EAST_2));
	}

	/**
	 * Returns true if this move is a pawn capturing en passant.
	 * 
	 * @param endSqrContents the character that is at the square where this piece is going in this move
	 * @return <code>true</code> if this move is a pawn capturing en passant;
	 *         <code>false</code> otherwise.
	 */
	public boolean isEnPassant(char endSqrContents) {
		return ((piece == Chess.WH_PAWN) || (piece == Chess.BK_PAWN)) && 
				(endSqrContents == Chess.EMPTY) && ((startSqr % 8) != (endSqr % 8));
	}

	/**
	 * Returns true if the move puts a pawn into a position where it can be captured
	 * en passant.
	 * 
	 * @param east1SqrContents the character that is at the square 1 square to the
	 *                         east of the ending square of this move
	 * @param west1SqrContents the character that is at the square 1 square to the
	 *                         west of the ending square of this move
	 * @return <code>true</code> if the move is a pawn advancing 2 squares and
	 *         potentially allowing itself to be captured en passant;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAllowsEnPassant(char east1SqrContents, char west1SqrContents) {
		return ((piece == Chess.WH_PAWN) && ((startSqr + Chess.NORTH_2) == endSqr)
				&& (east1SqrContents == Chess.BK_PAWN) || (west1SqrContents == Chess.BK_PAWN))
				|| ((piece == Chess.BK_PAWN) && ((startSqr + Chess.SOUTH_2) == endSqr)
						&& (east1SqrContents == Chess.WH_PAWN) || (west1SqrContents == Chess.WH_PAWN));
	}

	/**
	 * Returns a new array without the elements which are <code>null</code>. The
	 * <code>numberNonNullElements</code> must match the number of non
	 * <code>null</code> elements in the array.
	 * 
	 * @param arrayToUpdate         the move array to be updated
	 * @param numberNonNullElements int number of elements that aren't
	 *                              <code>null</code>
	 * @return Move[] without any elements that are <code>null</code>
	 */
	public static Move[] removeNullElements(Move[] arrayToUpdate, int numberNonNullElements) {
		Move[] newArray = new Move[numberNonNullElements];
		for (int i = 0, j = 0; j < newArray.length; i++, j++) {
			if (arrayToUpdate[i] != null)
				newArray[j] = arrayToUpdate[i];
			else
				j--;
		}
		return newArray;
	}

	/**
	 * Returns a string representation of the move. For promotion moves, a "=" and
	 * the character representation of the move being promoted to will be added to
	 * the end of the string.
	 * 
	 * @return a string representation of this move
	 */
	@Override
	public String toString() {
		if (promoteTo == Chess.EMPTY) {
			return Integer.toString(startSqr) + Integer.toString(endSqr);
		} else {
			return startSqr + endSqr + "=" + promoteTo;
		}
	}

	/**
	 * Tests if 2 moves are the same.
	 * 
	 * @param obj Object to be tested
	 * @return <code>true</code> if this move and the testing move are the same
	 *         chess moves
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
		return (piece == other.piece) && (endSqr == other.endSqr) && (promoteTo == other.promoteTo)
				&& (startSqr == other.startSqr);
	}

	/**
	 * @return the piece
	 */
	public char getPiece() {
		return piece;
	}

	/**
	 * @param piece the piece to set
	 */
	public void setPiece(char piece) {
		this.piece = piece;
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
