package chessengine.system;

/**
 * The <code>Chess</code> class contains chess specific information. For
 * example, the integer value of squares and methods to determine the color of a
 * piece.
 * 
 * @author Darcy McCoy
 * @since 1.0
 */
public final class Chess {

	/**
	 * Prevents instantiation of this class.
	 */
	private Chess() {
	}

	/**
	 * Direction vector for north 1 square from white's perspective of the board.
	 */
	public static final int NORTH_1 = -8;

	/**
	 * Direction vector for north 2 squares from white's perspective of the board.
	 */
	public static final int NORTH_2 = NORTH_1 + NORTH_1;

	/**
	 * Direction vector for north east 1 square from white's perspective of the
	 * board.
	 */
	public static final int NORTH_EAST_1 = -7;

	/** Direction vector for east 1 square from white's perspective of the board. */
	public static final int EAST_1 = 1;

	/**
	 * Direction vector for east 2 squares from white's perspective of the board.
	 */
	public static final int EAST_2 = EAST_1 + EAST_1;

	/**
	 * Direction vector for east 3 squares from white's perspective of the board.
	 */
	public static final int EAST_3 = EAST_1 + EAST_1 + EAST_1;

	/**
	 * Direction vector for south east 1 square from white's perspective of the
	 * board.
	 */
	public static final int SOUTH_EAST_1 = 9;

	/**
	 * Direction vector for south 1 square from white's perspective of the board.
	 */
	public static final int SOUTH_1 = 8;

	/**
	 * Direction vector for south 2 squares from white's perspective of the board.
	 */
	public static final int SOUTH_2 = SOUTH_1 + SOUTH_1;

	/**
	 * Direction vector for south west 1 square from white's perspective of the
	 * board.
	 */
	public static final int SOUTH_WEST_1 = 7;

	/** Direction vector for west 1 square from white's perspective of the board. */
	public static final int WEST_1 = -1;

	/**
	 * Direction vector for west 2 squares from white's perspective of the board.
	 */
	public static final int WEST_2 = WEST_1 + WEST_1;

	/**
	 * Direction vector for west 3 squares from white's perspective of the board.
	 */
	public static final int WEST_3 = WEST_1 + WEST_1 + WEST_1;

	/**
	 * Direction vector for west 4 squares from white's perspective of the board.
	 */
	public static final int WEST_4 = WEST_1 + WEST_1 + WEST_1 + WEST_1;

	/**
	 * Direction vector for north west 1 square from white's perspective of the
	 * board.
	 */
	public static final int NORTH_WEST_1 = -9;

	/** Character representation of an empty square. */
	public static final char EMPTY = '-';

	/** Character representation of the white pawn. */
	public static final char WH_PAWN = 'P';

	/** Character representation of the white knight. */
	public static final char WH_KNIGHT = 'N';

	/** Character representation of the white bishop. */
	public static final char WH_BISHOP = 'B';

	/** Character representation of the white rook. */
	public static final char WH_ROOK = 'R';

	/** Character representation of the white queen. */
	public static final char WH_QUEEN = 'Q';

	/** Character representation of the white king. */
	public static final char WH_KING = 'K';

	/** Character representation of the black pawn. */
	public static final char BK_PAWN = 'p';

	/** Character representation of the black knight. */
	public static final char BK_KNIGHT = 'n';

	/** Character representation of the black bishop. */
	public static final char BK_BISHOP = 'b';

	/** Character representation of the black rook. */
	public static final char BK_ROOK = 'r';

	/** Character representation of the black queen. */
	public static final char BK_QUEEN = 'q';

	/** Character representation of the black king. */
	public static final char BK_KING = 'k';
	
	/** Character representations of the pieces that white pawns can promote to. */
	public static final char[] WH_PROMOTING_TYPES = {WH_KNIGHT, WH_BISHOP, WH_ROOK, WH_QUEEN};
	
	/** Character representations of the pieces that black pawns can promote to. */
	public static final char[] BK_PROMOTING_TYPES = {BK_KNIGHT, BK_BISHOP, BK_ROOK, BK_QUEEN};

	/**
	 * Returns true if the piece is white.
	 * 
	 * @param piece character corresponding to the piece to test
	 * @return <code>true</code> if this is a white piece; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isWhitePiece(char piece) {
		return piece == WH_KING || (piece == WH_ROOK) || (piece == WH_KNIGHT) || (piece == WH_BISHOP)
				|| (piece == WH_QUEEN) || (piece == WH_PAWN);
	}

	/**
	 * Returns true if the piece is black.
	 * 
	 * @param piece character corresponding to the piece to test
	 * @return <code>true</code> if this is a black piece; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isBlackPiece(char piece) {
		return piece == BK_KING || (piece == BK_ROOK) || (piece == BK_KNIGHT) || (piece == BK_BISHOP)
				|| (piece == BK_QUEEN) || (piece == BK_PAWN);
	}
	
	/**
	 * Returns true if the vector contains in any way the east 1 direction vector.
	 * 
	 * @param vector int to test
	 * @return <code>true</code> if this vector contains east 1 vector; <code>false</code>
	 *         otherwise.
	 */
	public static boolean containsEast1Direction(int vector) {
		return (vector == EAST_1) || (vector == NORTH_EAST_1) || (vector == SOUTH_EAST_1);
	}
	
	/**
	 * Returns true if the vector contains in any way the west 1 direction vector.
	 * 
	 * @param vector int to test
	 * @return <code>true</code> if this vector contains west 1 vector; <code>false</code>
	 *         otherwise.
	 */
	public static boolean containsWest1Direction(int vector) {
		return (vector == WEST_1) || (vector == NORTH_WEST_1) || (vector == SOUTH_WEST_1);
	}
	
	/**
	 * Returns true if the vector contains in any way the east 2 direction vector.
	 * 
	 * @param vector int to test
	 * @return <code>true</code> if this vector contains east 2 vector; <code>false</code>
	 *         otherwise.
	 */
	public static boolean containsEast2Direction(int vector) {
		return (vector == EAST_2) || (vector == (NORTH_1 + EAST_2)) || (vector == (SOUTH_1 + EAST_2));
	}
	
	/**
	 * Returns true if the vector contains in any way the west 2 direction vector.
	 * 
	 * @param vector int to test
	 * @return <code>true</code> if this vector contains west 2 vector; <code>false</code>
	 *         otherwise.
	 */
	public static boolean containsWest2Direction(int vector) {
		return (vector == WEST_2) || (vector == (NORTH_1 + WEST_2)) || (vector == (SOUTH_1 + WEST_2));
	}

}
