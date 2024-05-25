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
			if (!isSelfCheckMove(move)) {
				legalMoves.add(move);
			}
		}
		if (legalMoves.isEmpty()) {
			throw new NoLegalMovesException("There are no legal moves for the current player in this position");
		} else {
			return legalMoves;
		}
	}

	/**
	 * Updates isWhiteToPlay and the board so that the move has been played.
	 * Accounts for castling, en passant and promotion.
	 *
	 * @param move the <code>Move</code> to be made
	 */
	public void makeMove(Move move) {
		board.updateRookForCastlingMove(move);
		board.updateCapturedPawnForEnPassantMove(move);
		board.removeEnPassantCapturability();
		board.updateStartSqrContents(move);
		board.updateEndSqrContents(move);
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
		if (!Board.isOnTheBoard(testMove.getStartSqr()) || !Board.isOnTheBoard(testMove.getEndSqr()))
			return false;
		LinkedList<Move> tempMoves = findPseudoLegalPieceMoves(testMove.getPiece(), testMove.getStartSqr());
		for (Move move : tempMoves) {
			if (move.equals(testMove))
				return !isSelfCheckMove(testMove);
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
			if (isMovableSqr(i)) {
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
			else if (isMovableSqr(testSqr))
				knightMoves.addAll(board.constructMove(knightSqr, testSqr));
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
		LinkedList<Move> kingMoves = new LinkedList<>();
		kingMoves.addAll(findNormalKingMoves(kingSqr));
		if (!isCheck())
			kingMoves.addAll(findCastlingKingMoves(kingSqr));
		return kingMoves;
	}
	
	/**
	 * Returns the standard (non castling) moves that the king can make.
	 * 
	 * @param kingSqr int index of the square the king is on
	 * @return <code>LinkedList</code> the non castling moves that the king can make
	 */
	private LinkedList<Move> findNormalKingMoves(int kingSqr) {
		LinkedList<Move> normalKingMoves = new LinkedList<>();
		int[] testVectors = { Chess.NORTH_1, Chess.NORTH_1_EAST_1, Chess.EAST_1, Chess.SOUTH_1_EAST_1, Chess.SOUTH_1,
				Chess.SOUTH_1_WEST_1, Chess.WEST_1, Chess.NORTH_1_WEST_1 };
		for (int testVector : testVectors) {
			int testSqr = kingSqr + testVector;
			if (Board.hasExceededAnEdge(testSqr, testVector))
				continue;
			else if (isMovableSqr(testSqr))
				normalKingMoves.addAll(board.constructMove(kingSqr, testSqr));
		}
		return normalKingMoves;
	}
	
	/**
	 * Returns the castling moves that king can make.
	 * 
	 * @param kingSqr int index of the square the king is on
	 * @return <code>LinkedList</code> the castling moves that the king can make
	 */
	private LinkedList<Move> findCastlingKingMoves(int kingSqr){
		LinkedList<Move> castlingKingMoves = new LinkedList<>();
		if (board.isKingsideCastleableKingAtSqr(kingSqr) && 
				board.isEmptySqr(kingSqr + Chess.EAST_1) && board.isEmptySqr(kingSqr + Chess.EAST_2)) {
			castlingKingMoves.addAll(board.constructMove(kingSqr, kingSqr + Chess.EAST_2));
		}
		if (board.isQueensideCastleableKingAtSqr(kingSqr) && board.isEmptySqr(kingSqr + Chess.WEST_1) 
				&& board.isEmptySqr(kingSqr + Chess.WEST_2) && board.isEmptySqr(kingSqr + Chess.WEST_3)) {
			castlingKingMoves.addAll(board.constructMove(kingSqr, kingSqr + Chess.WEST_2));
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
			if ((board.isCapturableSqr(testSqr, whiteToPlay))
					|| ((getSqr(pawnSqr + captureVector) == Chess.BK_PAWN_ENPASS) && whiteToPlay)
					|| ((getSqr(pawnSqr + captureVector) == Chess.WH_PAWN_ENPASS) && !whiteToPlay))
				diagonalPawnMoves.addAll(board.constructMove(pawnSqr, testSqr));
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
		int testSqr = pawnSqr + movementVector;
		if (board.isEmptySqr(testSqr))
			straightPawnMoves.addAll(board.constructMove(pawnSqr, testSqr));
		testSqr += movementVector;
		if (board.pawnCanMove2SqrsAhead(pawnSqr, movementVector))
			straightPawnMoves.addAll(board.constructMove(pawnSqr, testSqr));
		return straightPawnMoves;
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
					straightMoves.addAll(board.constructMove(pieceSqr, testSqr));
				} else if (board.isCapturableSqr(testSqr, whiteToPlay)) {
					straightMoves.addAll(board.constructMove(pieceSqr, testSqr));
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
					diagonalMoves.addAll(board.constructMove(pieceSqr, testSqr));
				} else if (board.isCapturableSqr(testSqr, whiteToPlay)) {
					diagonalMoves.addAll(board.constructMove(pieceSqr, testSqr));
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
	 * Returns true if this square can be moved to because it's empty or has
	 * a capturable piece.
	 * 
	 * @param sqr int index of the square to test
	 * @return <code>true</code> if this square is empty or has a piece that can be captured;
	 *         <code>false</code> otherwise.
	 */
	private boolean isMovableSqr(int sqr) {
		return board.isEmptySqr(sqr) || board.isCapturableSqr(sqr, whiteToPlay);
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
