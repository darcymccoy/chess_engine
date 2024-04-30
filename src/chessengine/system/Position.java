package chessengine.system;

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
	 * Copy constructor to copy a <code>Position</code>.
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
	 * Returns an array of all legal moves that the color to move can make
	 * 
	 * @return Move[] the legal moves found in this position
	 * @throws NoLegalMovesException if the color to play has no legal moves in this
	 *                               <code>Position</code>
	 */
	public Move[] findLegalMoves() throws NoLegalMovesException {
		Move[] pseudoLegalMoves = findPseudoLegalMoves();
		Move[] legalMoves = new Move[pseudoLegalMoves.length];
		int numberOfLegalMoves = 0;

		for (int i = 0; i < pseudoLegalMoves.length; i++) {
			if (!isSelfCheckMove(pseudoLegalMoves[i]) && (!pseudoLegalMoves[i].isCastling() || !isCheck())) {
				legalMoves[numberOfLegalMoves++] = pseudoLegalMoves[i];
			}
		}
		legalMoves = Move.removeNullElements(legalMoves, numberOfLegalMoves);
		if (!(legalMoves.length == 0)) {
			return legalMoves;
		} else {
			throw new NoLegalMovesException("There are no legal moves for the current player in this position");
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
			updateSqr(atSqr(move.getEndSqr() + Chess.EAST_1), move.getEndSqr() + Chess.WEST_1);
			updateSqr(Chess.EMPTY, move.getEndSqr() + Chess.EAST_1);
		} else if (move.isQueensideCastling()) {
			updateSqr(atSqr(move.getEndSqr() + Chess.WEST_2), move.getEndSqr() + Chess.EAST_1);
			updateSqr(Chess.EMPTY, move.getEndSqr() + Chess.WEST_2);
		} else if (move.isEnPassant(atSqr(move.getEndSqr() + Chess.NORTH_1), atSqr(move.getEndSqr() + Chess.SOUTH_1))) {

			if (whiteToPlay)
				updateSqr(Chess.EMPTY, move.getEndSqr() + Chess.SOUTH_1);
			else
				updateSqr(Chess.EMPTY, move.getEndSqr() + Chess.NORTH_1);

		} else if (move.isAllowsEnPassant(atSqr(move.getEndSqr() + Chess.EAST_1),
				atSqr(move.getEndSqr() + Chess.WEST_1))) {

			if (whiteToPlay)
				pieceToPut = Chess.WH_PAWN_ENPASS;
			else
				pieceToPut = Chess.BK_PAWN_ENPASS;

		} else if (move.isPromotion()) {

			if (whiteToPlay)
				pieceToPut = Chess.WH_QUEEN;
			else
				pieceToPut = Chess.BK_QUEEN;
		}

		if ((atSqr(move.getStartSqr()) == Chess.WH_KING_CASTLE_BOTH_SIDES)
				|| (atSqr(move.getStartSqr()) == Chess.WH_KING_CASTLE_KINGSIDE)
				|| (atSqr(move.getStartSqr()) == Chess.WH_KING_CASTLE_QUEENSIDE)) {
			// Updating white king for castling ability for king moves
			pieceToPut = Chess.WH_KING;
		} else if ((atSqr(move.getStartSqr()) == Chess.BK_KING_CASTLE_BOTH_SIDES)
				|| (atSqr(move.getStartSqr()) == Chess.BK_KING_CASTLE_KINGSIDE)
				|| (atSqr(move.getStartSqr()) == Chess.BK_KING_CASTLE_QUEENSIDE)) {
			// Updating black king for castling ability for king moves
			pieceToPut = Chess.BK_KING;
		} else {
			// Updating king castling ability for non-king moves
			updateKingCastlingAbility(move);
		}
		removeEnPassantAbility();
		updateSqr(Chess.EMPTY, move.getStartSqr());
		updateSqr(pieceToPut, move.getEndSqr());
		whiteToPlay = !whiteToPlay;
	}

	/**
	 * Returns true when the move is legal for this position. Can test impossible
	 * and pseudo legal moves.
	 * 
	 * @param move the move to be tested
	 * @return <code>true</code> if the move is legal for this position;
	 *         <code>false</code> otherwise.
	 */
	public boolean isLegalMove(Move move) {
		if (((move.getStartSqr()) <= Chess.H1_SQR) && ((move.getEndSqr()) <= Chess.H1_SQR)
				&& ((move.getStartSqr()) >= Chess.A8_SQR) && ((move.getEndSqr()) >= Chess.A8_SQR)) {
			Move[] tempMoves = findPseudoLegalPieceMoves(atSqr(move.getStartSqr()), move.getStartSqr());
			for (int i = 0; i < tempMoves.length; i++) {
				if (move.equals(tempMoves[i]))
					return !isSelfCheckMove(move) && (!move.isCastling() || !isCheck());
			}
		}
		return false;
	}

	/**
	 * Returns an array of all pseudo legal moves and legal moves that the color to
	 * move can make. This can include illegal moves (such as self check moves,
	 * castling out of check).
	 * 
	 * @return Move[] the legal and pseudo legal moves in a position
	 */
	public Move[] findPseudoLegalMoves() {
		Move[] pseudoLegalMoves = new Move[218];
		Move[] pseudoLegalPieceMoves = null;
		int numberOfPseudoLegalMoves = 0;

		for (int i = 0; i < board.length(); i++) {
			if ((isEmptySqr(i)) || (isOtherColorAtSqr(i)))
				continue;

			pseudoLegalPieceMoves = findPseudoLegalPieceMoves(atSqr(i), i);
			for (int j = 0; j < pseudoLegalPieceMoves.length; j++) {
				pseudoLegalMoves[numberOfPseudoLegalMoves++] = pseudoLegalPieceMoves[j];
			}
		}
		pseudoLegalMoves = Move.removeNullElements(pseudoLegalMoves, numberOfPseudoLegalMoves);
		return pseudoLegalMoves;
	}

	/**
	 * Returns an array of pseudo legal moves that the piece can make.
	 * 
	 * @param piece    character representing the piece
	 * @param pieceSqr int value where the piece is on the board
	 * @return Move[] the legal and pseudo legal moves for a single piece
	 */
	public Move[] findPseudoLegalPieceMoves(char piece, int pieceSqr) {
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
			return new Move[0];
		}
	}

	/**
	 * Returns an array of moves that the knight can make.
	 * 
	 * @param knightSqr int value where the piece is on the board
	 * @return Move[] the legal and pseudo legal moves for this knight
	 */
	public Move[] findKnightMoves(int knightSqr) {
		Move[] knightMoves = new Move[Chess.MAX_KNIGHT_MOVES];
		int numberOfMoves = 0;
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
				knightMoves[numberOfMoves++] = new Move(atSqr(knightSqr), knightSqr, knightSqr + testVector);
			}
		}
		knightMoves = Move.removeNullElements(knightMoves, numberOfMoves);
		return knightMoves;
	}

	/**
	 * Returns an array of moves that the king can make
	 * 
	 * @param kingSqr int value where the piece is on the board
	 * @return Move[] the legal and pseudo legal moves for this king
	 */
	public Move[] findKingMoves(int kingSqr) {
		char piece = atSqr(kingSqr);
		Move[] kingMoves = new Move[Chess.MAX_KING_MOVES];
		int numberOfMoves = 0;
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
				kingMoves[numberOfMoves++] = new Move(atSqr(kingSqr), kingSqr, kingSqr + testVector);
			}
		}

		// Kingside castling
		if (((((piece == Chess.WH_KING_CASTLE_BOTH_SIDES) || (piece == Chess.WH_KING_CASTLE_KINGSIDE))
				&& (atSqr(kingSqr + Chess.EAST_3) == Chess.WH_ROOK))
				|| (((piece == Chess.BK_KING_CASTLE_BOTH_SIDES) || (piece == Chess.BK_KING_CASTLE_KINGSIDE))
						&& (atSqr(kingSqr + Chess.EAST_3) == Chess.BK_ROOK)))
				&& (isEmptySqr(kingSqr + Chess.EAST_1)) && (isEmptySqr(kingSqr + Chess.EAST_2))) {
			kingMoves[numberOfMoves++] = new Move(atSqr(kingSqr), kingSqr, kingSqr + Chess.EAST_2);
		}
		// Queenside castling
		if (((((piece == Chess.WH_KING_CASTLE_BOTH_SIDES) || (piece == Chess.WH_KING_CASTLE_QUEENSIDE))
				&& (atSqr(kingSqr + Chess.WEST_4) == Chess.WH_ROOK))
				|| (((piece == Chess.BK_KING_CASTLE_BOTH_SIDES) || (piece == Chess.BK_KING_CASTLE_QUEENSIDE))
						&& (atSqr(kingSqr + Chess.WEST_4) == Chess.BK_ROOK)))
				&& (isEmptySqr(kingSqr + Chess.WEST_1)) && (isEmptySqr(kingSqr + Chess.WEST_2))
				&& (isEmptySqr(kingSqr + Chess.WEST_3))) {
			kingMoves[numberOfMoves++] = new Move(atSqr(kingSqr), kingSqr, kingSqr + Chess.WEST_2);
		}
		kingMoves = Move.removeNullElements(kingMoves, numberOfMoves);
		return kingMoves;
	}

	/**
	 * Returns an array of moves that the queen can make
	 * 
	 * @param queenSqr int value where the piece is on the board
	 * @return Move[] the legal and pseudo legal moves for this queen
	 */
	public Move[] findQueenMoves(int queenSqr) {
		Move[] tempStraightMoves = findStraightMoves(queenSqr);
		Move[] tempDiagonalMoves = findDiagonalMoves(queenSqr);
		Move[] queenMoves = new Move[tempStraightMoves.length + tempDiagonalMoves.length];

		for (int i = 0; i < tempStraightMoves.length; i++) {
			queenMoves[i] = tempStraightMoves[i];
		}
		for (int i = 0; i < tempDiagonalMoves.length; i++) {
			queenMoves[i + tempStraightMoves.length] = tempDiagonalMoves[i];
		}
		return queenMoves;
	}

	/**
	 * Returns an array of moves that the pawn can make.
	 * 
	 * @param pawnSqr int value where the piece is on the board
	 * @return Move[] the legal and pseudo legal moves for this pawn
	 */
	public Move[] findPawnMoves(int pawnSqr) {
		Move[] pawnMoves = new Move[Chess.MAX_PAWN_MOVES];
		int numberOfMoves = 0;

		if (whiteToPlay) {
			// White pawns
			if (isEmptySqr(pawnSqr + Chess.NORTH_1)) {
				pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.NORTH_1);
				if ((Chess.isRank2Sqr(pawnSqr)) && (isEmptySqr(pawnSqr + Chess.NORTH_2))) {
					pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.NORTH_2);
				}
			}
			if (Chess.isFileASqr(pawnSqr)) {
				if ((isOtherColorAtSqr(pawnSqr + Chess.NORTH_1_EAST_1))
						|| (atSqr(pawnSqr + Chess.EAST_1) == Chess.BK_PAWN_ENPASS)) {
					pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.NORTH_1_EAST_1);
				}
			} else if (Chess.isFileHSqr(pawnSqr)) {
				if ((isOtherColorAtSqr(pawnSqr + Chess.NORTH_1_WEST_1))
						|| (atSqr(pawnSqr + Chess.WEST_1) == Chess.BK_PAWN_ENPASS)) {
					pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.NORTH_1_WEST_1);
				}
			} else {
				if ((isOtherColorAtSqr(pawnSqr + Chess.NORTH_1_EAST_1))
						|| (atSqr(pawnSqr + Chess.EAST_1) == Chess.BK_PAWN_ENPASS)) {
					pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.NORTH_1_EAST_1);
				}
				if ((isOtherColorAtSqr(pawnSqr + Chess.NORTH_1_WEST_1))
						|| (atSqr(pawnSqr + Chess.WEST_1) == Chess.BK_PAWN_ENPASS)) {
					pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.NORTH_1_WEST_1);
				}
			}
		} else {
			// Black pawns
			if (isEmptySqr(pawnSqr + Chess.SOUTH_1)) {
				pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.SOUTH_1);
				if ((Chess.isRank7Sqr(pawnSqr)) && (isEmptySqr(pawnSqr + Chess.SOUTH_2))) {
					pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.SOUTH_2);
				}
			}
			if (Chess.isFileASqr(pawnSqr)) {
				if ((isOtherColorAtSqr(pawnSqr + Chess.SOUTH_1_EAST_1))
						|| (atSqr(pawnSqr + Chess.EAST_1) == Chess.WH_PAWN_ENPASS)) {
					pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.SOUTH_1_EAST_1);
				}
			} else if (Chess.isFileHSqr(pawnSqr)) {
				if ((isOtherColorAtSqr(pawnSqr + Chess.SOUTH_1_WEST_1))
						|| (atSqr(pawnSqr + Chess.WEST_1) == Chess.WH_PAWN_ENPASS)) {
					pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.SOUTH_1_WEST_1);
				}
			} else {
				if ((isOtherColorAtSqr(pawnSqr + Chess.SOUTH_1_EAST_1))
						|| (atSqr(pawnSqr + Chess.EAST_1) == Chess.WH_PAWN_ENPASS)) {
					pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.SOUTH_1_EAST_1);
				}
				if ((isOtherColorAtSqr(pawnSqr + Chess.SOUTH_1_WEST_1))
						|| (atSqr(pawnSqr + Chess.WEST_1) == Chess.WH_PAWN_ENPASS)) {
					pawnMoves[numberOfMoves++] = new Move(atSqr(pawnSqr), pawnSqr, pawnSqr + Chess.SOUTH_1_WEST_1);
				}
			}
		}
		pawnMoves = Move.removeNullElements(pawnMoves, numberOfMoves);
		return pawnMoves;
	}

	/**
	 * Returns an array of moves along straight directions. This finds the legal and
	 * pseudo legal moves that a rook can make, or the moves for a queen's straight
	 * directions.
	 * 
	 * @param pieceSqr int value where the piece is on the board
	 * @return Move[] the legal and pseudo legal moves in straight directions
	 */
	public Move[] findStraightMoves(int pieceSqr) {
		Move[] straightMoves = new Move[Chess.MAX_STRAIGHT_MOVES];
		int numberOfMoves = 0;

		// Direction north
		for (int testSqr = (pieceSqr + Chess.NORTH_1); testSqr >= Chess.A8_SQR; testSqr += Chess.NORTH_1) {
			if (isEmptySqr(testSqr)) {
				straightMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
			} else if (isOtherColorAtSqr(testSqr)) {
				straightMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
				break;
			} else {
				break;
			}
		}

		// Direction south
		for (int testSqr = (pieceSqr + Chess.SOUTH_1); testSqr <= Chess.H1_SQR; testSqr += Chess.SOUTH_1) {
			if (isEmptySqr(testSqr)) {
				straightMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
			} else if (isOtherColorAtSqr(testSqr)) {
				straightMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
				break;
			} else {
				break;
			}
		}

		// Direction east
		for (int testSqr = (pieceSqr + Chess.EAST_1); !Chess
				.isFileHSqr(testSqr + Chess.WEST_1); testSqr += Chess.EAST_1) {
			if (isEmptySqr(testSqr)) {
				straightMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
			} else if (isOtherColorAtSqr(testSqr)) {
				straightMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
				break;
			} else {
				break;
			}
		}

		// Direction west
		for (int testSqr = (pieceSqr + Chess.WEST_1); !Chess
				.isFileASqr(testSqr + Chess.EAST_1); testSqr += Chess.WEST_1) {
			if (isEmptySqr(testSqr)) {
				straightMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
			} else if (isOtherColorAtSqr(testSqr)) {
				straightMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
				break;
			} else {
				break;
			}
		}
		straightMoves = Move.removeNullElements(straightMoves, numberOfMoves);
		return straightMoves;
	}

	/**
	 * Returns an array of moves along diagonal directions. This finds the legal and
	 * pseudo legal moves that a bishop can make, or the moves for a queen's
	 * diagonal directions.
	 * 
	 * @param pieceSqr int value where the piece is on the board
	 * @return Move[] the legal and pseudo legal moves in diagonal directions
	 */
	public Move[] findDiagonalMoves(int pieceSqr) {
		Move[] diagonalMoves = new Move[Chess.MAX_DIAGONAL_MOVES];
		int numberOfMoves = 0;
		// Direction north-east
		for (int testSqr = (pieceSqr + Chess.NORTH_1_EAST_1); (testSqr >= Chess.A8_SQR)
				&& (!Chess.isFileHSqr(testSqr + Chess.SOUTH_1_WEST_1)); testSqr += Chess.NORTH_1_EAST_1) {
			if (isEmptySqr(testSqr)) {
				diagonalMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
			} else if (isOtherColorAtSqr(testSqr)) {
				diagonalMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
				break;
			} else {
				break;
			}
		}
		// Direction south-east
		for (int testSqr = (pieceSqr + Chess.SOUTH_1_EAST_1); (testSqr <= Chess.H1_SQR)
				&& (!Chess.isFileHSqr(testSqr + Chess.NORTH_1_WEST_1)); testSqr += Chess.SOUTH_1_EAST_1) {
			if (isEmptySqr(testSqr)) {
				diagonalMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
			} else if (isOtherColorAtSqr(testSqr)) {
				diagonalMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
				break;
			} else {
				break;
			}
		}
		// Direction south-west
		for (int testSqr = (pieceSqr + Chess.SOUTH_1_WEST_1); (testSqr <= Chess.H1_SQR)
				&& (!Chess.isFileASqr(testSqr + Chess.NORTH_1_EAST_1)); testSqr += Chess.SOUTH_1_WEST_1) {
			if (isEmptySqr(testSqr)) {
				diagonalMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
			} else if (isOtherColorAtSqr(testSqr)) {
				diagonalMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
				break;
			} else {
				break;
			}
		}
		// Direction north-west
		for (int testSqr = (pieceSqr + Chess.NORTH_1_WEST_1); (testSqr >= Chess.A8_SQR)
				&& (!Chess.isFileASqr(testSqr + Chess.SOUTH_1_EAST_1)); testSqr += Chess.NORTH_1_WEST_1) {
			if (isEmptySqr(testSqr)) {
				diagonalMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
			} else if (isOtherColorAtSqr(testSqr)) {
				diagonalMoves[numberOfMoves++] = new Move(atSqr(pieceSqr), pieceSqr, testSqr);
				break;
			} else {
				break;
			}
		}
		diagonalMoves = Move.removeNullElements(diagonalMoves, numberOfMoves);
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
	 * @param sqr              int, the square to be assessed
	 * @param whiteIsAttacking boolean whether white is the color to be assessed on
	 *                         it's attacking of the square
	 * @return <code>true</code> if this square is attacked by the chosen color;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAttackedSqr(int sqr, boolean whiteIsAttacking) {
		Position tempPosition = clone();
		if ((Chess.isWhitePiece(atSqr(sqr)) || (isEmptySqr(sqr))) && whiteIsAttacking)
			tempPosition.updateSqr(Chess.BK_QUEEN, sqr);
		else if ((Chess.isBlackPiece(atSqr(sqr)) || (isEmptySqr(sqr))) && !whiteIsAttacking)
			tempPosition.updateSqr(Chess.WH_QUEEN, sqr);

		if (whiteToPlay != whiteIsAttacking)
			tempPosition.whiteToPlay = whiteIsAttacking;

		Move[] tempMoves = tempPosition.findPseudoLegalMoves();
		for (int i = 0; i < tempMoves.length; i++) {
			if ((tempMoves[i].getEndSqr()) == sqr)
				return true;
		}
		return (atSqr(sqr) == Chess.WH_PAWN_ENPASS) || (atSqr(sqr) == Chess.BK_PAWN_ENPASS);
	}

	/**
	 * Returns one of the kings' squares.
	 * 
	 * @param whiteKingColor <code>boolean</code> whether white is the color of the
	 *                       king to be found
	 * @return int value where the king is on the board
	 */
	public int findKingSqr(boolean whiteKingColor) {
		for (int i = 0; i < board.length(); i++) {
			if ((whiteKingColor && Chess.isWhiteKing(atSqr(i))) || (!whiteKingColor && Chess.isBlackKing(atSqr(i))))
				return i;
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
		if (whiteToPlay)
			return Chess.isBlackPiece(atSqr(sqr));
		else
			return Chess.isWhitePiece(atSqr(sqr));
	}

	/**
	 * Returns true if there is no piece at this square.
	 * 
	 * @param sqr int value of the square
	 * @return <code>true</code> if this square has no piece on it;
	 *         <code>false</code> otherwise.
	 */
	public boolean isEmptySqr(int sqr) {
		return atSqr(sqr) == Chess.EMPTY;
	}

	/**
	 * Updates this square on the board to either empty or to the piece that is
	 * passed.
	 * 
	 * @param charToPut character representing the piece or an empty square
	 * @param sqr       int value of the square to be updated
	 */
	public void updateSqr(char charToPut, int sqr) {
		board = board.substring(0, sqr) + charToPut + board.substring(sqr + 1);
	}

	/**
	 * Updates the king's castling ability for non king moves.
	 * 
	 * @param move the move that the (non king) piece is making
	 */
	public void updateKingCastlingAbility(Move move) {
		// Updating white king for castling ability
		if ((atSqr(Chess.E1_SQR) == Chess.WH_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == Chess.H1_SQR) || (move.getEndSqr() == Chess.H1_SQR)))
			updateSqr(Chess.WH_KING_CASTLE_QUEENSIDE, Chess.E1_SQR);
		else if ((atSqr(Chess.E1_SQR) == Chess.WH_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == Chess.A1_SQR) || (move.getEndSqr() == Chess.A1_SQR)))
			updateSqr(Chess.WH_KING_CASTLE_KINGSIDE, Chess.E1_SQR);
		else if (((atSqr(Chess.E1_SQR) == Chess.WH_KING_CASTLE_KINGSIDE)
				&& ((move.getStartSqr() == Chess.H1_SQR) || (move.getEndSqr() == Chess.H1_SQR)))
				|| ((atSqr(Chess.E1_SQR) == Chess.WH_KING_CASTLE_QUEENSIDE)
						&& (((move.getStartSqr()) == Chess.A1_SQR) || (move.getEndSqr() == Chess.A1_SQR))))
			updateSqr(Chess.WH_KING, Chess.E1_SQR);
		// Updating black king for castling ability
		if ((atSqr(Chess.E8_SQR) == Chess.BK_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == Chess.H8_SQR) || (move.getEndSqr() == Chess.H8_SQR)))
			updateSqr(Chess.BK_KING_CASTLE_QUEENSIDE, Chess.E8_SQR);
		else if ((atSqr(Chess.E8_SQR) == Chess.BK_KING_CASTLE_BOTH_SIDES)
				&& ((move.getStartSqr() == Chess.A8_SQR) || (move.getEndSqr() == Chess.A8_SQR)))
			updateSqr(Chess.BK_KING_CASTLE_KINGSIDE, Chess.E8_SQR);
		else if (((atSqr(Chess.E8_SQR) == Chess.BK_KING_CASTLE_KINGSIDE)
				&& ((move.getStartSqr() == Chess.H8_SQR) || (move.getEndSqr() == Chess.H8_SQR)))
				|| ((atSqr(Chess.E8_SQR) == Chess.BK_KING_CASTLE_QUEENSIDE)
						&& ((move.getStartSqr() == Chess.A8_SQR) || (move.getEndSqr() == Chess.A8_SQR))))
			updateSqr(Chess.BK_KING, Chess.E8_SQR);
	}

	/**
	 * Updates the board so that pawns that could have been captured en passant
	 * become regular pawns.
	 */
	public void removeEnPassantAbility() {
		for (int i = 0; i < board.length(); i++) {
			if (atSqr(i) == Chess.WH_PAWN_ENPASS)
				updateSqr(Chess.WH_PAWN, i);
			else if (atSqr(i) == Chess.BK_PAWN_ENPASS)
				updateSqr(Chess.BK_PAWN, i);
		}
	}

	/**
	 * Returns the contents of a square.
	 * 
	 * @param sqr int value the square
	 * @return character contents of the square
	 */
	public char atSqr(int sqr) {
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
		if (whiteToPlay)
			printPosition = "White to move:\n";
		else
			printPosition = "Black to move:\n";

		for (int i = 0; i < board.length(); i++) {
			printPosition += atSqr(i) + " ";
			if (Chess.isFileHSqr(i))
				printPosition += "\n";
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
