package chessengine.system;

/**
 * Stores the information of a single chess board.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public class Board {
	
	/** The index of the A8 square on the board. */
	public static final int A8_SQR = 0;

	/** The index of the E8 square on the board. */
	public static final int E8_SQR = 4;

	/** The index of the H8 square on the board. */
	public static final int H8_SQR = 7;

	/** The index of the A7 square on the board. */
	public static final int A7_SQR = 8;

	/** The index of the H7 square on the board. */
	public static final int H7_SQR = 15;

	/** The index of the A2 square on the board. */
	public static final int A2_SQR = 48;

	/** The index of the H2 square on the board. */
	public static final int H2_SQR = 55;

	/** The index of the A1 square on the board. */
	public static final int A1_SQR = 56;

	/** The index of the E1 square on the board. */
	public static final int E1_SQR = 60;

	/** The index of the H1 square on the board. */
	public static final int H1_SQR = 63;
	
	/** The squares of the board stored as a 64 character <code>String</code>. */
	private String sqrs;

	/**
	 * Default constructor that sets the squares to the standard starting chess position.
	 */
	public Board() {
		this("rnbq2bnr" + "pppppppp" + "--------" + "--------" + "--------" + "--------" + "PPPPPPPP" + "RNBQ5BNR");
	}
	
	/**
	 * Class constructor which specifies the squares.
	 * 
	 * @param sqrs the squares of the chess board
	 */
	public Board(String sqrs) {
		this.sqrs = sqrs;
	}
	
	/**
	 * Returns the contents of a square.
	 *
	 * @param sqr int value the square
	 * @return character contents of the square
	 */
	public char getSqr(int sqr) {
		return sqrs.charAt(sqr);
	}
	
	/**
	 * Updates this square on the board to either empty or to the piece that is
	 * passed.
	 *
	 * @param charToPut character representing the piece or an empty square
	 * @param sqr       int value of the square to be updated
	 */
	public void setSqr(char charToPut, int sqr) {
		sqrs = sqrs.substring(0, sqr) + charToPut + sqrs.substring(sqr + 1);
	}
	
	/**
	 * Updates the board so that pawns that could have been captured en passant
	 * become regular pawns.
	 */
	public void removeEnPassantAbility() {
		for (int i = 0; i < sqrs.length(); i++) {
			if (getSqr(i) == Chess.WH_PAWN_ENPASS) {
				setSqr(Chess.WH_PAWN, i);
			} else if (getSqr(i) == Chess.BK_PAWN_ENPASS) {
				setSqr(Chess.BK_PAWN, i);
			}
		}
	}
	
	/**
	 * Updates the king's castling ability for non king moves.
	 *
	 * @param move the move that the (non king) piece is making
	 */
	public void updateKingCastlingAbility(Move move) {
		// Updating white king for castling ability
		if ((getSqr(E1_SQR) == Chess.WH_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == H1_SQR) || (move.getEndSqr() == H1_SQR))) {
			setSqr(Chess.WH_KING_CASTLE_QUEENSIDE, E1_SQR);
		} else if ((getSqr(E1_SQR) == Chess.WH_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == A1_SQR) || (move.getEndSqr() == A1_SQR))) {
			setSqr(Chess.WH_KING_CASTLE_KINGSIDE, E1_SQR);
		} else if (((getSqr(E1_SQR) == Chess.WH_KING_CASTLE_KINGSIDE)
				&& ((move.getStartSqr() == H1_SQR) || (move.getEndSqr() == H1_SQR)))
				|| ((getSqr(E1_SQR) == Chess.WH_KING_CASTLE_QUEENSIDE)
						&& (((move.getStartSqr()) == A1_SQR) || (move.getEndSqr() == A1_SQR)))) {
			setSqr(Chess.WH_KING, E1_SQR);
		}
		// Updating black king for castling ability
		if ((getSqr(E8_SQR) == Chess.BK_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == H8_SQR) || (move.getEndSqr() == H8_SQR))) {
			setSqr(Chess.BK_KING_CASTLE_QUEENSIDE, E8_SQR);
		} else if ((getSqr(E8_SQR) == Chess.BK_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == A8_SQR) || (move.getEndSqr() == A8_SQR))) {
			setSqr(Chess.BK_KING_CASTLE_KINGSIDE, E8_SQR);
		} else if (((getSqr(E8_SQR) == Chess.BK_KING_CASTLE_KINGSIDE)
				&& ((move.getStartSqr() == H8_SQR) || (move.getEndSqr() == H8_SQR)))
				|| ((getSqr(E8_SQR) == Chess.BK_KING_CASTLE_QUEENSIDE)
						&& ((move.getStartSqr() == A8_SQR) || (move.getEndSqr() == A8_SQR)))) {
			setSqr(Chess.BK_KING, E8_SQR);
		}
	}
	
	/**
	 * Returns one of the kings' squares.
	 *
	 * @param findWhiteKing <code>boolean</code> whether white is the color of the
	 *                      king to be found
	 * @return int value where the king is on the board
	 */
	public int findKingSqr(boolean findWhiteKing) {
		for (int i = 0; i < sqrs.length(); i++) {
			if ((findWhiteKing && Chess.isWhiteKing(getSqr(i))) || (!findWhiteKing && Chess.isBlackKing(getSqr(i)))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns true if there is no piece at this square.
	 *
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square has no piece on it;
	 *         <code>false</code> otherwise.
	 */
	public boolean isEmptySqr(int sqr) {
		return getSqr(sqr) == Chess.EMPTY;
	}
	/**
	 * Returns true if the square is on the 1st rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 1st rank;
	 *         <code>false</code> otherwise.
	 */
	public static boolean isRank1Sqr(int sqr) {
		return (sqr >= A1_SQR) && (sqr <= H1_SQR);
	}

	/**
	 * Returns true if the square is on the 2nd rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 2nd rank;
	 *         <code>false</code> otherwise.
	 */
	public static boolean isRank2Sqr(int sqr) {
		return (sqr >= A2_SQR) && (sqr <= H2_SQR);
	}

	/**
	 * Returns true if the square is on the 7th rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 7th rank;
	 *         <code>false</code> otherwise.
	 */
	public static boolean isRank7Sqr(int sqr) {
		return (sqr >= A7_SQR) && (sqr <= H7_SQR);
	}

	/**
	 * Returns true if the square is on the 8th rank of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on the 8th rank;
	 *         <code>false</code> otherwise.
	 */
	public static boolean isRank8Sqr(int sqr) {
		return (sqr >= A8_SQR) && (sqr <= H8_SQR);
	}

	/**
	 * Returns true if the square is on file A of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file A; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isFileASqr(int sqr) {
		return sqr % 8 == 0;
	}

	/**
	 * Returns true if the square is on file B of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file B; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isFileBSqr(int sqr) {
		return (sqr + Chess.WEST_1) % 8 == 0;
	}

	/**
	 * Returns true if the square is on file G of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file G; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isFileGSqr(int sqr) {
		return (sqr + Chess.EAST_2) % 8 == 0;
	}

	/**
	 * Returns true if the square is on file H of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file H; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isFileHSqr(int sqr) {
		return (sqr + Chess.EAST_1) % 8 == 0;
	}
	
	/**
	 * Returns true if this vector put this square over the edge of the board.
	 * 
	 * @param sqr the square to be tested
	 * @param vector the direction vector that was used to find test square 
	 * @return <code>true</code> if this square has gone over any of the edges of the board; <code>false</code>
	 *         otherwise.
	 */
	public static boolean hasExceededAnEdge(int sqr, int vector) {
		if ((sqr < A8_SQR) || (sqr > H1_SQR))
			return true;
		return (Chess.containsEast1Direction(vector) && isFileHSqr(sqr + Chess.WEST_1))
				|| (Chess.containsWest1Direction(vector) && isFileASqr(sqr + Chess.EAST_1))
				|| (Chess.containsEast1Direction(vector) && (isFileHSqr(sqr + Chess.WEST_2) || isFileGSqr(sqr + Chess.WEST_2)))
				|| (Chess.containsWest2Direction(vector) && (isFileASqr(sqr + Chess.EAST_2) || isFileBSqr(sqr + Chess.EAST_2)));
	}
	
	/**
	 * @return the squares
	 */
	public String getSqrs() {
		return sqrs;
	}

	/**
	 * @param sqrs the squares to set
	 */
	public void setSqrs(String sqrs) {
		this.sqrs = sqrs;
	}
	
}
