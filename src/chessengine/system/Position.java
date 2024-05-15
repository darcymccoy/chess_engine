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

	/** The information of a physical board for the chess position */
	private Board board;

	/**
	 * Default constructor (standard starting chess position, white is to play).
	 */
	public Position() {
		this(true, new Board());
	}

	/**
	 * Parameterized constructor specifying the board and which color is to play.
	 *
	 * @param whiteToPlay boolean whether white is currently to play
	 * @param board       string 64 characters long where each character is a square
	 *                    on the board starting from A8 and moving left and down
	 */
	public Position(boolean whiteToPlay, Board board) {
		this.whiteToPlay = whiteToPlay;
		this.board = board;
	}

	/**
	 * Copy constructor.
	 *
	 * @param otherPosition the <code>Position</code> to copy
	 */
	public Position(Position otherPosition) {
		this(otherPosition.whiteToPlay, otherPosition.board.clone());
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
			board.setSqr(getSqr(move.getEndSqr() + Chess.EAST_1), move.getEndSqr() + Chess.WEST_1);
			board.setSqr(Chess.EMPTY, move.getEndSqr() + Chess.EAST_1);
		} else if (move.isQueensideCastling()) {
			board.setSqr(getSqr(move.getEndSqr() + Chess.WEST_2), move.getEndSqr() + Chess.EAST_1);
			board.setSqr(Chess.EMPTY, move.getEndSqr() + Chess.WEST_2);
		} else if (move.isEnPassant()) {

			if (whiteToPlay) {
				board.setSqr(Chess.EMPTY, move.getEndSqr() + Chess.SOUTH_1);
			} else {
				board.setSqr(Chess.EMPTY, move.getEndSqr() + Chess.NORTH_1);
			}

		} else if (move.isPromotion()) {
			pieceToPut = move.getPromoteTo();
		}
		if (isAllowsEnPassant(move)) {
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
			board.updateKingCastlingAbility(move);
		}
		
		board.removeEnPassantAbility();
		board.setSqr(Chess.EMPTY, move.getStartSqr());
		board.setSqr(pieceToPut, move.getEndSqr());
		whiteToPlay = !whiteToPlay;
	}
	
	/**
	 * Returns true if the move puts a pawn into a position where it can be captured
	 * en passant.
	 * 
	 * @param move the Move to be tested
	 * @return <code>true</code> if the move is a pawn advancing 2 squares and
	 *         allowing itself to be captured en passant;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAllowsEnPassant(Move move) {
		if ((move.getPiece() == Chess.WH_PAWN) || (move.getPiece() == Chess.BK_PAWN)) {
	        char east1SqrContents = 0;
	        char west1SqrContents = 0;
	        
	        if (!Board.isFileHSqr(move.getEndSqr())) {
	            east1SqrContents = getSqr(move.getEndSqr() + Chess.EAST_1);
	        }
	        if (!Board.isFileASqr(move.getEndSqr())) {
	            west1SqrContents = getSqr(move.getEndSqr() + Chess.WEST_1);
	        }
	        return (((move.getStartSqr() + Chess.NORTH_2) == move.getEndSqr())
	                && ((east1SqrContents == Chess.BK_PAWN) || (west1SqrContents == Chess.BK_PAWN)))
	                || (((move.getStartSqr() + Chess.SOUTH_2) == move.getEndSqr())
	                && ((east1SqrContents == Chess.WH_PAWN) || (west1SqrContents == Chess.WH_PAWN)));
	    }
	    return false;
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
		if ((testMove.getStartSqr() <= Board.H1_SQR) && (testMove.getEndSqr() <= Board.H1_SQR)
				&& (testMove.getStartSqr() >= Board.A8_SQR) && (testMove.getEndSqr() >= Board.A8_SQR)) {
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

		for (int i = 0; i < board.getSqrs().length(); i++) {
			if (board.isEmptySqr(i) || isOtherColorAtSqr(i)) {
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

		for (int testVector : testVectors) {
			int testSqr = knightSqr + testVector;
			if (Board.hasExceededAnEdge(testSqr, testVector))
				continue;
			else if (isOtherColorAtSqr(testSqr) || board.isEmptySqr(testSqr))
				knightMoves.add(new Move(getSqr(knightSqr), knightSqr, testSqr, getSqr(testSqr)));
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

		for (int testVector : testVectors) {
			int testSqr = kingSqr + testVector;
			if (Board.hasExceededAnEdge(testSqr, testVector))
				continue;
			else if (isOtherColorAtSqr(testSqr) || board.isEmptySqr(testSqr))
				kingMoves.add(new Move(piece, kingSqr, testSqr, getSqr(testSqr)));
		}
		kingMoves.addAll(findCastlingKingMoves(piece, kingSqr));
		return kingMoves;
	}
	
	/**
	 * Returns the castling moves that king can make.
	 * 
	 * @param piece character representing the king
	 * @param kingSqr int index of the square the king is on
	 * @return <code>LinkedList</code> the castling moves that the king can make
	 */
	private LinkedList<Move> findCastlingKingMoves(char piece, int kingSqr){
		LinkedList<Move> castlingKingMoves = new LinkedList<>();
		// Kingside castling
		if (((((piece == Chess.WH_KING_CASTLE_BOTH_SIDES) || (piece == Chess.WH_KING_CASTLE_KINGSIDE))
				&& (getSqr(kingSqr + Chess.EAST_3) == Chess.WH_ROOK))
				|| (((piece == Chess.BK_KING_CASTLE_BOTH_SIDES) || (piece == Chess.BK_KING_CASTLE_KINGSIDE))
						&& (getSqr(kingSqr + Chess.EAST_3) == Chess.BK_ROOK)))
				&& (board.isEmptySqr(kingSqr + Chess.EAST_1)) && (board.isEmptySqr(kingSqr + Chess.EAST_2))) {
			castlingKingMoves.add(new Move(piece, kingSqr, kingSqr + Chess.EAST_2, getSqr(kingSqr + Chess.EAST_2)));
		}
		// Queenside castling
		if (((((piece == Chess.WH_KING_CASTLE_BOTH_SIDES) || (piece == Chess.WH_KING_CASTLE_QUEENSIDE))
				&& (getSqr(kingSqr + Chess.WEST_4) == Chess.WH_ROOK))
				|| (((piece == Chess.BK_KING_CASTLE_BOTH_SIDES) || (piece == Chess.BK_KING_CASTLE_QUEENSIDE))
						&& (getSqr(kingSqr + Chess.WEST_4) == Chess.BK_ROOK)))
				&& (board.isEmptySqr(kingSqr + Chess.WEST_1)) && (board.isEmptySqr(kingSqr + Chess.WEST_2))
				&& (board.isEmptySqr(kingSqr + Chess.WEST_3))) {
			castlingKingMoves.add(new Move(piece, kingSqr, kingSqr + Chess.WEST_2, getSqr(kingSqr + Chess.WEST_2)));
		}
		return castlingKingMoves;
	}

	/**
	 * Returns the moves that the queen can make.
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
		int movementVector;
		if (whiteToPlay) {
			movementVector = Chess.NORTH_1;
		} else {
			movementVector = Chess.SOUTH_1;
		}
		pawnMoves.addAll(findStraightPawnMoves(pawnSqr, movementVector));
		pawnMoves.addAll(findDiagonalPawnMoves(pawnSqr, movementVector));
		pawnMoves = addPromoteTypes(pawnMoves);
		return pawnMoves;
	}

	/**
	 * Returns the pseudo legal moves that the pawn can capture on (diagonally).
	 *
	 * @param pawnSqr    int index of the square the pawn is on
	 * @param movementVector int direction vector of the pawn's movement
	 * @return <code>LinkedList</code> the moves where the pawn can capture
	 */
	private LinkedList<Move> findDiagonalPawnMoves(int pawnSqr, int movementVector) {
		LinkedList<Move> diagonalPawnMoves = new LinkedList<>();
		int[] captureVectors = { Chess.EAST_1, Chess.WEST_1 };
		for (int captureVector : captureVectors) {
			int testSqr = pawnSqr + captureVector + movementVector;
			if (Board.hasExceededAnEdge(testSqr, captureVector))
				continue;
			if ((isOtherColorAtSqr(testSqr))
					|| ((getSqr(pawnSqr + captureVector) == Chess.BK_PAWN_ENPASS) && whiteToPlay)
					|| ((getSqr(pawnSqr + captureVector) == Chess.WH_PAWN_ENPASS) && !whiteToPlay))
				diagonalPawnMoves.add(new Move(getSqr(pawnSqr), pawnSqr, testSqr, getSqr(testSqr)));
		}
		return diagonalPawnMoves;
	}

	/**
	 * Returns the pseudo legal moves that the pawn can make for the 2 squares ahead
	 * of it.
	 *
	 * @param pawnSqr    int index of the square the pawn is on
	 * @param movementVector int direction vector of the pawn's movement
	 * @return <code>LinkedList</code> the moves that the pawn can make for the 2
	 *         squares ahead of it
	 */
	private LinkedList<Move> findStraightPawnMoves(int pawnSqr, int movementVector) {
		LinkedList<Move> straightPawnMoves = new LinkedList<>();
		for (int testSqr = (pawnSqr + movementVector), i = 0; ((testSqr <= Board.H1_SQR) && (testSqr >= Board.A8_SQR))
				&& i < 2; testSqr += movementVector, i++) {
			if (board.isEmptySqr(testSqr)) {
				straightPawnMoves.add(new Move(getSqr(pawnSqr), pawnSqr, testSqr, getSqr(testSqr)));
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
				newMoves.remove(numberOfMoves);
				for (char promoteType : promoteTypes) {
					newMoves.add(pawnMoves.get(i).clone());
					newMoves.get(numberOfMoves++).setPromoteTo(promoteType);
				}
				numberOfMoves--;
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
			for (int testSqr = (pieceSqr + testVector); !Board.hasExceededAnEdge(testSqr, testVector); testSqr += testVector) {
				if (board.isEmptySqr(testSqr)) {
					straightMoves.add(new Move(getSqr(pieceSqr), pieceSqr, testSqr, getSqr(testSqr)));
				} else if (isOtherColorAtSqr(testSqr)) {
					straightMoves.add(new Move(getSqr(pieceSqr), pieceSqr, testSqr, getSqr(testSqr)));
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
			for (int testSqr = (pieceSqr + testVector); !Board.hasExceededAnEdge(testSqr, testVector); testSqr += testVector) {
				if (board.isEmptySqr(testSqr)) {
					diagonalMoves.add(new Move(getSqr(pieceSqr), pieceSqr, testSqr, getSqr(testSqr)));
				} else if (isOtherColorAtSqr(testSqr)) {
					diagonalMoves.add(new Move(getSqr(pieceSqr), pieceSqr, testSqr, getSqr(testSqr)));
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
		return isAttackedSqr(board.findKingSqr(whiteToPlay), !whiteToPlay);
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
		return tempPosition.isAttackedSqr(tempPosition.board.findKingSqr(!tempPosition.whiteToPlay), tempPosition.whiteToPlay)
				|| (move.isCastling() && tempPosition.isAttackedSqr((move.getStartSqr() + move.getEndSqr()) / 2,
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
		if ((Chess.isWhitePiece(getSqr(sqr)) || (board.isEmptySqr(sqr))) && whiteIsAttacking) {
			tempPosition.board.setSqr(Chess.BK_QUEEN, sqr);
		} else if ((Chess.isBlackPiece(getSqr(sqr)) || (board.isEmptySqr(sqr))) && !whiteIsAttacking) {
			tempPosition.board.setSqr(Chess.WH_QUEEN, sqr);
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
	 * Returns the contents of a square.
	 *
	 * @param sqr int value the square
	 * @return character contents of the square
	 */
	public char getSqr(int sqr) {
		return board.getSqr(sqr);
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
		printPosition += board.toString();
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
	 * @return the Board object
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * @param board the Board
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

}
