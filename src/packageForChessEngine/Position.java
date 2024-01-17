package packageForChessEngine;

//Written by Darcy McCoy
//Starting November 27, 2023

public class Position {
	private boolean whiteToPlay = true;
	private String board = "";

	// Cardinal and ordinal direction moves
	private static final int NORTH_1 = -8;
	private static final int NORTH_2 = 2 * NORTH_1;
	private static final int NORTH_1_EAST_1 = -7;
	private static final int EAST_1 = 1;
	private static final int EAST_2 = 2 * EAST_1;
	private static final int SOUTH_1_EAST_1 = 9;
	private static final int SOUTH_1 = 8;
	private static final int SOUTH_2 = 2 * SOUTH_1;
	private static final int SOUTH_1_WEST_1 = 7;
	private static final int WEST_1 = -1;
	private static final int WEST_2 = 2 * WEST_1;
	private static final int NORTH_1_WEST_1 = -9;

	// specific squares
	private static final int A8_SQR = 0;
	private static final int E8_SQR = 4;
	private static final int H8_SQR = 7;
	private static final int A7_SQR = 8;
	private static final int H7_SQR = 15;
	private static final int A2_SQR = 48;
	private static final int H2_SQR = 55;
	private static final int A1_SQR = 56;
	private static final int E1_SQR = 60;
	private static final int H1_SQR = 63;

	public Position() {
		// Default constructor
		this(true,
				"rnbq2bnr" + "pppppppp" + "--------" + "--------" + "--------" + "--------" + "PPPPPPPP" + "RNBQ5BNR");
	}

	public Position(boolean whiteToPlay, String board) {
		// Parameterized constructor
		this.whiteToPlay = whiteToPlay;
		this.board = board;
	}

	public Position(Position otherPosition) {
		// Copy constructor
		this(otherPosition.whiteToPlay, otherPosition.board);
	}

	public int[] findLegalMoves() {
		int[] legalMoves = new int[218];
		int[] possiblePieceMoves = new int[0];
		int numberOfLegalMoves = 0;
		
		for (int i = 0; i < board.length(); i++) {
			if ((isEmptySqr(i)) || (isOtherColAtSqr(i)))
				continue;
			
			possiblePieceMoves = findPossibleMoves(atSqr(i), i);
			for (int j = 0; j < possiblePieceMoves.length; j++) {

				if (!isSelfCheckMove(possiblePieceMoves[j])
						&& (!isCastling(atSqr(possiblePieceMoves[j] / 100), possiblePieceMoves[j]) || !isCheck())) {
					legalMoves[numberOfLegalMoves] = possiblePieceMoves[j];
					numberOfLegalMoves++;
				}
			}
		}
		return removeZeroElements(legalMoves, numberOfLegalMoves);
	}
	
	public void makeMove(int move) {
		// Updates isWhiteToPlay and the board so that the move has been played
		// Accounts for castling, en-passant and promotion
		char pieceToPut = atSqr(move / 100);

		if (isCastling(pieceToPut, move)) {

			if ((move / 100) == ((move % 100) + WEST_2)) {
				// Kingside castling
				updateSqr(atSqr(move % 100 + EAST_1), move % 100 + WEST_1);
				updateSqr('-', move % 100 + EAST_1);
			} else {
				// Queenside castling
				updateSqr(atSqr(move % 100 + WEST_2), move % 100 + EAST_1);
				updateSqr('-', move % 100 + WEST_2);
			}

		} else if (isEnPassant(pieceToPut, move)) {

			if (whiteToPlay)
				updateSqr('-', move % 100 + SOUTH_1);
			else
				updateSqr('-', move % 100 + NORTH_1);

		} else if (isAllowsEnPassant(pieceToPut, move)) {

			if (whiteToPlay)
				pieceToPut = 'E';
			else
				pieceToPut = 'e';
			
		} else if (isPromotion(pieceToPut, move)) {

			if (whiteToPlay)
				pieceToPut = 'Q';
			else
				pieceToPut = 'q';
		}
		
		if ((atSqr(move / 100) == '5') || (atSqr(move / 100) == '4') || (atSqr(move / 100) == '3')) {
			// Updating white king for castling ability for king moves
			pieceToPut = 'K';
		} else if ((atSqr(move / 100) == '2') || (atSqr(move / 100) == '1') || (atSqr(move / 100) == '0')) {
			// Updating black king for castling ability for king moves
			pieceToPut = 'k';
		} else {
			// Updating king castling ability for non-king moves
			updateKingCastlingAbility(move);
		}
		removeEnPassant();
		
		updateSqr('-', move / 100);
		updateSqr(pieceToPut, move % 100);
		whiteToPlay = !whiteToPlay;
	}

	public boolean isLegalMove(int move) {
		// Returns true when the move is legal
		// **Works for both impossible and possible moves**
		int[] tempMoves = findPossibleMoves(atSqr(move / 100), move / 100);
		for (int i = 0; i < tempMoves.length; i++) {
			if (move == tempMoves[i])
				return !isSelfCheckMove(move) && (!isCastling(atSqr(move / 100), move) || !isCheck());
		}
		return false;
	}

	public int[] findPossibleMoves(char piece, int pieceSquare) {
		// Returns an array of moves that the piece can make
		// **Doesn't assess move legality**
		switch (piece) {
		case 'P':
		case 'p':
			return findPawnMoves(pieceSquare);

		case 'R':
		case 'r':
			return findStraightMoves(pieceSquare);

		case 'N':
		case 'n':
			return findKnightMoves(pieceSquare);

		case 'B':
		case 'b':
			return findDiagonalMoves(pieceSquare);

		case 'Q':
		case 'q':
			return findQueenMoves(pieceSquare);

		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case 'k':
		case 'K':
			return findKingMoves(pieceSquare);
			
		default:
			return null;
		}
	}

	public int[] findKnightMoves(int knightSquare) {
		// Returns an array of moves that the knight can make (includes
		// capturing)
		int[] knightMoves = new int[8];
		int numberOfMoves = 0;
		int[] inspectSquares = { NORTH_2 + EAST_1, NORTH_1 + EAST_2, SOUTH_1 + EAST_2, SOUTH_2 + EAST_1, 
				SOUTH_2 + WEST_1, SOUTH_1 + WEST_2, NORTH_1 + WEST_2, NORTH_2 + WEST_1 };

		// Assessing rank square
		if (isRank8Sqr(knightSquare)) {
			knightMoves[7] = -1;
			knightMoves[6] = -1;
			knightMoves[1] = -1;
			knightMoves[0] = -1;
		} else if (isRank7Sqr(knightSquare)) {
			knightMoves[7] = -1;
			knightMoves[0] = -1;
		} else if (isRank1Sqr(knightSquare)) {
			knightMoves[5] = -1;
			knightMoves[4] = -1;
			knightMoves[3] = -1;
			knightMoves[2] = -1;
		} else if (isRank2Sqr(knightSquare)) {
			knightMoves[4] = -1;
			knightMoves[3] = -1;
		}

		// Assessing file square
		if (isFileHSqr(knightSquare)) {
			knightMoves[3] = -1;
			knightMoves[2] = -1;
			knightMoves[1] = -1;
			knightMoves[0] = -1;
		} else if (isFileGSqr(knightSquare)) {
			knightMoves[2] = -1;
			knightMoves[1] = -1;
		} else if (isFileASqr(knightSquare)) {
			knightMoves[7] = -1;
			knightMoves[6] = -1;
			knightMoves[5] = -1;
			knightMoves[4] = -1;
		} else if (isFileBSqr(knightSquare)) {
			knightMoves[6] = -1;
			knightMoves[5] = -1;
		}
		for (int i = 0; i < inspectSquares.length; i++) {
			if ((knightMoves[i] != -1) && (isOtherColAtSqr(knightSquare + inspectSquares[i])
					|| isEmptySqr(knightSquare + inspectSquares[i]))) {
				knightMoves[i] = knightSquare * 100 + knightSquare + inspectSquares[i];
				numberOfMoves++;
			} else
				knightMoves[i] = 0;
		}
		return removeZeroElements(knightMoves, numberOfMoves);
	}

	public int[] findKingMoves(int kingSquare) {
		// Returns an array of moves that the king can make (includes
		// capturing and castling)
		char piece = atSqr(kingSquare);
		int[] kingMoves = new int[10];
		int numberOfMoves = 0;
		int[] inspectSquares = { NORTH_1, NORTH_1_EAST_1, EAST_1, SOUTH_1_EAST_1, SOUTH_1, SOUTH_1_WEST_1, WEST_1, NORTH_1_WEST_1 };

		if (isFileHSqr(kingSquare)) {
			kingMoves[1] = -1;
			kingMoves[2] = -1;
			kingMoves[3] = -1;
		} else if (isFileASqr(kingSquare)) {
			kingMoves[5] = -1;
			kingMoves[6] = -1;
			kingMoves[7] = -1;
		}
		if (isRank1Sqr(kingSquare)) {
			kingMoves[3] = -1;
			kingMoves[4] = -1;
			kingMoves[5] = -1;
		} else if (isRank8Sqr(kingSquare)) {
			kingMoves[0] = -1;
			kingMoves[1] = -1;
			kingMoves[7] = -1;
		}
		for (int i = 0; i < inspectSquares.length; i++) {
			if ((kingMoves[i] != -1) && (isOtherColAtSqr(kingSquare + inspectSquares[i])
					|| isEmptySqr(kingSquare + inspectSquares[i]))) {
				kingMoves[i] = kingSquare * 100 + kingSquare + inspectSquares[i];
				numberOfMoves++;
			} else
				kingMoves[i] = 0;
		}

		// White king castling
		if ((piece == '5') || (piece == '4')) {
			if ((isEmptySqr(kingSquare + EAST_1)) && (isEmptySqr(kingSquare + EAST_2))) {
				kingMoves[8] = kingSquare * 100 + kingSquare + EAST_2;
				numberOfMoves++;
			}
		}
		if ((piece == '5') || (piece == '3')) {
			if ((isEmptySqr(kingSquare + WEST_1)) && (isEmptySqr(kingSquare + WEST_2)) && (isEmptySqr(kingSquare + 3 * WEST_1))) {
				kingMoves[9] = kingSquare * 100 + kingSquare + WEST_2;
				numberOfMoves++;
			}
		}

		// Black king castling
		if ((piece == '2') || (piece == '1')) {
			if ((isEmptySqr(kingSquare + EAST_1)) && (isEmptySqr(kingSquare + EAST_2))) {
				kingMoves[8] = kingSquare * 100 + kingSquare + EAST_2;
				numberOfMoves++;
			}
		}
		if ((piece == '2') || (piece == '0')) {
			if ((isEmptySqr(kingSquare + WEST_1)) && (isEmptySqr(kingSquare + WEST_2)) && (isEmptySqr(kingSquare + 3 * WEST_1))) {
				kingMoves[9] = kingSquare * 100 + kingSquare + WEST_2;
				numberOfMoves++;
			}
		}
		return removeZeroElements(kingMoves, numberOfMoves);
	}

	public int[] findQueenMoves(int queenSquare) {
		// Returns an array of moves that the queen can make (includes
		// capturing)
		int[] tempStraightMoves = findStraightMoves(queenSquare);
		int[] tempDiagonalMoves = findDiagonalMoves(queenSquare);
		int[] queenMoves = new int[tempStraightMoves.length + tempDiagonalMoves.length];

		for (int i = 0; i < tempStraightMoves.length; i++) {
			queenMoves[i] = tempStraightMoves[i];
		}
		for (int i = 0; i < tempDiagonalMoves.length; i++) {
			queenMoves[i + tempStraightMoves.length] = tempDiagonalMoves[i];
		}
		return queenMoves;
	}

	public int[] findPawnMoves(int pawnSquare) {
		// Returns an array of moves that the pawn can make (includes all types of
		// capturing)
		// **Doesn't include promotion**
		// **Only works for pawns**
		int[] pawnMoves = new int[4];
		int numberOfMoves = 0;

		if (whiteToPlay) {
			// White pawns
			if (isEmptySqr(pawnSquare + NORTH_1)) {
				pawnMoves[0] = (pawnSquare * 100) + (pawnSquare + NORTH_1);
				numberOfMoves++;
				if ((isRank2Sqr(pawnSquare)) && (isEmptySqr(pawnSquare + NORTH_2))) {
					pawnMoves[1] = (pawnSquare * 100) + (pawnSquare + NORTH_2);
					numberOfMoves++;
				}
			}
			if (isFileASqr(pawnSquare)) {
				if ((isOtherColAtSqr(pawnSquare + NORTH_1_EAST_1)) || (atSqr(pawnSquare + EAST_1) == 'e')) {
					pawnMoves[2] = (pawnSquare * 100) + (pawnSquare + NORTH_1_EAST_1);
					numberOfMoves++;
				}
			} else if (isFileHSqr(pawnSquare + EAST_1)) {
				if ((isOtherColAtSqr(pawnSquare + NORTH_1_WEST_1)) || (atSqr(pawnSquare + WEST_1) == 'e')) {
					pawnMoves[2] = (pawnSquare * 100) + (pawnSquare + NORTH_1_WEST_1);
					numberOfMoves++;
				}
			} else {
				if ((isOtherColAtSqr(pawnSquare + NORTH_1_EAST_1)) || (atSqr(pawnSquare + EAST_1) == 'e')) {
					pawnMoves[2] = (pawnSquare * 100) + (pawnSquare + NORTH_1_EAST_1);
					numberOfMoves++;
				}
				if ((isOtherColAtSqr(pawnSquare + NORTH_1_WEST_1)) || (atSqr(pawnSquare + WEST_1) == 'e')) {
					pawnMoves[3] = (pawnSquare * 100) + (pawnSquare + NORTH_1_WEST_1);
					numberOfMoves++;
				}
			}
		} else {
			// Black pawns
			if (isEmptySqr(pawnSquare + SOUTH_1)) {
				pawnMoves[0] = (pawnSquare * 100) + (pawnSquare + SOUTH_1);
				numberOfMoves++;
				if ((isRank7Sqr(pawnSquare)) && (isEmptySqr(pawnSquare + SOUTH_2))) {
					pawnMoves[1] = (pawnSquare * 100) + (pawnSquare + SOUTH_2);
					numberOfMoves++;
				}
			}
			if (isFileASqr(pawnSquare)) {
				if ((isOtherColAtSqr(pawnSquare + SOUTH_1_EAST_1)) || (atSqr(pawnSquare + EAST_1) == 'E')) {
					pawnMoves[2] = (pawnSquare * 100) + (pawnSquare + SOUTH_1_EAST_1);
					numberOfMoves++;
				}
			} else if (isFileHSqr(pawnSquare)) {
				if ((isOtherColAtSqr(pawnSquare + SOUTH_1_WEST_1)) || (atSqr(pawnSquare + WEST_1) == 'E')) {
					pawnMoves[2] = (pawnSquare * 100) + (pawnSquare + SOUTH_1_WEST_1);
					numberOfMoves++;
				}
			} else {
				if ((isOtherColAtSqr(pawnSquare + SOUTH_1_EAST_1)) || (atSqr(pawnSquare + EAST_1) == 'E')) {
					pawnMoves[2] = (pawnSquare * 100) + (pawnSquare + SOUTH_1_EAST_1);
					numberOfMoves++;
				}
				if ((isOtherColAtSqr(pawnSquare + SOUTH_1_WEST_1)) || (atSqr(pawnSquare + WEST_1) == 'E')) {
					pawnMoves[3] = (pawnSquare * 100) + (pawnSquare + SOUTH_1_WEST_1);
					numberOfMoves++;
				}
			}
		}
		return removeZeroElements(pawnMoves, numberOfMoves);
	}

	public int[] findStraightMoves(int pieceSquare) {
		// Returns an array of moves along straight directions (includes
		// capturing)
		int[] straightMoves = new int[28];
		int numberOfMoves = 0;

		// Direction north
		for (int i = 0, inspectSquare = (pieceSquare + NORTH_1); inspectSquare >= A8_SQR; inspectSquare += NORTH_1, i++) {
			if (isEmptySqr(inspectSquare)) {
				straightMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				straightMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction south
		for (int i = 7, inspectSquare = (pieceSquare + SOUTH_1); inspectSquare <= H1_SQR; inspectSquare += SOUTH_1, i++) {
			if (isEmptySqr(inspectSquare)) {
				straightMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				straightMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction east
		for (int i = 14, inspectSquare = pieceSquare; !isFileHSqr(inspectSquare++); i++) {
			if (isEmptySqr(inspectSquare)) {
				straightMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				straightMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction west
		for (int i = 21, inspectSquare = pieceSquare; !isFileASqr(inspectSquare--); i++) {
			if (isEmptySqr(inspectSquare)) {
				straightMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				straightMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}
		return removeZeroElements(straightMoves, numberOfMoves);
	}

	public int[] findDiagonalMoves(int pieceSquare) {
		// Returns an array of moves along diagonal directions (includes
		// capturing)
		int[] diagonalMoves = new int[28];
		int numberOfMoves = 0;

		// Direction north-east
		for (int i = 0, inspectSquare = (pieceSquare + NORTH_1_EAST_1); (inspectSquare >= A8_SQR)
				&& (!isFileHSqr(inspectSquare + SOUTH_1_WEST_1)); inspectSquare += NORTH_1_EAST_1, i++) {
			if (isEmptySqr(inspectSquare)) {
				diagonalMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				diagonalMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction south-east
		for (int i = 7, inspectSquare = (pieceSquare + SOUTH_1_EAST_1); (inspectSquare <= H1_SQR)
				&& (!isFileHSqr(inspectSquare + NORTH_1_WEST_1)); inspectSquare += SOUTH_1_EAST_1, i++) {
			if (isEmptySqr(inspectSquare)) {
				diagonalMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				diagonalMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction south-west
		for (int i = 14, inspectSquare = (pieceSquare + SOUTH_1_WEST_1); (inspectSquare <= H1_SQR)
				&& (!isFileASqr(inspectSquare + NORTH_1_EAST_1)); inspectSquare += SOUTH_1_WEST_1, i++) {
			if (isEmptySqr(inspectSquare)) {
				diagonalMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				diagonalMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction north-west
		for (int i = 21, inspectSquare = (pieceSquare + NORTH_1_WEST_1); (inspectSquare >= A8_SQR)
				&& (!isFileASqr(inspectSquare + SOUTH_1_EAST_1)); inspectSquare += NORTH_1_WEST_1, i++) {
			if (isEmptySqr(inspectSquare)) {
				diagonalMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				diagonalMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}
		return removeZeroElements(diagonalMoves, numberOfMoves);
	}

	public int[] removeZeroElements(int[] otherArray, int numberOfNonZeroElements) {
		// Returns a new array without the elements which are zeroes
		// **the numberOfNonZeroElements must match the number of non zero elements in the other array**
		int[] newArray = new int[numberOfNonZeroElements];
		for (int i = 0, j = 0; j < newArray.length; i++, j++) {
			if (otherArray[i] != 0)
				newArray[j] = otherArray[i];
			else
				j--;
		}
		return newArray;
	}

	public boolean isPromotion(char piece, int move) {
		// Returns true if the move is promotion
		if (piece == 'P')
			return (isRank8Sqr(move % 100));
		else if (piece == 'p')
			return (isRank1Sqr(move % 100));
		else
			return false;
	}

	public boolean isCastling(char piece, int move) {
		// Returns true if the move is castling
		return ((piece == 'K') || (piece == '5') || (piece == '4') || (piece == '3') || (piece == 'k') || (piece == '2')
				|| (piece == '1') || (piece == '0'))
				&& (((move / 100) == ((move % 100) + EAST_2)) || ((move / 100) == ((move % 100) + WEST_2)));
	}

	public boolean isEnPassant(char piece, int move) {
		// Returns true if the move is en-passant
		if (piece == 'P')
			return atSqr((move % 100) + SOUTH_1) == 'e';
		else if (piece == 'p')
			return atSqr((move % 100) + NORTH_1) == 'E';
		else
			return false;
	}

	public boolean isAllowsEnPassant(char piece, int move) {
		// Returns true if the move puts a pawn into a position where it can be captured en-passant 
		if (piece == 'P')
			return (((move / 100) + NORTH_2) == (move % 100)) && ((atSqr((move % 100) + EAST_1) == 'p') || (atSqr((move % 100) + WEST_1) == 'p'));
		else if (piece == 'p')
			return (((move / 100) + SOUTH_2) == (move % 100)) && ((atSqr((move % 100) + EAST_1) == 'P') || (atSqr((move % 100) + WEST_1) == 'P'));
		else
			return false;
	}
	
	public boolean isCheck() {
		// Returns true if the color to move is in check
		return isAttackedSqr(findKingSqr(whiteToPlay), !whiteToPlay);
	}
	
	public boolean isSelfCheckMove(int move) {
		// Returns true if the move puts the king (of the color who makes that move)
		// into check or castles that king through check
		// **Only works for possible moves**
		Position tempPosition = new Position(this);
		tempPosition.makeMove(move);
		return tempPosition.isAttackedSqr(tempPosition.findKingSqr(!tempPosition.whiteToPlay), tempPosition.whiteToPlay)
				|| (isCastling(atSqr(move / 100), move)
						&& tempPosition.isAttackedSqr(((move / 100) + (move % 100)) / 2, tempPosition.whiteToPlay));
	}

	public boolean isAttackedSqr(int square, boolean attackingColourIsWhite) {
		// Returns true if the square is attacked by a piece of the opposing color
		int[] tempMoves = new int[0];
		tempMoves = findKnightMoves(square);
		for (int i = 0; i < tempMoves.length; i++) {
			if (attackingColourIsWhite) {
				if (atSqr(tempMoves[i] % 100) == 'N')
					return true;
			} else {
				if (atSqr(tempMoves[i] % 100) == 'n')
					return true;
			}
		}
		tempMoves = findStraightMoves(square);
		for (int i = 0; i < tempMoves.length; i++) {
			if (attackingColourIsWhite) {
				if ((atSqr(tempMoves[i] % 100) == 'R') || (atSqr(tempMoves[i] % 100) == 'Q'))
					return true;
			} else {
				if ((atSqr(tempMoves[i] % 100) == 'r') || (atSqr(tempMoves[i] % 100) == 'q'))
					return true;
			}
		}
		tempMoves = findDiagonalMoves(square);
		for (int i = 0; i < tempMoves.length; i++) {
			if (attackingColourIsWhite) {
				if ((atSqr(tempMoves[i] % 100) == 'B') || (atSqr(tempMoves[i] % 100) == 'Q'))
					return true;
			} else {
				if ((atSqr(tempMoves[i] % 100) == 'b') || (atSqr(tempMoves[i] % 100) == 'q'))
					return true;
			}
		}
		tempMoves = findKingMoves(square);
		for (int i = 0; i < tempMoves.length; i++) {
			if (attackingColourIsWhite) {
				if ((atSqr(tempMoves[i] % 100) == 'K') || (atSqr(tempMoves[i] % 100) == '5') 
						|| (atSqr(tempMoves[i] % 100) == '4') || (atSqr(tempMoves[i] % 100) == '3'))
					return true;
			} else {
				if ((atSqr(tempMoves[i] % 100) == 'k') || (atSqr(tempMoves[i] % 100) == '2') 
						|| (atSqr(tempMoves[i] % 100) == '1') || (atSqr(tempMoves[i] % 100) == '0'))
					return true;
			}
		}
		// Assessing possible pawn attacks
		if (attackingColourIsWhite && !isRank1Sqr(square)) {
			if (!isFileASqr(square) && ((atSqr(square + SOUTH_1_EAST_1) == 'P') || (atSqr(square + SOUTH_1_EAST_1) == 'E')))
				return true;
			else if (!isFileHSqr(square) && ((atSqr(square + SOUTH_1_WEST_1) == 'P') || (atSqr(square + SOUTH_1_WEST_1) == 'E')))
				return true;
		} else if (!attackingColourIsWhite && !isRank8Sqr(square)) {
			if (!isFileASqr(square) && ((atSqr(square + NORTH_1_WEST_1) == 'p') || (atSqr(square + NORTH_1_WEST_1) == 'e')))
				return true;
			else if (!isFileHSqr(square) && ((atSqr(square + NORTH_1_EAST_1) == 'p') || (atSqr(square + NORTH_1_EAST_1) == 'e')))
				return true;
		}
		return ((atSqr(square) == 'e') || (atSqr(square) == 'E'));
	}

	public int findKingSqr(boolean kingColorIsWhite) {
		// Returns the king square of the color passed
		// **Returns -1 if the piece isn't found**
		for (int i = 0; i < board.length(); i++) {
			if (kingColorIsWhite) {
				if ((atSqr(i) == 'K') || (atSqr(i) == '5') || (atSqr(i) == '4') || (atSqr(i) == '3')) {
					return i;
				}
			} else {
				if ((atSqr(i) == 'k') || (atSqr(i) == '2') || (atSqr(i) == '1') || (atSqr(i) == '0')) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public int findPieceSqr(char piece) {
		// Returns the square that the first instance of the piece is found at
		// **Returns -1 if the piece isn't found**
		int pieceSquare = -1;
		for (int i = 0; i < board.length(); i++) {
			if (atSqr(i) == piece) {
				pieceSquare = i;
				break;
			}	
		}
		return pieceSquare;
	}

	public boolean isOuterEdgeSqr(int square) {
		// Returns true if the square is one of the 28 squares on the outer edge of the
		// board
		return (isRank1Sqr(square)) || (isRank8Sqr(square)) || (isFileASqr(square)) || (isFileHSqr(square));
	}

	public boolean isInner16Sqr(int square) {
		// Returns true if the square is one of the 16 center squares
		return ((square >= 18) && (square <= 21)) || ((square >= 26) && (square <= 29))
				|| ((square >= 34) && (square <= 37)) || ((square >= 42) && (square <= 45));
	}

	public boolean isRank1Sqr(int square) {
		// Returns true if the square is on the 1st rank of the board
		return (square >= A1_SQR) && (square <= H1_SQR);
	}

	public boolean isRank2Sqr(int square) {
		// Returns true if the square is on the 2nd rank of the board
		return (square >= A2_SQR) && (square <= H2_SQR);
	}

	public boolean isRank7Sqr(int square) {
		// Returns true if the square is on the 7th rank of the board
		return (square >= A7_SQR) && (square <= H7_SQR);
	}

	public boolean isRank8Sqr(int square) {
		// Returns true if the square is on the 8th rank of the board
		return (square >= A8_SQR) && (square <= H8_SQR);
	}

	public boolean isFileASqr(int square) {
		// Returns true if the square is on file A of the board
		return square % 8 == 0;
	}

	public boolean isFileBSqr(int square) {
		// Returns true if the square is on file B of the board
		return (square + WEST_1) % 8 == 0;
	}

	public boolean isFileGSqr(int square) {
		// Returns true if the square is on file G of the board
		return (square + EAST_2) % 8 == 0;
	}

	public boolean isFileHSqr(int square) {
		// Returns true if the square is on file H of the board
		return (square + EAST_1) % 8 == 0;
	}

	public boolean isOtherColAtSqr(int square) {
		// Returns true if the piece at the square is the opposite color
		// of the color who is to play
		if (whiteToPlay)
			return isBlackPiece(atSqr(square));
		else
			return isWhitePiece(atSqr(square));
	}

	public boolean isEmptySqr(int square) {
		// Returns true if the square has no piece
		return atSqr(square) == '-';
	}

	public void updateSqr(char charToPut, int square) {
		// Updates the square either to empty or to the piece that is passed
		board = board.substring(0, square) + charToPut + board.substring(square + 1);
	}

	public void updateKingCastlingAbility(int move) {
		// Updates the king's castling ability based on a move
		// **Only updates for non-king moves**
		
		// Updating white king for castling ability
		if ((atSqr(E1_SQR) == '5') && (((move / 100) == H1_SQR) || ((move % 100) == H1_SQR)))
			updateSqr('3', E1_SQR);
		else if ((atSqr(E1_SQR) == '5') && (((move / 100) == A1_SQR) || ((move % 100) == A1_SQR)))
			updateSqr('4', E1_SQR);
		else if (((atSqr(E1_SQR) == '4') && (((move / 100) == H1_SQR) || ((move % 100) == H1_SQR))) 
				|| ((atSqr(E1_SQR) == '3') && (((move / 100) == A1_SQR) || ((move % 100) == A1_SQR))))
			updateSqr('K', E1_SQR);
		// Updating black king for castling ability
		else if ((atSqr(E8_SQR) == '2') && (((move / 100) == H8_SQR) || ((move % 100) == H8_SQR)))
			updateSqr('0', E8_SQR);
		else if ((atSqr(E8_SQR) == '2') && (((move / 100) == A8_SQR) || ((move % 100) == A8_SQR)))
			updateSqr('1', E8_SQR);
		else if (((atSqr(E8_SQR) == '1') && (((move / 100) == H8_SQR) || ((move % 100) == H8_SQR)))
				|| ((atSqr(E8_SQR) == '0') && (((move / 100) == A8_SQR) || ((move % 100) == A8_SQR))))
			updateSqr('k', E8_SQR);
	}
	
	public void removeEnPassant() {
		// Updates the board so that pawns that could be captured en-passant
		// become regular pawns
		for (int i = 0; i < board.length(); i++) {
			if (atSqr(i) == 'E')
				updateSqr('P', i);
			else if (atSqr(i) == 'e')
				updateSqr('p', i);
		}
	}
	
	public char atSqr(int square) {
		// Returns the contents of a square
		return board.charAt(square);
	}

	public boolean isWhitePiece(char piece) {
		// Returns true if the piece is white
		return ((piece == 'R') || (piece == 'N') || (piece == 'B') || (piece == 'Q') || (piece == '5') || (piece == '4')
				|| (piece == '3') || (piece == 'K') || (piece == 'P') || (piece == 'E'));
	}

	public boolean isBlackPiece(char piece) {
		// Returns true if the piece is black
		return ((piece == 'r') || (piece == 'n') || (piece == 'b') || (piece == 'q') || (piece == '2') || (piece == '1')
				|| (piece == '0') || (piece == 'k') || (piece == 'p') || (piece == 'e'));
	}

	public String toString() {
		// Returns a string with the player who is to move and the board laid out in a
		// 8 x 8 grid
		String printPosition = "";
		if (whiteToPlay)
			printPosition = "White to move:\n";
		else
			printPosition = "Black to move:\n";

		for (int i = 0; i < board.length(); i++) {
			printPosition += atSqr(i) + " ";
			if (isFileHSqr(i))
				printPosition += "\n";
		}
		return printPosition;
	}

	public boolean isWhiteToPlay() {
		// Getter for white to play
		return whiteToPlay;
	}

	public void setWhiteToPlay(boolean whiteToPlay) {
		// Setter for white to play
		this.whiteToPlay = whiteToPlay;
	}

	public String getBoard() {
		// Getter for board
		return board;
	}

	public void setBoard(String board) {
		// Setter for board
		this.board = board;
	}

}
