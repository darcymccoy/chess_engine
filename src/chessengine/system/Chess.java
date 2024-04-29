package chessengine.system;

/**
 * The <code>Chess</code> class contains chess specific
 * information. For example, the integer value of squares and
 * methods to determine the color of a piece.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public final class Chess {
	
	/**
	 * Prevents instantiation of this class.
	 */
	private Chess() {	
	}
	
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
	
	/** Direction vector for north 1 square from white's perspective of the board. */
	public static final int NORTH_1 = -8;

	/** Direction vector for north 2 squares from white's perspective of the board. */
	public static final int NORTH_2 = NORTH_1 + NORTH_1;

	/** Direction vector for north east 1 square from white's perspective of the board. */
	public static final int NORTH_1_EAST_1 = -7;
	
	/** Direction vector for east 1 square from white's perspective of the board. */
	public static final int EAST_1 = 1;
	
	/** Direction vector for east 2 squares from white's perspective of the board. */
	public static final int EAST_2 = EAST_1 + EAST_1;
	
	/** Direction vector for east 3 squares from white's perspective of the board. */
	public static final int EAST_3 = EAST_1 + EAST_1 + EAST_1;
	
	/** Direction vector for south east 1 square from white's perspective of the board. */
	public static final int SOUTH_1_EAST_1 = 9;
	
	/** Direction vector for south 1 square from white's perspective of the board. */
	public static final int SOUTH_1 = 8;
	
	/** Direction vector for south 2 squares from white's perspective of the board. */
	public static final int SOUTH_2 = SOUTH_1 + SOUTH_1;
	
	/** Direction vector for south west 1 square from white's perspective of the board. */
	public static final int SOUTH_1_WEST_1 = 7;
	
	/** Direction vector for west 1 square from white's perspective of the board. */
	public static final int WEST_1 = -1;
	
	/** Direction vector for west 2 squares from white's perspective of the board. */
	public static final int WEST_2 = WEST_1 + WEST_1;
	
	/** Direction vector for west 3 squares from white's perspective of the board. */
	public static final int WEST_3 = WEST_1 + WEST_1 + WEST_1;
	
	/** Direction vector for west 4 squares from white's perspective of the board. */
	public static final int WEST_4 = WEST_1 + WEST_1 + WEST_1 + WEST_1;
	
	/** Direction vector for north west 1 square from white's perspective of the board. */
	public static final int NORTH_1_WEST_1 = -9;
	
	/** Character representations of an empty square. */
	public static final char EMPTY = '-';
	
	/** Character representations of the white pawn. */
	public static final char WH_PAWN = 'P';
	
	/** Character representations of the white pawn that can be captured en passant. */
	public static final char WH_PAWN_ENPASS = 'E';
	
	/** Character representations of the white knight. */
	public static final char WH_KNIGHT = 'N';
	
	/** Character representations of the white bishop. */
	public static final char WH_BISHOP = 'B';
	
	/** Character representations of the white rook. */
	public static final char WH_ROOK = 'R';
	
	/** Character representations of the white queen. */
	public static final char WH_QUEEN = 'Q';
	
	/** Character representations of the white king that does not have any castling rights. */
	public static final char WH_KING = 'K';
	
	/** Character representations of the white king that can castle on either side. */
	public static final char WH_KING_CASTLE_BOTH_SIDES = '5';
	
	/** Character representations of the white king that can only castle on the kingside. */
	public static final char WH_KING_CASTLE_KINGSIDE = '4';
	
	/** Character representations of the white king that can only castle on the queenside. */
	public static final char WH_KING_CASTLE_QUEENSIDE = '3';
	
	/** Character representations of the black pawn. */
	public static final char BK_PAWN = 'p';
	
	/** Character representations of the black pawn that can be captured en passant. */
	public static final char BK_PAWN_ENPASS = 'e';
	
	/** Character representations of the black knight. */
	public static final char BK_KNIGHT = 'n';
	
	/** Character representations of the black bishop. */
	public static final char BK_BISHOP = 'b';
	
	/** Character representations of the black rook. */
	public static final char BK_ROOK = 'r';
	
	/** Character representations of the black queen. */
	public static final char BK_QUEEN = 'q';
	
	/** Character representations of the black king that does not have any castling rights. */
	public static final char BK_KING = 'k';
	
	/** Character representations of the black king that can castle on either side. */
	public static final char BK_KING_CASTLE_BOTH_SIDES = '2';
	
	/** Character representations of the black king that can only castle on the kingside. */
	public static final char BK_KING_CASTLE_KINGSIDE = '1';
	
	/** Character representations of the black king that can only castle on the queenside. */
	public static final char BK_KING_CASTLE_QUEENSIDE = '0';
	
	/** Maximum number of moves a pawn can make. */
	public static final int MAX_PAWN_MOVES = 12;
	
	/** Maximum number of moves a piece can make in straight directions. */
	public static final int MAX_STRAIGHT_MOVES = 28;
	
	/** Maximum number of moves a piece can make in diagonal directions. */
	public static final int MAX_DIAGONAL_MOVES = 28;
	
	/** Maximum number of moves a king can make. */
	public static final int MAX_KING_MOVES = 8;
	
	/** Maximum number of moves a knight can make. */
	public static final int MAX_KNIGHT_MOVES = 8;
	
	/**
	 * Returns true if the piece is white.
	 * 
	 * @param piece character corresponding to this piece
	 * @return <code>true</code> if this is a white piece; <code>false</code> otherwise.
	 */
	public static boolean isWhitePiece(char piece) {
		return ((piece == WH_ROOK) || (piece == WH_KNIGHT) || (piece == WH_BISHOP) || (piece == WH_QUEEN) 
				|| (piece == WH_KING_CASTLE_BOTH_SIDES) || (piece == WH_KING_CASTLE_KINGSIDE)
				|| (piece == WH_KING_CASTLE_QUEENSIDE) || (piece == WH_KING) || (piece == WH_PAWN) || (piece == WH_PAWN_ENPASS));
	}
	
	/**
	 * Returns true if the piece is black.
	 * 
	 * @param piece character corresponding to this piece
	 * @return <code>true</code> if this is a black piece; <code>false</code> otherwise.
	 */
	public static boolean isBlackPiece(char piece) {
		return ((piece == BK_ROOK) || (piece == BK_KNIGHT) || (piece == BK_BISHOP) || (piece == BK_QUEEN) 
				|| (piece == BK_KING_CASTLE_BOTH_SIDES) || (piece == BK_KING_CASTLE_KINGSIDE)
				|| (piece == BK_KING_CASTLE_QUEENSIDE) || (piece == BK_KING) || (piece == BK_PAWN) || (piece == BK_PAWN_ENPASS));
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
		return (sqr + WEST_1) % 8 == 0;
	}

	/**
	 * Returns true if the square is on file G of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file G; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isFileGSqr(int sqr) {
		return (sqr + EAST_2) % 8 == 0;
	}

	/**
	 * Returns true if the square is on file H of the board.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square is on file H; <code>false</code>
	 *         otherwise.
	 */
	public static boolean isFileHSqr(int sqr) {
		return (sqr + EAST_1) % 8 == 0;
	}

}
