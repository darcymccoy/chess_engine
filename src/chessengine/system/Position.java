package chessengine.system;

import java.util.LinkedList;

/**
 * Stores the information for a single chess position and can assemble all the
 * legal moves that can be made in that position.
 *
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public class Position {

	/** Whether white is the color to play in the current position. */
	private boolean whiteToPlay;

	/** The board of the position stored as a 64 character <code>String</code>. */
	private String board;

	/**
	 * Default constructor (standard starting chess position, white is to play).
	 */
	public Position() {
		this(true,
				"rnbq2bnr" + "pppppppp" + "--------" + "--------" + "--------" + "--------" + "PPPPPPPP" + "RNBQ5BNR");
	}

	/**
	 * Parameterized constructor specifying the board and which color is to play.
	 *
	 * @param whiteToPlay boolean whether white is currently to play
	 * @param board       string 64 characters long where each character is a square
	 *                    on the board starting from A8 and moving left and down
	 */
	public Position(boolean whiteToPlay, String board) {
		this.whiteToPlay = whiteToPlay;
		this.board = board;
	}

	/**
	 * Copy constructor.
	 *
	 * @param otherPosition the <code>Position</code> to copy
	 */
	public Position(Position otherPosition) {
		this(otherPosition.whiteToPlay, otherPosition.board);
	}

	/**
	 * Returns a copy of this position.
	 *
	 * @return <code>Position</code> that is a copy of this
	 */
	@Override
	public Position clone() {
		return new Position(this);
	}

	/**
	 * Returns all legal moves that the color to move can make.
	 *
	 * @return <code>LinkedList</code> of the legal moves found in this position
	 * @throws NoLegalMovesException if the color to play has no legal moves in this
	 *                               <code>Position</code>
	 */
	public LinkedList<Move> findLegalMoves() throws NoLegalMovesException {
		LinkedList<Move> pseudoLegalMoves = findPseudoLegalMoves();
		LinkedList<Move> legalMoves = new LinkedList<>();

		for (Move move : pseudoLegalMoves) {
			if (!isSelfCheckMove(move) && !(move.isCastling() && isCheck())) {
				legalMoves.add(move);
			}
		}
		if (legalMoves.size() == 0) {
			throw new NoLegalMovesException("There are no legal moves for the current player in this position");
		} else {
			return legalMoves;
		}
	}

	/**
	 * Updates isWhiteToPlay and the board so that the move has been played.
	 * Accounts for castling, en passant and promotion.
	 *
	 * @param move the move to be made
	 */
	public void makeMove(Move move) {
		char pieceToPut = move.getPiece();

		if (move.isKingsideCastling()) {
			setSqr(getSqr(move.getEndSqr() + Chess.EAST_1), move.getEndSqr() + Chess.WEST_1);
			setSqr(Chess.EMPTY, move.getEndSqr() + Chess.EAST_1);
		} else if (move.isQueensideCastling()) {
			setSqr(getSqr(move.getEndSqr() + Chess.WEST_2), move.getEndSqr() + Chess.EAST_1);
			setSqr(Chess.EMPTY, move.getEndSqr() + Chess.WEST_2);
		} else if (move.isEnPassant(getSqr(move.getEndSqr()))) {

			if (whiteToPlay) {
				setSqr(Chess.EMPTY, move.getEndSqr() + Chess.SOUTH_1);
			} else {
				setSqr(Chess.EMPTY, move.getEndSqr() + Chess.NORTH_1);
			}

		} else if (move.isPromotion()) {
			pieceToPut = move.getPromoteTo();
		}
		if (move.isAllowsEnPassant(getSqr(move.getEndSqr() + Chess.EAST_1), getSqr(move.getEndSqr() + Chess.WEST_1))) {

			if (whiteToPlay) {
				pieceToPut = Chess.WH_PAWN_ENPASS;
			} else {
				pieceToPut = Chess.BK_PAWN_ENPASS;
			}
		}
		if ((move.getPiece() == Chess.WH_KING_CASTLE_BOTH_SIDES) || (move.getPiece() == Chess.WH_KING_CASTLE_KINGSIDE)
				|| (move.getPiece() == Chess.WH_KING_CASTLE_QUEENSIDE)) {
			// Updating white king for castling ability for king moves
			pieceToPut = Chess.WH_KING;
		} else if ((move.getPiece() == Chess.BK_KING_CASTLE_BOTH_SIDES)
				|| (move.getPiece() == Chess.BK_KING_CASTLE_KINGSIDE)
				|| (move.getPiece() == Chess.BK_KING_CASTLE_QUEENSIDE)) {
			// Updating black king for castling ability for king moves
			pieceToPut = Chess.BK_KING;
		} else {
			// Updating king castling ability for non-king moves
			updateKingCastlingAbility(move);
		}
		removeEnPassantAbility();
		setSqr(Chess.EMPTY, move.getStartSqr());
		setSqr(pieceToPut, move.getEndSqr());
		whiteToPlay = !whiteToPlay;
	}

	/**
	 * Returns true when the move is legal for this position. Can test impossible
	 * and pseudo legal moves.
	 *
	 * @param testMove the move to be tested
	 * @return <code>true</code> if the move is legal for this position;
	 *         <code>false</code> otherwise.
	 */
	public boolean isLegalMove(Move testMove) {
		if ((testMove.getStartSqr() <= Chess.H1_SQR) && (testMove.getEndSqr() <= Chess.H1_SQR)
				&& (testMove.getStartSqr() >= Chess.A8_SQR) && (testMove.getEndSqr() >= Chess.A8_SQR)) {
			LinkedList<Move> tempMoves = findPseudoLegalPieceMoves(testMove.getPiece(), testMove.getStartSqr());
			for (Move move : tempMoves) {
				if (move.equals(testMove)) {
					return !isSelfCheckMove(testMove) && !(testMove.isCastling() && isCheck());
				}
			}
		}
		return false;
	}

	/**
	 * Returns all pseudo legal moves and legal moves that the color to move can
	 * make. This can include illegal moves (such as self check moves, castling out
	 * of check).
	 *
	 * @return <code>LinkedList</code> the legal and pseudo legal moves in a
	 *         position
	 */
	public LinkedList<Move> findPseudoLegalMoves() {
		LinkedList<Move> pseudoLegalMoves = new LinkedList<>();

		for (int i = 0; i < board.length(); i++) {
			if (isEmptySqr(i) || isOtherColorAtSqr(i)) {
				continue;
			}
			pseudoLegalMoves.addAll(findPseudoLegalPieceMoves(getSqr(i), i));
		}
		return pseudoLegalMoves;
	}

	/**
	 * Returns the pseudo legal moves that this piece can make.
	 *
	 * @param piece    character representing the piece
	 * @param pieceSqr int index of the square the piece is on
	 * @return <code>LinkedList</code> the legal and pseudo legal moves for a single
	 *         piece
	 */
	public LinkedList<Move> findPseudoLegalPieceMoves(char piece, int pieceSqr) {
		switch (piece) {
		case Chess.WH_PAWN:
		case Chess.BK_PAWN:
		case Chess.BK_PAWN_ENPASS:
		case Chess.WH_PAWN_ENPASS:
			return findPawnMoves(pieceSqr);

		case Chess.WH_ROOK:
		case Chess.BK_ROOK:
			return findStraightMoves(pieceSqr);

		case Chess.WH_KNIGHT:
		case Chess.BK_KNIGHT:
			return findKnightMoves(pieceSqr);

		case Chess.WH_BISHOP:
		case Chess.BK_BISHOP:
			return findDiagonalMoves(pieceSqr);

		case Chess.WH_QUEEN:
		case Chess.BK_QUEEN:
			return findQueenMoves(pieceSqr);

		case Chess.BK_KING_CASTLE_QUEENSIDE:
		case Chess.BK_KING_CASTLE_KINGSIDE:
		case Chess.BK_KING_CASTLE_BOTH_SIDES:
		case Chess.WH_KING_CASTLE_QUEENSIDE:
		case Chess.WH_KING_CASTLE_KINGSIDE:
		case Chess.WH_KING_CASTLE_BOTH_SIDES:
		case Chess.BK_KING:
		case Chess.WH_KING:
			return findKingMoves(pieceSqr);

		default:
			return new LinkedList<>();
		}
	}

	/**
	 * Returns the moves that the knight can make.
	 *
	 * @param knightSqr int index of the square the knight is on
	 * @return <code>LinkedList</code> the legal and pseudo legal moves for this
	 *         knight
	 */
	public LinkedList<Move> findKnightMoves(int knightSqr) {
		LinkedList<Move> knightMoves = new LinkedList<>();
		int[] testVectors = { Chess.NORTH_2 + Chess.EAST_1, Chess.NORTH_1 + Chess.EAST_2, Chess.SOUTH_1 + Chess.EAST_2,
				Chess.SOUTH_2 + Chess.EAST_1, Chess.SOUTH_2 + Chess.WEST_1, Chess.SOUTH_1 + Chess.WEST_2,
				Chess.NORTH_1 + Chess.WEST_2, Chess.NORTH_2 + Chess.WEST_1 };

		if (Chess.isRank8Sqr(knightSqr)) {// Assessing rank square
			testVectors[7] = 0;
			testVectors[6] = 0;
			testVectors[1] = 0;
			testVectors[0] = 0;
		} else if (Chess.isRank7Sqr(knightSqr)) {
			testVectors[7] = 0;
			testVectors[0] = 0;
		} else if (Chess.isRank1Sqr(knightSqr)) {
			testVectors[5] = 0;
			testVectors[4] = 0;
			testVectors[3] = 0;
			testVectors[2] = 0;
		} else if (Chess.isRank2Sqr(knightSqr)) {
			testVectors[4] = 0;
			testVectors[3] = 0;
		}

		if (Chess.isFileHSqr(knightSqr)) {// Assessing file square
			testVectors[3] = 0;
			testVectors[2] = 0;
			testVectors[1] = 0;
			testVectors[0] = 0;
		} else if (Chess.isFileGSqr(knightSqr)) {
			testVectors[2] = 0;
			testVectors[1] = 0;
		} else if (Chess.isFileASqr(knightSqr)) {
			testVectors[7] = 0;
			testVectors[6] = 0;
			testVectors[5] = 0;
			testVectors[4] = 0;
		} else if (Chess.isFileBSqr(knightSqr)) {
			testVectors[6] = 0;
			testVectors[5] = 0;
		}
		for (int testVector : testVectors) {
			if ((testVector != 0)
					&& (isOtherColorAtSqr(knightSqr + testVector) || isEmptySqr(knightSqr + testVector))) {
				knightMoves.add(new Move(getSqr(knightSqr), knightSqr, knightSqr + testVector));
			}
		}
		return knightMoves;
	}

	/**
	 * Returns the moves that the king can make.
	 *
	 * @param kingSqr int index of the square the king is on
	 * @return <code>LinkedList</code> the legal and pseudo legal moves for this
	 *         king
	 */
	public LinkedList<Move> findKingMoves(int kingSqr) {
		char piece = getSqr(kingSqr);
		LinkedList<Move> kingMoves = new LinkedList<>();
		int[] testVectors = { Chess.NORTH_1, Chess.NORTH_1_EAST_1, Chess.EAST_1, Chess.SOUTH_1_EAST_1, Chess.SOUTH_1,
				Chess.SOUTH_1_WEST_1, Chess.WEST_1, Chess.NORTH_1_WEST_1 };

		if (Chess.isFileHSqr(kingSqr)) {
			testVectors[1] = 0;
			testVectors[2] = 0;
			testVectors[3] = 0;
		} else if (Chess.isFileASqr(kingSqr)) {
			testVectors[5] = 0;
			testVectors[6] = 0;
			testVectors[7] = 0;
		}
		if (Chess.isRank1Sqr(kingSqr)) {
			testVectors[3] = 0;
			testVectors[4] = 0;
			testVectors[5] = 0;
		} else if (Chess.isRank8Sqr(kingSqr)) {
			testVectors[0] = 0;
			testVectors[1] = 0;
			testVectors[7] = 0;
		}
		for (int testVector : testVectors) {
			if ((testVector != 0) && (isOtherColorAtSqr(kingSqr + testVector) || isEmptySqr(kingSqr + testVector))) {
				kingMoves.add(new Move(piece, kingSqr, kingSqr + testVector));
			}
		}

		// Kingside castling
		if (((((piece == Chess.WH_KING_CASTLE_BOTH_SIDES) || (piece == Chess.WH_KING_CASTLE_KINGSIDE))
				&& (getSqr(kingSqr + Chess.EAST_3) == Chess.WH_ROOK))
				|| (((piece == Chess.BK_KING_CASTLE_BOTH_SIDES) || (piece == Chess.BK_KING_CASTLE_KINGSIDE))
						&& (getSqr(kingSqr + Chess.EAST_3) == Chess.BK_ROOK)))
				&& (isEmptySqr(kingSqr + Chess.EAST_1)) && (isEmptySqr(kingSqr + Chess.EAST_2))) {
			kingMoves.add(new Move(piece, kingSqr, kingSqr + Chess.EAST_2));
		}
		// Queenside castling
		if (((((piece == Chess.WH_KING_CASTLE_BOTH_SIDES) || (piece == Chess.WH_KING_CASTLE_QUEENSIDE))
				&& (getSqr(kingSqr + Chess.WEST_4) == Chess.WH_ROOK))
				|| (((piece == Chess.BK_KING_CASTLE_BOTH_SIDES) || (piece == Chess.BK_KING_CASTLE_QUEENSIDE))
						&& (getSqr(kingSqr + Chess.WEST_4) == Chess.BK_ROOK)))
				&& (isEmptySqr(kingSqr + Chess.WEST_1)) && (isEmptySqr(kingSqr + Chess.WEST_2))
				&& (isEmptySqr(kingSqr + Chess.WEST_3))) {
			kingMoves.add(new Move(piece, kingSqr, kingSqr + Chess.WEST_2));
		}
		return kingMoves;
	}

	/**
	 * Returns the moves that the queen can make
	 *
	 * @param queenSqr int index of the square the queen is on
	 * @return <code>LinkedList</code> the legal and pseudo legal moves for this
	 *         queen
	 */
	public LinkedList<Move> findQueenMoves(int queenSqr) {
		LinkedList<Move> queenMoves = new LinkedList<>();
		queenMoves.addAll(findStraightMoves(queenSqr));
		queenMoves.addAll(findDiagonalMoves(queenSqr));
		return queenMoves;
	}

	/**
	 * Returns the moves that the pawn can make.
	 *
	 * @param pawnSqr int index of the square the pawn is on
	 * @return <code>LinkedList</code> the legal and pseudo legal moves for this
	 *         pawn
	 */
	public LinkedList<Move> findPawnMoves(int pawnSqr) {
		LinkedList<Move> pawnMoves = new LinkedList<>();
		int testVector;
		if (whiteToPlay) {
			testVector = Chess.NORTH_1;
		} else {
			testVector = Chess.SOUTH_1;
		}

		pawnMoves.addAll(findStraightPawnMoves(pawnSqr, testVector));
		pawnMoves.addAll(findDiagonalPawnMoves(pawnSqr, testVector));
		pawnMoves = addPromoteTypes(pawnMoves);
		return pawnMoves;
	}

	/**
	 * Returns the pseudo legal moves that the pawn can capture on (diagonally).
	 *
	 * @param pawnSqr    int index of the square the pawn is on
	 * @param testVector int direction vector of the pawn's movement
	 * @return <code>LinkedList</code> the moves where the pawn can capture
	 */
	private LinkedList<Move> findDiagonalPawnMoves(int pawnSqr, int testVector) {
		LinkedList<Move> diagonalPawnMoves = new LinkedList<>();
		if (!Chess.isFileHSqr(pawnSqr)) {
			if ((isOtherColorAtSqr(pawnSqr + Chess.EAST_1 + testVector))
					|| ((getSqr(pawnSqr + Chess.EAST_1) == Chess.BK_PAWN_ENPASS) && whiteToPlay)
					|| ((getSqr(pawnSqr + Chess.EAST_1) == Chess.WH_PAWN_ENPASS) && !whiteToPlay)) {
				diagonalPawnMoves.add(new Move(getSqr(pawnSqr), pawnSqr, pawnSqr + Chess.EAST_1 + testVector));
			}
		}
		if (!Chess.isFileASqr(pawnSqr)) {
			if ((isOtherColorAtSqr(pawnSqr + Chess.WEST_1 + testVector))
					|| ((getSqr(pawnSqr + Chess.WEST_1) == Chess.BK_PAWN_ENPASS) && whiteToPlay)
					|| ((getSqr(pawnSqr + Chess.WEST_1) == Chess.WH_PAWN_ENPASS) && !whiteToPlay)) {
				diagonalPawnMoves.add(new Move(getSqr(pawnSqr), pawnSqr, pawnSqr + Chess.WEST_1 + testVector));
			}
		}
		return diagonalPawnMoves;
	}

	/**
	 * Returns the pseudo legal moves that the pawn can make for the 2 squares ahead
	 * of it.
	 *
	 * @param pawnSqr    int index of the square the pawn is on
	 * @param testVector int direction vector of the pawn's movement
	 * @return <code>LinkedList</code> the moves that the pawn can make for the 2
	 *         squares ahead of it
	 */
	private LinkedList<Move> findStraightPawnMoves(int pawnSqr, int testVector) {
		LinkedList<Move> straightPawnMoves = new LinkedList<>();
		for (int testSqr = (pawnSqr + testVector), i = 0; ((testSqr <= Chess.H1_SQR) && (testSqr >= Chess.A8_SQR))
				&& i < 2; testSqr += testVector, i++) {
			if (isEmptySqr(testSqr)) {
				straightPawnMoves.add(new Move(getSqr(pawnSqr), pawnSqr, testSqr));
			} else {
				break;
			}
		}
		return straightPawnMoves;
	}

	/**
	 * Returns the moves with the piece that is being promoted to set for each
	 * promotion move. This will clone each promotion move 4 times and set the
	 * corresponding promotion piece for each of those cloned moves.
	 *
	 * @param pawnMoves LinkedList of moves to have the promotion pieces moves added
	 * @return <code>LinkedList</code> with all the non promotion moves and the set
	 *         promotion moves
	 */
	private LinkedList<Move> addPromoteTypes(LinkedList<Move> pawnMoves) {
		LinkedList<Move> newMoves = new LinkedList<>();
		int numberOfMoves = 0;
		char[] promoteTypes;
		if (whiteToPlay) {
			promoteTypes = Chess.WH_PROMOTING_TYPES;
		} else {
			promoteTypes = Chess.BK_PROMOTING_TYPES;
		}

		for (int i = 0; i < pawnMoves.size(); i++, numberOfMoves++) {
			newMoves.add(pawnMoves.get(i));
			if (newMoves.get(i).isPromotion()) {
				for (char promoteType : promoteTypes) {
					newMoves.set(numberOfMoves, pawnMoves.get(i).clone());
					newMoves.get(numberOfMoves++).setPromoteTo(promoteType);
				}
			}
		}
		return newMoves;
	}

	/**
	 * Returns the moves along straight directions. This finds the legal and pseudo
	 * legal moves that a rook can make, or the moves for a queen's straight
	 * directions.
	 *
	 * @param pieceSqr int index of the square the piece is on
	 * @return <code>LinkedList</code> the legal and pseudo legal moves in straight
	 *         directions
	 */
	public LinkedList<Move> findStraightMoves(int pieceSqr) {
		LinkedList<Move> straightMoves = new LinkedList<>();
		int[] testVectors = { Chess.NORTH_1, Chess.EAST_1, Chess.SOUTH_1, Chess.WEST_1 };

		for (int testVector : testVectors) {
			for (int testSqr = (pieceSqr + testVector); (testSqr <= Chess.H1_SQR)
					&& (testSqr >= Chess.A8_SQR); testSqr += testVector) {
				if ((Chess.containsEastDirection(testVector) && Chess.isFileHSqr(testSqr + Chess.WEST_1))
						|| (Chess.containsWestDirection(testVector) && Chess.isFileASqr(testSqr + Chess.EAST_1))) {
					break;// If the test square has passed the east or west edge of the board
				} else if (isEmptySqr(testSqr)) {
					straightMoves.add(new Move(getSqr(pieceSqr), pieceSqr, testSqr));
				} else if (isOtherColorAtSqr(testSqr)) {
					straightMoves.add(new Move(getSqr(pieceSqr), pieceSqr, testSqr));
					break;
				} else {
					break;
				}
			}
		}
		return straightMoves;
	}

	/**
	 * Returns the moves along diagonal directions. This finds the legal and pseudo
	 * legal moves that a bishop can make, or the moves for a queen's diagonal
	 * directions.
	 *
	 * @param pieceSqr int index of the square the piece is on
	 * @return <code>LinkedList</code> the legal and pseudo legal moves in diagonal
	 *         directions
	 */
	public LinkedList<Move> findDiagonalMoves(int pieceSqr) {
		LinkedList<Move> diagonalMoves = new LinkedList<>();
		int[] testVectors = { Chess.NORTH_1_EAST_1, Chess.SOUTH_1_EAST_1, Chess.SOUTH_1_WEST_1, Chess.NORTH_1_WEST_1 };

		for (int testVector : testVectors) {
			for (int testSqr = (pieceSqr + testVector); (testSqr <= Chess.H1_SQR)
					&& (testSqr >= Chess.A8_SQR); testSqr += testVector) {
				if ((Chess.containsEastDirection(testVector) && Chess.isFileHSqr(testSqr + Chess.WEST_1))
						|| (Chess.containsWestDirection(testVector) && Chess.isFileASqr(testSqr + Chess.EAST_1))) {
					break;// If the test square has passed the east or west edge of the board
				} else if (isEmptySqr(testSqr)) {
					diagonalMoves.add(new Move(getSqr(pieceSqr), pieceSqr, testSqr));
				} else if (isOtherColorAtSqr(testSqr)) {
					diagonalMoves.add(new Move(getSqr(pieceSqr), pieceSqr, testSqr));
					break;
				} else {
					break;
				}
			}
		}
		return diagonalMoves;
	}

	/**
	 * Returns true if the color to move is in check.
	 *
	 * @return <code>true</code> if this position has the color to move's king
	 *         attacked; <code>false</code> otherwise.
	 */
	public boolean isCheck() {
		return isAttackedSqr(findKingSqr(whiteToPlay), !whiteToPlay);
	}

	/**
	 * Returns true if the move puts the king (of the color who makes that move)
	 * into check or castles that king through check.
	 *
	 * @param move the move to be tested
	 * @return <code>true</code> if this move puts the color that made it into
	 *         check; <code>false</code> otherwise.
	 */
	public boolean isSelfCheckMove(Move move) {
		Position tempPosition = clone();
		tempPosition.makeMove(move);
		return tempPosition.isAttackedSqr(tempPosition.findKingSqr(!tempPosition.whiteToPlay), tempPosition.whiteToPlay)
				|| (move.isCastling() && tempPosition.isAttackedSqr(((move.getStartSqr()) + (move.getEndSqr())) / 2,
						tempPosition.whiteToPlay));
	}

	/**
	 * Returns true if the square is attacked by a piece of the corresponding color.
	 *
	 * @param sqr              int index of the square to be tested
	 * @param whiteIsAttacking boolean whether white is the color to be tested on
	 *                         it's attacking of the square
	 * @return <code>true</code> if this square is attacked by the chosen color;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAttackedSqr(int sqr, boolean whiteIsAttacking) {
		Position tempPosition = clone();
		if ((Chess.isWhitePiece(getSqr(sqr)) || (isEmptySqr(sqr))) && whiteIsAttacking) {
			tempPosition.setSqr(Chess.BK_QUEEN, sqr);
		} else if ((Chess.isBlackPiece(getSqr(sqr)) || (isEmptySqr(sqr))) && !whiteIsAttacking) {
			tempPosition.setSqr(Chess.WH_QUEEN, sqr);
		}

		if (whiteToPlay != whiteIsAttacking) {
			tempPosition.whiteToPlay = whiteIsAttacking;
		}

		LinkedList<Move> tempMoves = tempPosition.findPseudoLegalMoves();
		for (Move move : tempMoves) {
			if (move.getEndSqr() == sqr) {
				return true;
			}
		}
		return (getSqr(sqr) == Chess.WH_PAWN_ENPASS) || (getSqr(sqr) == Chess.BK_PAWN_ENPASS);
	}

	/**
	 * Returns one of the kings' squares.
	 *
	 * @param findWhiteKing <code>boolean</code> whether white is the color of the
	 *                      king to be found
	 * @return int value where the king is on the board
	 */
	public int findKingSqr(boolean findWhiteKing) {
		for (int i = 0; i < board.length(); i++) {
			if ((findWhiteKing && Chess.isWhiteKing(getSqr(i))) || (!findWhiteKing && Chess.isBlackKing(getSqr(i)))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns true if the piece at this square is the opposite color of the color
	 * who is to play.
	 *
	 * @param sqr int value of the square
	 * @return <code>true</code> if there is a piece of the opposite color at this
	 *         square; <code>false</code> otherwise.
	 */
	public boolean isOtherColorAtSqr(int sqr) {
		if (whiteToPlay) {
			return Chess.isBlackPiece(getSqr(sqr));
		} else {
			return Chess.isWhitePiece(getSqr(sqr));
		}
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
	 * Updates this square on the board to either empty or to the piece that is
	 * passed.
	 *
	 * @param charToPut character representing the piece or an empty square
	 * @param sqr       int value of the square to be updated
	 */
	public void setSqr(char charToPut, int sqr) {
		board = board.substring(0, sqr) + charToPut + board.substring(sqr + 1);
	}

	/**
	 * Updates the king's castling ability for non king moves.
	 *
	 * @param move the move that the (non king) piece is making
	 */
	public void updateKingCastlingAbility(Move move) {
		// Updating white king for castling ability
		if ((getSqr(Chess.E1_SQR) == Chess.WH_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == Chess.H1_SQR) || (move.getEndSqr() == Chess.H1_SQR))) {
			setSqr(Chess.WH_KING_CASTLE_QUEENSIDE, Chess.E1_SQR);
		} else if ((getSqr(Chess.E1_SQR) == Chess.WH_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == Chess.A1_SQR) || (move.getEndSqr() == Chess.A1_SQR))) {
			setSqr(Chess.WH_KING_CASTLE_KINGSIDE, Chess.E1_SQR);
		} else if (((getSqr(Chess.E1_SQR) == Chess.WH_KING_CASTLE_KINGSIDE)
				&& ((move.getStartSqr() == Chess.H1_SQR) || (move.getEndSqr() == Chess.H1_SQR)))
				|| ((getSqr(Chess.E1_SQR) == Chess.WH_KING_CASTLE_QUEENSIDE)
						&& (((move.getStartSqr()) == Chess.A1_SQR) || (move.getEndSqr() == Chess.A1_SQR)))) {
			setSqr(Chess.WH_KING, Chess.E1_SQR);
		}
		// Updating black king for castling ability
		if ((getSqr(Chess.E8_SQR) == Chess.BK_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == Chess.H8_SQR) || (move.getEndSqr() == Chess.H8_SQR))) {
			setSqr(Chess.BK_KING_CASTLE_QUEENSIDE, Chess.E8_SQR);
		} else if ((getSqr(Chess.E8_SQR) == Chess.BK_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == Chess.A8_SQR) || (move.getEndSqr() == Chess.A8_SQR))) {
			setSqr(Chess.BK_KING_CASTLE_KINGSIDE, Chess.E8_SQR);
		} else if (((getSqr(Chess.E8_SQR) == Chess.BK_KING_CASTLE_KINGSIDE)
				&& ((move.getStartSqr() == Chess.H8_SQR) || (move.getEndSqr() == Chess.H8_SQR)))
				|| ((getSqr(Chess.E8_SQR) == Chess.BK_KING_CASTLE_QUEENSIDE)
						&& ((move.getStartSqr() == Chess.A8_SQR) || (move.getEndSqr() == Chess.A8_SQR)))) {
			setSqr(Chess.BK_KING, Chess.E8_SQR);
		}
	}

	/**
	 * Updates the board so that pawns that could have been captured en passant
	 * become regular pawns.
	 */
	public void removeEnPassantAbility() {
		for (int i = 0; i < board.length(); i++) {
			if (getSqr(i) == Chess.WH_PAWN_ENPASS) {
				setSqr(Chess.WH_PAWN, i);
			} else if (getSqr(i) == Chess.BK_PAWN_ENPASS) {
				setSqr(Chess.BK_PAWN, i);
			}
		}
	}

	/**
	 * Returns the contents of a square.
	 *
	 * @param sqr int value the square
	 * @return character contents of the square
	 */
	public char getSqr(int sqr) {
		return board.charAt(sqr);
	}

	/**
	 * Returns a string with the player who is to move and the board laid out in a
	 * 8x8 grid.
	 *
	 * @return String with the color who is to move and representation of the board
	 */
	@Override
	public String toString() {
		String printPosition = "";
		if (whiteToPlay) {
			printPosition = "White to move:\n";
		} else {
			printPosition = "Black to move:\n";
		}

		for (int i = 0; i < board.length(); i++) {
			printPosition += getSqr(i) + " ";
			if (Chess.isFileHSqr(i)) {
				printPosition += "\n";
			}
		}
		return printPosition;
	}

	/**
	 * @return <code>true</code> if white is to play; <code>false</code> otherwise.
	 */
	public boolean isWhiteToPlay() {
		return whiteToPlay;
	}

	/**
	 * @param whiteToPlay <code>boolean</code> whether to set white as the color to
	 *                    play
	 */
	public void setWhiteToPlay(boolean whiteToPlay) {
		this.whiteToPlay = whiteToPlay;
	}

	/**
	 * @return String, the board as 64 characters starting from square A8
	 */
	public String getBoard() {
		return board;
	}

	/**
	 * @param board <code>String</code>, the new board
	 */
	public void setBoard(String board) {
		this.board = board;
	}

}
