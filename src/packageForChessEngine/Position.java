package packageForChessEngine;

// Written by Darcy McCoy
// Starting November 27, 2023

public class Position {
	private boolean whiteToPlay;
	private String board;

	// Cardinal and ordinal direction moves
	private static final int NORTH_1 = -8;
	private static final int NORTH_2 = NORTH_1 + NORTH_1;
	private static final int NORTH_1_EAST_1 = -7;
	private static final int EAST_1 = 1;
	private static final int EAST_2 = EAST_1 + EAST_1;
	private static final int EAST_3 = EAST_1 + EAST_1 + EAST_1;
	private static final int SOUTH_1_EAST_1 = 9;
	private static final int SOUTH_1 = 8;
	private static final int SOUTH_2 = SOUTH_1 + SOUTH_1;
	private static final int SOUTH_1_WEST_1 = 7;
	private static final int WEST_1 = -1;
	private static final int WEST_2 = WEST_1 + WEST_1;
	private static final int WEST_3 = WEST_1 + WEST_1 + WEST_1;
	private static final int WEST_4 = WEST_1 + WEST_1 + WEST_1 + WEST_1;
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

	public Position clone() {
		// Returns a copy of the position
		return new Position(this);
	}
	
	public int[] findLegalMoves() {
		// Returns an array of all legal moves that the color to move can make
		int[] possibleMoves = findPossibleMoves();
		int[] legalMoves = new int[possibleMoves.length];
		int numberOfLegalMoves = 0;

		for (int i = 0; i < possibleMoves.length; i++) {
			if (!isSelfCheckMove(possibleMoves[i])
					&& (!isCastling(atSqr(possibleMoves[i] / 100), possibleMoves[i]) || !isCheck())) {
				legalMoves[numberOfLegalMoves++] = possibleMoves[i];
			}
		}
		return removeElementsThatAreZero(legalMoves, numberOfLegalMoves);
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
		removeEnPassantAbility();
		
		updateSqr('-', move / 100);
		updateSqr(pieceToPut, move % 100);
		whiteToPlay = !whiteToPlay;
	}

	public boolean isLegalMove(int move) {
		// Returns true when the move is legal
		// **Works for both impossible and possible moves**
		if(((move / 100) <= H1_SQR) && ((move % 100) <= H1_SQR) && ((move / 100) >= A8_SQR) && ((move % 100) >= A8_SQR)) {
			int[] tempMoves = findPossiblePieceMoves(atSqr(move / 100), move / 100);
			for (int i = 0; i < tempMoves.length; i++) {
				if (move == tempMoves[i])
					return !isSelfCheckMove(move) && (!isCastling(atSqr(move / 100), move) || !isCheck());
			}
		}
		return false;
	}

	public int[] findPossibleMoves() {
		// Returns an array of all possible moves that the color to move can make
		// **This can include illegal moves**
		// **(such as self check moves, castling out of check)**
		int[] possibleMoves = new int[218];
		int[] possiblePieceMoves = new int[0];
		int numberOfPossibleMoves = 0;
		
		for (int i = 0; i < board.length(); i++) {
			if ((isEmptySqr(i)) || (isOtherColorAtSqr(i)))
				continue;
			
			possiblePieceMoves = findPossiblePieceMoves(atSqr(i), i);
			for (int j = 0; j < possiblePieceMoves.length; j++) {
				possibleMoves[numberOfPossibleMoves++] = possiblePieceMoves[j];
			}
		}
		return removeElementsThatAreZero(possibleMoves, numberOfPossibleMoves);
	}
	
	public int[] findPossiblePieceMoves(char piece, int pieceSquare) {
		// Returns an array of moves that the piece can make
		// **Doesn't assess move legality**
		switch (piece) {
		case 'P':
		case 'p':
		case 'e':
		case 'E':
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
			return new int[0];
		}
	}

	public int[] findKnightMoves(int knightSquare) {
		// Returns an array of moves that the knight can make (includes
		// capturing)
		int[] knightMoves = new int[8];
		int numberOfMoves = 0;
		int[] inspectMoves = { NORTH_2 + EAST_1, NORTH_1 + EAST_2, SOUTH_1 + EAST_2, SOUTH_2 + EAST_1, 
				SOUTH_2 + WEST_1, SOUTH_1 + WEST_2, NORTH_1 + WEST_2, NORTH_2 + WEST_1 };

		// Assessing rank square
		if (isRank8Sqr(knightSquare)) {
			inspectMoves[7] = 0;
			inspectMoves[6] = 0;
			inspectMoves[1] = 0;
			inspectMoves[0] = 0;
		} else if (isRank7Sqr(knightSquare)) {
			inspectMoves[7] = 0;
			inspectMoves[0] = 0;
		} else if (isRank1Sqr(knightSquare)) {
			inspectMoves[5] = 0;
			inspectMoves[4] = 0;
			inspectMoves[3] = 0;
			inspectMoves[2] = 0;
		} else if (isRank2Sqr(knightSquare)) {
			inspectMoves[4] = 0;
			inspectMoves[3] = 0;
		}

		// Assessing file square
		if (isFileHSqr(knightSquare)) {
			inspectMoves[3] = 0;
			inspectMoves[2] = 0;
			inspectMoves[1] = 0;
			inspectMoves[0] = 0;
		} else if (isFileGSqr(knightSquare)) {
			inspectMoves[2] = 0;
			inspectMoves[1] = 0;
		} else if (isFileASqr(knightSquare)) {
			inspectMoves[7] = 0;
			inspectMoves[6] = 0;
			inspectMoves[5] = 0;
			inspectMoves[4] = 0;
		} else if (isFileBSqr(knightSquare)) {
			inspectMoves[6] = 0;
			inspectMoves[5] = 0;
		}
		for (int inspectMove : inspectMoves) {
			if ((inspectMove != 0) && (isOtherColorAtSqr(knightSquare + inspectMove)
					|| isEmptySqr(knightSquare + inspectMove))) {
				knightMoves[numberOfMoves++] = knightSquare * 100 + knightSquare + inspectMove;
			}
		}
		return removeElementsThatAreZero(knightMoves, numberOfMoves);
	}

	public int[] findKingMoves(int kingSquare) {
		// Returns an array of moves that the king can make (includes
		// capturing and castling)
		char piece = atSqr(kingSquare);
		int[] kingMoves = new int[10];
		int numberOfMoves = 0;
		int[] inspectMoves = { NORTH_1, NORTH_1_EAST_1, EAST_1, SOUTH_1_EAST_1, SOUTH_1, SOUTH_1_WEST_1, WEST_1, NORTH_1_WEST_1 };

		if (isFileHSqr(kingSquare)) {
			inspectMoves[1] = 0;
			inspectMoves[2] = 0;
			inspectMoves[3] = 0;
		} else if (isFileASqr(kingSquare)) {
			inspectMoves[5] = 0;
			inspectMoves[6] = 0;
			inspectMoves[7] = 0;
		}
		if (isRank1Sqr(kingSquare)) {
			inspectMoves[3] = 0;
			inspectMoves[4] = 0;
			inspectMoves[5] = 0;
		} else if (isRank8Sqr(kingSquare)) {
			inspectMoves[0] = 0;
			inspectMoves[1] = 0;
			inspectMoves[7] = 0;
		}
		for (int inspectMove : inspectMoves) {
			if ((inspectMove != 0) && (isOtherColorAtSqr(kingSquare + inspectMove)
					|| isEmptySqr(kingSquare + inspectMove))) {
				kingMoves[numberOfMoves++] = kingSquare * 100 + kingSquare + inspectMove;
			}
		}

		// Kingside castling
		if (((((piece == '5') || (piece == '4')) && (atSqr(kingSquare + EAST_3) == 'R')) 
				|| (((piece == '2') || (piece == '1')) && (atSqr(kingSquare + EAST_3) == 'r')))
				&& (isEmptySqr(kingSquare + EAST_1)) && (isEmptySqr(kingSquare + EAST_2))) {
			kingMoves[numberOfMoves++] = kingSquare * 100 + kingSquare + EAST_2;
		}
		// Queenside castling
		if (((((piece == '5') || (piece == '3')) && (atSqr(kingSquare + WEST_4) == 'R')) 
				|| (((piece == '2') || (piece == '0')) && (atSqr(kingSquare + WEST_4) == 'r')))
				&& (isEmptySqr(kingSquare + WEST_1)) && (isEmptySqr(kingSquare + WEST_2)) && (isEmptySqr(kingSquare + WEST_3))) {
			kingMoves[numberOfMoves++] = kingSquare * 100 + kingSquare + WEST_2;
		}
		return removeElementsThatAreZero(kingMoves, numberOfMoves);
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
		int[] pawnMoves = new int[12];
		int numberOfMoves = 0;

		if (whiteToPlay) {
			// White pawns
			if (isEmptySqr(pawnSquare + NORTH_1)) {
				pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + NORTH_1);
				if ((isRank2Sqr(pawnSquare)) && (isEmptySqr(pawnSquare + NORTH_2))) {
					pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + NORTH_2);
				}
			}
			if (isFileASqr(pawnSquare)) {
				if ((isOtherColorAtSqr(pawnSquare + NORTH_1_EAST_1)) || (atSqr(pawnSquare + EAST_1) == 'e')) {
					pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + NORTH_1_EAST_1);
				}
			} else if (isFileHSqr(pawnSquare)) {
				if ((isOtherColorAtSqr(pawnSquare + NORTH_1_WEST_1)) || (atSqr(pawnSquare + WEST_1) == 'e')) {
					pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + NORTH_1_WEST_1);
				}
			} else {
				if ((isOtherColorAtSqr(pawnSquare + NORTH_1_EAST_1)) || (atSqr(pawnSquare + EAST_1) == 'e')) {
					pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + NORTH_1_EAST_1);
				}
				if ((isOtherColorAtSqr(pawnSquare + NORTH_1_WEST_1)) || (atSqr(pawnSquare + WEST_1) == 'e')) {
					pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + NORTH_1_WEST_1);
				}
			}
		} else {
			// Black pawns
			if (isEmptySqr(pawnSquare + SOUTH_1)) {
				pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + SOUTH_1);
				if ((isRank7Sqr(pawnSquare)) && (isEmptySqr(pawnSquare + SOUTH_2))) {
					pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + SOUTH_2);
				}
			}
			if (isFileASqr(pawnSquare)) {
				if ((isOtherColorAtSqr(pawnSquare + SOUTH_1_EAST_1)) || (atSqr(pawnSquare + EAST_1) == 'E')) {
					pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + SOUTH_1_EAST_1);
				}
			} else if (isFileHSqr(pawnSquare)) {
				if ((isOtherColorAtSqr(pawnSquare + SOUTH_1_WEST_1)) || (atSqr(pawnSquare + WEST_1) == 'E')) {
					pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + SOUTH_1_WEST_1);
				}
			} else {
				if ((isOtherColorAtSqr(pawnSquare + SOUTH_1_EAST_1)) || (atSqr(pawnSquare + EAST_1) == 'E')) {
					pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + SOUTH_1_EAST_1);
				}
				if ((isOtherColorAtSqr(pawnSquare + SOUTH_1_WEST_1)) || (atSqr(pawnSquare + WEST_1) == 'E')) {
					pawnMoves[numberOfMoves++] = (pawnSquare * 100) + (pawnSquare + SOUTH_1_WEST_1);
				}
			}
		}
		return removeElementsThatAreZero(pawnMoves, numberOfMoves);
	}

	public int[] findStraightMoves(int pieceSquare) {
		// Returns an array of moves along straight directions (includes
		// capturing)
		int[] straightMoves = new int[28];
		int numberOfMoves = 0;

		// Direction north
		for (int inspectSquare = (pieceSquare + NORTH_1); inspectSquare >= A8_SQR; inspectSquare += NORTH_1) {
			if (isEmptySqr(inspectSquare)) {
				straightMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
			} else if (isOtherColorAtSqr(inspectSquare)) {
				straightMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
				break;
			} else {
				break;
			}
		}

		// Direction south
		for (int inspectSquare = (pieceSquare + SOUTH_1); inspectSquare <= H1_SQR; inspectSquare += SOUTH_1) {
			if (isEmptySqr(inspectSquare)) {
				straightMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
			} else if (isOtherColorAtSqr(inspectSquare)) {
				straightMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
				break;
			} else {
				break;
			}
		}

		// Direction east
		for (int inspectSquare = (pieceSquare + EAST_1); !isFileHSqr(inspectSquare + WEST_1); inspectSquare += EAST_1) {
			if (isEmptySqr(inspectSquare)) {
				straightMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
			} else if (isOtherColorAtSqr(inspectSquare)) {
				straightMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
				break;
			} else {
				break;
			}
		}

		// Direction west
		for (int inspectSquare = (pieceSquare + WEST_1); !isFileASqr(inspectSquare + EAST_1); inspectSquare += WEST_1) {
			if (isEmptySqr(inspectSquare)) {
				straightMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
			} else if (isOtherColorAtSqr(inspectSquare)) {
				straightMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
				break;
			} else {
				break;
			}
		}
		return removeElementsThatAreZero(straightMoves, numberOfMoves);
	}

	public int[] findDiagonalMoves(int pieceSquare) {
		// Returns an array of moves along diagonal directions (includes
		// capturing)
		int[] diagonalMoves = new int[28];
		int numberOfMoves = 0;

		// Direction north-east
		for (int inspectSquare = (pieceSquare + NORTH_1_EAST_1); (inspectSquare >= A8_SQR)
				&& (!isFileHSqr(inspectSquare + SOUTH_1_WEST_1)); inspectSquare += NORTH_1_EAST_1) {
			if (isEmptySqr(inspectSquare)) {
				diagonalMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
			} else if (isOtherColorAtSqr(inspectSquare)) {
				diagonalMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
				break;
			} else {
				break;
			}
		}

		// Direction south-east
		for (int inspectSquare = (pieceSquare + SOUTH_1_EAST_1); (inspectSquare <= H1_SQR)
				&& (!isFileHSqr(inspectSquare + NORTH_1_WEST_1)); inspectSquare += SOUTH_1_EAST_1) {
			if (isEmptySqr(inspectSquare)) {
				diagonalMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
			} else if (isOtherColorAtSqr(inspectSquare)) {
				diagonalMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
				break;
			} else {
				break;
			}
		}

		// Direction south-west
		for (int inspectSquare = (pieceSquare + SOUTH_1_WEST_1); (inspectSquare <= H1_SQR)
				&& (!isFileASqr(inspectSquare + NORTH_1_EAST_1)); inspectSquare += SOUTH_1_WEST_1) {
			if (isEmptySqr(inspectSquare)) {
				diagonalMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
			} else if (isOtherColorAtSqr(inspectSquare)) {
				diagonalMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
				break;
			} else {
				break;
			}
		}

		// Direction north-west
		for (int inspectSquare = (pieceSquare + NORTH_1_WEST_1); (inspectSquare >= A8_SQR)
				&& (!isFileASqr(inspectSquare + SOUTH_1_EAST_1)); inspectSquare += NORTH_1_WEST_1) {
			if (isEmptySqr(inspectSquare)) {
				diagonalMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
			} else if (isOtherColorAtSqr(inspectSquare)) {
				diagonalMoves[numberOfMoves++] = (pieceSquare * 100) + inspectSquare;
				break;
			} else {
				break;
			}
		}
		return removeElementsThatAreZero(diagonalMoves, numberOfMoves);
	}

	public int[] removeElementsThatAreZero(int[] arrayToUpdate, int numberNonZeroElements) {
		// Returns a new array without the elements which are zeroes
		// **the numberNonZeroElements must match the number of non zero elements in the array**
		int[] newArray = new int[numberNonZeroElements];
		for (int i = 0, j = 0; j < newArray.length; i++, j++) {
			if (arrayToUpdate[i] != 0)
				newArray[j] = arrayToUpdate[i];
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

	public boolean isAttackedSqr(int square, boolean whiteIsAttacking) {
		// Returns true if the square is attacked by a piece of the corresponding color
		Position tempPosition = new Position(this);
		if ((isWhitePiece(atSqr(square)) || (isEmptySqr(square))) && whiteIsAttacking)
			tempPosition.updateSqr('q', square);
		else if ((isBlackPiece(atSqr(square)) || (isEmptySqr(square))) && !whiteIsAttacking)
			tempPosition.updateSqr('Q', square);
		
		if (whiteToPlay != whiteIsAttacking)
			tempPosition.whiteToPlay = whiteIsAttacking;
		
		int[] tempMoves = tempPosition.findPossibleMoves();
		for (int i = 0; i < tempMoves.length; i++) {
			if ((tempMoves[i] % 100) == square)
				return true;
		}
		
		return (atSqr(square) == 'E') || (atSqr(square) == 'e');
	}

	public int findKingSqr(boolean whiteKingColor) {
		// Returns the king square of the corresponding color
		// **Returns -1 if the king isn't found**
		for (int i = 0; i < board.length(); i++) {
			if (whiteKingColor) {
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

	public boolean isOtherColorAtSqr(int square) {
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
		board = board.substring(A8_SQR, square) + charToPut + board.substring(square + 1);
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
	
	public void removeEnPassantAbility() {
		// Updates the board so that pawns that could be captured en-passant
		// become regular pawns
		for (int i = 0; i < board.length(); i++) {
			if (atSqr(i) == 'E')
				updateSqr('P', i);
			else if (atSqr(i) == 'e')
				updateSqr('p', i);
		}
	}
	
	public int getStartSqr(int move) {
		// Gets the starting square of a move
		return (move % 10000) / 100;
	}
	
	public int getEndSqr(int move) {
		// Gets the end square of a move
		return move % 100;
	}
	
	public int getPromoteType(int move) {
		// Gets the corresponding promotion number of a move
		// **If the move isn't promotion, 0 will be returned**
		return move / 1000000;
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
