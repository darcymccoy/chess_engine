package PackageForChessEngine;

//Written by Darcy McCoy
//Starting November 27, 2023

public class Position {
	private boolean whiteToPlay = true;
	private String board = "";

	// Cardinal and ordinal directions
	static final int N1 = -8;
	static final int N2 = 2 * N1;
	static final int N1E1 = -7;
	static final int E1 = 1;
	static final int E2 = 2;
	static final int S1E1 = 9;
	static final int S1 = 8;
	static final int S2 = 2 * S1;
	static final int S1W1 = 7;
	static final int W1 = -1;
	static final int W2 = -2;
	static final int N1W1 = -9;

	// For knight moves
	static final int N2E1 = N2 + E1;
	static final int N1E2 = N1 + E2;
	static final int S1E2 = S1 + E2;
	static final int S2E1 = S2 + E1;
	static final int S2W1 = S2 + W1;
	static final int S1W2 = S1 + W2;
	static final int N1W2 = N1 + W2;
	static final int N2W1 = N2 + W1;

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

	public int evaluatePosition() {
		// Returns an integer (in centipawns) which is an evaluation of the position
		// from the perspective of the color who is to play
		int evaluationOfPosition = 0;
		for (int i = 0; i < board.length(); i++) {
			evaluationOfPosition += getPieceValue(atSqr(i), i);
		}
		if (whiteToPlay)
			return evaluationOfPosition;
		else
			return evaluationOfPosition * -1;
	}

	public void makeMove(int move) {
		// Updates isWhiteToPlay and the board so that the move has been played
		// Accounts for castling, en-passant and promotion
		char piece = atSqr(move / 100);
		updateSqr('-', move / 100);

		if (isCastling(piece, move)) {

			if ((move / 100) == ((move % 100) + W2)) {
				// Kingside castling
				updateSqr(atSqr(move % 100 + E1), move % 100 + W1);
				updateSqr('-', move % 100 + E1);
			} else {
				// Queenside castling
				updateSqr(atSqr(move % 100 + W2), move % 100 + E1);
				updateSqr('-', move % 100 + W2);
			}
			updateSqr(piece, move % 100);

		} else if (isEnPassant(piece, move)) {

			if (piece == 'P')
				updateSqr('-', move % 100 + S1);
			else
				updateSqr('-', move % 100 + N1);
			updateSqr(piece, move % 100);

		} else if (isPromotion(piece, move)) {

			if (piece == 'P')
				updateSqr('Q', move % 100);
			else
				updateSqr('q', move % 100);

		} else {
			updateSqr(piece, move % 100);
		}

		whiteToPlay = !whiteToPlay;
	}

	public int findTopMove() {
		// Returns an integer which is the top move for a position
		// **If no possible legal moves are found, 0 will be returned**
		int[] possiblePieceMoves = new int[0];
		int topMove = 0;
		int topMoveEval = 100000;
		int tempPositionEval;

		for (int i = 0; i < board.length(); i++) {
			if ((isEmptySqr(i)) || (isOtherColAtSqr(i)))
				continue;

			possiblePieceMoves = findPossibleMoves(atSqr(i), i);
			for (int j = 0; j < possiblePieceMoves.length; j++) {

				if (!isSelfCheckMove(possiblePieceMoves[j])
						&& (!isCastling(atSqr(possiblePieceMoves[j] / 100), possiblePieceMoves[j]) || !isCheck())) {

					Position tempPosition = new Position(this);
					tempPosition.makeMove(possiblePieceMoves[j]);
					tempPositionEval = tempPosition.evaluatePosition();
					if (tempPositionEval < topMoveEval) {
						topMove = possiblePieceMoves[j];
						topMoveEval = tempPositionEval;
					}
				}
			}
		}
		return topMove;
	}

	public int calculate(int currentDepth) {
		// Returns an integer which is the top moves concatenated
		int topMove = findTopMove();

		if (currentDepth <= 1) {
			return topMove;
		} else {
			Position tempPositionCalc = new Position(this);
			tempPositionCalc.makeMove(topMove);
			return topMove * 10000 + tempPositionCalc.calculate(--currentDepth);
		}

	}

	public boolean isLegalMove(int move) {
		// Returns true when the move is legal
		// (Can be passed impossible moves)
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
		int[] possibleMoves = new int[0];
		switch (piece) {
		case 'P':
		case 'p':
			possibleMoves = findPawnMoves(pieceSquare);
			break;

		case 'R':
		case 'r':
			possibleMoves = findStraightMoves(pieceSquare);
			break;

		case 'N':
		case 'n':
			possibleMoves = findKnightMoves(pieceSquare);
			break;

		case 'B':
		case 'b':
			possibleMoves = findDiagonalMoves(pieceSquare);
			break;

		case 'Q':
		case 'q':
			possibleMoves = findQueenMoves(pieceSquare);
			break;

		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case 'k':
		case 'K':
			possibleMoves = findKingMoves(pieceSquare);
			break;
		}
		return possibleMoves;

	}

	public int[] findKnightMoves(int knightSquare) {
		// Returns an array of moves that the knight can make (includes
		// capturing)
		int[] tempMoves = new int[8];
		int numberOfMoves = 0;
		int[] inspectSquares = { N2E1, N1E2, S1E2, S2E1, S2W1, S1W2, N1W2, N2W1 };

		// Assessing rank square
		if (isRank8Sqr(knightSquare)) {
			tempMoves[7] = -1;
			tempMoves[6] = -1;
			tempMoves[1] = -1;
			tempMoves[0] = -1;
		}
		if (isRank7Sqr(knightSquare)) {
			tempMoves[7] = -1;
			tempMoves[0] = -1;
		}
		if (isRank1Sqr(knightSquare)) {
			tempMoves[5] = -1;
			tempMoves[4] = -1;
			tempMoves[3] = -1;
			tempMoves[2] = -1;
		}
		if (isRank2Sqr(knightSquare)) {
			tempMoves[4] = -1;
			tempMoves[3] = -1;
		}

		// Assessing file square
		if (isFileHSqr(knightSquare)) {
			tempMoves[3] = -1;
			tempMoves[2] = -1;
			tempMoves[1] = -1;
			tempMoves[0] = -1;
		}
		if (isFileGSqr(knightSquare)) {
			tempMoves[2] = -1;
			tempMoves[1] = -1;
		}
		if (isFileASqr(knightSquare)) {
			tempMoves[7] = -1;
			tempMoves[6] = -1;
			tempMoves[5] = -1;
			tempMoves[4] = -1;
		}
		if (isFileBSqr(knightSquare)) {
			tempMoves[6] = -1;
			tempMoves[5] = -1;
		}
		for (int i = 0; i < inspectSquares.length; i++) {
			if ((tempMoves[i] != -1) && (isOtherColAtSqr(knightSquare + inspectSquares[i])
					|| isEmptySqr(knightSquare + inspectSquares[i]))) {
				tempMoves[i] = knightSquare * 100 + knightSquare + inspectSquares[i];
				numberOfMoves++;
			} else
				tempMoves[i] = 0;
		}

		int[] knightMoves = new int[numberOfMoves];
		for (int i = 0, j = 0; j < numberOfMoves; i++, j++) {
			if (tempMoves[i] != 0)
				knightMoves[j] = tempMoves[i];
			else
				j--;
		}
		return knightMoves;
	}

	public int[] findKingMoves(int kingSquare) {
		// Returns an array of moves that the king can make (includes
		// capturing and castling)
		char piece = atSqr(kingSquare);
		int[] tempMoves = new int[10];
		int numberOfMoves = 0;
		int[] inspectSquares = { N1, N1E1, E1, S1E1, S1, S1W1, W1, N1W1 };

		if (isFileHSqr(kingSquare)) {
			tempMoves[1] = -1;
			tempMoves[2] = -1;
			tempMoves[3] = -1;
		} else if (isFileASqr(kingSquare)) {
			tempMoves[5] = -1;
			tempMoves[6] = -1;
			tempMoves[7] = -1;
		}
		if (isRank1Sqr(kingSquare)) {
			tempMoves[3] = -1;
			tempMoves[4] = -1;
			tempMoves[5] = -1;
		} else if (isRank8Sqr(kingSquare)) {
			tempMoves[0] = -1;
			tempMoves[1] = -1;
			tempMoves[7] = -1;
		}
		for (int i = 0; i < inspectSquares.length; i++) {
			if ((tempMoves[i] != -1) && (isOtherColAtSqr(kingSquare + inspectSquares[i])
					|| isEmptySqr(kingSquare + inspectSquares[i]))) {
				tempMoves[i] = kingSquare * 100 + kingSquare + inspectSquares[i];
				numberOfMoves++;
			} else
				tempMoves[i] = 0;
		}

		// White king castling
		if ((piece == '5') || (piece == '4')) {
			if ((isEmptySqr(kingSquare + E1)) && (isEmptySqr(kingSquare + E2))) {
				tempMoves[8] = kingSquare * 100 + kingSquare + E2;
				numberOfMoves++;
			}
		}
		if ((piece == '5') || (piece == '3')) {
			if ((isEmptySqr(kingSquare + W1)) && (isEmptySqr(kingSquare + W2)) && (isEmptySqr(kingSquare + 3 * W1))) {
				tempMoves[9] = kingSquare * 100 + kingSquare + W2;
				numberOfMoves++;
			}
		}

		// Black king castling
		if ((piece == '2') || (piece == '1')) {
			if ((isEmptySqr(kingSquare + E1)) && (isEmptySqr(kingSquare + E2))) {
				tempMoves[8] = kingSquare * 100 + kingSquare + E2;
				numberOfMoves++;
			}
		}
		if ((piece == '2') || (piece == '0')) {
			if ((isEmptySqr(kingSquare + W1)) && (isEmptySqr(kingSquare + W2)) && (isEmptySqr(kingSquare + 3 * W1))) {
				tempMoves[9] = kingSquare * 100 + kingSquare + W2;
				numberOfMoves++;
			}
		}

		int[] kingMoves = new int[numberOfMoves];
		for (int i = 0, j = 0; j < numberOfMoves; i++, j++) {
			if (tempMoves[i] != 0)
				kingMoves[j] = tempMoves[i];
			else
				j--;
		}
		return kingMoves;
	}

	public int[] findQueenMoves(int queenSquare) {
		// Returns an array of moves that the queen can make (includes
		// capturing)
		int[] tempMoves = findStraightMoves(queenSquare);
		int numOfStrMoves = tempMoves.length;
		int[] queenMoves = new int[numOfStrMoves + findDiagonalMoves(queenSquare).length];

		for (int i = 0; i < tempMoves.length; i++) {
			queenMoves[i] = tempMoves[i];
		}
		tempMoves = findDiagonalMoves(queenSquare);
		for (int i = 0; i < tempMoves.length; i++) {
			queenMoves[i + numOfStrMoves] = tempMoves[i];
		}
		return queenMoves;
	}

	public int[] findPawnMoves(int pawnSquare) {
		// Returns an array of moves that the pawn can make (includes all types of
		// capturing)
		// **Doesn't include promotion**
		// **Only works for pawns**
		int[] tempMoves = new int[4];
		int numberOfMoves = 0;

		if (whiteToPlay) {
			// White pawns
			if (isEmptySqr(pawnSquare + N1)) {
				tempMoves[0] = (pawnSquare * 100) + (pawnSquare + N1);
				numberOfMoves++;
				if ((isRank2Sqr(pawnSquare)) && (isEmptySqr(pawnSquare + N2))) {
					tempMoves[1] = (pawnSquare * 100) + (pawnSquare + N2);
					numberOfMoves++;
				}
			}
			if (isFileASqr(pawnSquare)) {
				if ((isOtherColAtSqr(pawnSquare + N1E1)) || (atSqr(pawnSquare + E1) == 'e')) {
					tempMoves[2] = (pawnSquare * 100) + (pawnSquare + N1E1);
					numberOfMoves++;
				}
			} else if (isFileHSqr(pawnSquare + E1)) {
				if ((isOtherColAtSqr(pawnSquare + N1W1)) || (atSqr(pawnSquare + W1) == 'e')) {
					tempMoves[2] = (pawnSquare * 100) + (pawnSquare + N1W1);
					numberOfMoves++;
				}
			} else {
				if ((isOtherColAtSqr(pawnSquare + N1E1)) || (atSqr(pawnSquare + E1) == 'e')) {
					tempMoves[2] = (pawnSquare * 100) + (pawnSquare + N1E1);
					numberOfMoves++;
				}
				if ((isOtherColAtSqr(pawnSquare + N1W1)) || (atSqr(pawnSquare + W1) == 'e')) {
					tempMoves[3] = (pawnSquare * 100) + (pawnSquare + N1W1);
					numberOfMoves++;
				}
			}
		} else {
			// Black pawns
			if (isEmptySqr(pawnSquare + S1)) {
				tempMoves[0] = (pawnSquare * 100) + (pawnSquare + S1);
				numberOfMoves++;
				if ((isRank7Sqr(pawnSquare)) && (isEmptySqr(pawnSquare + S2))) {
					tempMoves[1] = (pawnSquare * 100) + (pawnSquare + S2);
					numberOfMoves++;
				}
			}
			if (isFileASqr(pawnSquare)) {
				if ((isOtherColAtSqr(pawnSquare + S1E1)) || (atSqr(pawnSquare + E1) == 'E')) {
					tempMoves[2] = (pawnSquare * 100) + (pawnSquare + S1E1);
					numberOfMoves++;
				}
			} else if (isFileHSqr(pawnSquare)) {
				if ((isOtherColAtSqr(pawnSquare + S1W1)) || (atSqr(pawnSquare + W1) == 'E')) {
					tempMoves[2] = (pawnSquare * 100) + (pawnSquare + S1W1);
					numberOfMoves++;
				}
			} else {
				if ((isOtherColAtSqr(pawnSquare + S1E1)) || (atSqr(pawnSquare + E1) == 'E')) {
					tempMoves[2] = (pawnSquare * 100) + (pawnSquare + S1E1);
					numberOfMoves++;
				}
				if ((isOtherColAtSqr(pawnSquare + S1W1)) || (atSqr(pawnSquare + W1) == 'E')) {
					tempMoves[3] = (pawnSquare * 100) + (pawnSquare + S1W1);
					numberOfMoves++;
				}
			}
		}
		int[] pawnMoves = new int[numberOfMoves];
		for (int i = 0, j = 0; j < numberOfMoves; i++, j++) {
			if (tempMoves[i] != 0)
				pawnMoves[j] = tempMoves[i];
			else
				j--;
		}
		return pawnMoves;
	}

	public int[] findStraightMoves(int pieceSquare) {
		// Returns an array of moves along straight directions (includes
		// capturing)
		int[] tempMoves = new int[28];
		int numberOfMoves = 0;

		// Direction north
		for (int i = 0, inspectSquare = (pieceSquare + N1); inspectSquare >= 0; inspectSquare += N1, i++) {
			if (isEmptySqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction south
		for (int i = 7, inspectSquare = (pieceSquare + S1); inspectSquare <= 63; inspectSquare += S1, i++) {
			if (isEmptySqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction east
		for (int i = 14, inspectSquare = pieceSquare; !isFileHSqr(inspectSquare++); i++) {
			if (isEmptySqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction west
		for (int i = 21, inspectSquare = pieceSquare; !isFileASqr(inspectSquare--); i++) {
			if (isEmptySqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}
		int[] straightMoves = new int[numberOfMoves];
		for (int i = 0, j = 0; j < numberOfMoves; i++, j++) {
			if (tempMoves[i] != 0)
				straightMoves[j] = tempMoves[i];
			else
				j--;
		}
		return straightMoves;
	}

	public int[] findDiagonalMoves(int pieceSquare) {
		// Returns an array of moves along diagonal directions (includes
		// capturing)
		int[] tempMoves = new int[28];
		int numberOfMoves = 0;

		// Direction north-east
		for (int i = 0, inspectSquare = (pieceSquare + N1E1); (inspectSquare >= 0)
				&& (!isFileHSqr(inspectSquare + S1W1)); inspectSquare += N1E1, i++) {
			if (isEmptySqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction south-east
		for (int i = 7, inspectSquare = (pieceSquare + S1E1); (inspectSquare <= 63)
				&& (!isFileHSqr(inspectSquare + N1W1)); inspectSquare += S1E1, i++) {
			if (isEmptySqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction south-west
		for (int i = 14, inspectSquare = (pieceSquare + S1W1); (inspectSquare <= 63)
				&& (!isFileASqr(inspectSquare + N1E1)); inspectSquare += S1W1, i++) {
			if (isEmptySqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}

		// Direction north-west
		for (int i = 21, inspectSquare = (pieceSquare + N1W1); (inspectSquare >= 0)
				&& (!isFileASqr(inspectSquare + S1E1)); inspectSquare += N1W1, i++) {
			if (isEmptySqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
			} else if (isOtherColAtSqr(inspectSquare)) {
				tempMoves[i] = (pieceSquare * 100) + inspectSquare;
				numberOfMoves++;
				break;
			} else
				break;
		}
		int[] diagonalMoves = new int[numberOfMoves];
		for (int i = 0, j = 0; j < numberOfMoves; i++, j++) {
			if (tempMoves[i] != 0)
				diagonalMoves[j] = tempMoves[i];
			else
				j--;
		}
		return diagonalMoves;
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
				&& (((move / 100) == ((move % 100) + E2)) || ((move / 100) == ((move % 100) + W2)));
	}

	public boolean isEnPassant(char piece, int move) {
		// Returns true if the move is en-passant
		if (piece == 'P')
			return (move % 100) + S1 == 'e';
		else if (piece == 'p')
			return (move % 100) + N1 == 'E';
		else
			return false;
	}

	public boolean isCheck() {
		// Returns true if the color to move is in check
		return isAttackedSqr(findKingSqr(whiteToPlay));
	}

	public boolean isSelfCheckMove(int move) {
		// Returns true if the move puts the king (of the color who makes that move)
		// into check
		// or castles that king through check
		// **Only works for possible moves**
		Position tempPosition = new Position(this);
		tempPosition.makeMove(move);
		return tempPosition.isAttackedSqr(tempPosition.findKingSqr(this.whiteToPlay))
				|| (isCastling(atSqr(move / 100), move)
						&& tempPosition.isAttackedSqr(((move / 100) + (move % 100)) / 2));
	}

	public boolean isAttackedSqr(int square) {
		// Returns true if the square is attacked by a piece of the opposing color
		int[] tempMoves = new int[0];
		tempMoves = findKnightMoves(square);
		for (int i = 0; i < tempMoves.length; i++) {
			if (whiteToPlay) {
				if (atSqr(tempMoves[i] % 100) == 'N')
					return false;
			} else {
				if (atSqr(tempMoves[i] % 100) == 'n')
					return false;
			}
		}
		tempMoves = findStraightMoves(square);
		for (int i = 0; i < tempMoves.length; i++) {
			if (whiteToPlay) {
				if ((atSqr(tempMoves[i] % 100) == 'R') || (atSqr(tempMoves[i] % 100) == 'Q'))
					return false;
			} else {
				if ((atSqr(tempMoves[i] % 100) == 'r') || (atSqr(tempMoves[i] % 100) == 'q'))
					return false;
			}
		}
		tempMoves = findDiagonalMoves(square);
		for (int i = 0; i < tempMoves.length; i++) {
			if (whiteToPlay) {
				if ((atSqr(tempMoves[i] % 100) == 'B') || (atSqr(tempMoves[i] % 100) == 'Q'))
					return false;
			} else {
				if ((atSqr(tempMoves[i] % 100) == 'b') || (atSqr(tempMoves[i] % 100) == 'q'))
					return false;
			}
		}
		// Assessing possible pawn attacks
		if (whiteToPlay && !isRank1Sqr(square)) {
			if (!isFileASqr(square) && ((atSqr(square + S1E1) == 'P') || (atSqr(square + S1E1) == 'E')))
				return false;
			else if (!isFileHSqr(square) && ((atSqr(square + S1W1) == 'P') || (atSqr(square + S1W1) == 'E')))
				return false;
		} else if (!isRank8Sqr(square)) {
			if (!isFileASqr(square) && ((atSqr(square + N1W1) == 'p') || (atSqr(square + N1W1) == 'e')))
				return false;
			else if (!isFileHSqr(square) && ((atSqr(square + N1E1) == 'p') || (atSqr(square + N1E1) == 'e')))
				return false;
		}
		return ((atSqr(square) == 'e') || (atSqr(square) == 'E'));
	}

	public int findKingSqr(boolean kingColor) {
		// Returns the king square of the color passed (white is true)
		int kingSquare = 0;
		for (int i = 0; i < board.length(); i++) {
			if (kingColor) {
				if ((atSqr(i) == 'K') || (atSqr(i) == '5') || (atSqr(i) == '4') || (atSqr(i) == '3')) {
					kingSquare = i;
					break;
				}
			} else {
				if ((atSqr(i) == 'k') || (atSqr(i) == '2') || (atSqr(i) == '1') || (atSqr(i) == '0')) {
					kingSquare = i;
					break;
				}
			}
		}
		return kingSquare;
	}

	public boolean isOuterEdgeSqr(int square) {
		// Returns true if the square is one of the 28 squares on the outer edge of the
		// board
		return (isRank1Sqr(square)) || (isRank8Sqr(square)) || (isFileASqr(square)) || (isFileHSqr(square));
	}

	public boolean isInner4Sqr(int square) {
		// Returns true if the square is one of the 4 center squares
		return (square == 27) || (square == 28) || (square == 35) || (square == 36);
	}

	public boolean isInner16Sqr(int square) {
		// Returns true if the square is one of the 16 center squares
		return ((square >= 18) && (square <= 21)) || ((square >= 26) && (square <= 29))
				|| ((square >= 34) && (square <= 37)) || ((square >= 42) && (square <= 45));
	}

	public boolean isRank1Sqr(int square) {
		// Returns true if the square is on the 1st rank of the board
		return (square >= 56) && (square <= 63);
	}

	public boolean isRank2Sqr(int square) {
		// Returns true if the square is on the 2nd rank of the board
		return (square >= 48) && (square <= 55);
	}

	public boolean isRank7Sqr(int square) {
		// Returns true if the square is on the 7th rank of the board
		return (square >= 8) && (square <= 15);
	}

	public boolean isRank8Sqr(int square) {
		// Returns true if the square is on the 8th rank of the board
		return (square >= 0) && (square <= 7);
	}

	public boolean isFileASqr(int square) {
		// Returns true if the square is on file A of the board
		return square % 8 == 0;
	}

	public boolean isFileBSqr(int square) {
		// Returns true if the square is on file B of the board
		return (square + W1) % 8 == 0;
	}

	public boolean isFileGSqr(int square) {
		// Returns true if the square is on file G of the board
		return (square + E2) % 8 == 0;
	}

	public boolean isFileHSqr(int square) {
		// Returns true if the square is on file H of the board
		return (square + E1) % 8 == 0;
	}

	public void updateSqr(char charToPut, int square) {
		// Updates the square either to empty or to the piece that is passed
		board = board.substring(0, square) + charToPut + board.substring(square + 1);
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

	public int getPieceValue(char piece, int pieceSquare) {
		// Returns a int based on the value of any piece (normal value and square
		// on the board)
		// This int is positive if the piece is white and negative if black
		int value = 0;
		switch (piece) {
		case '-':
			break;

		case 'P':
		case 'E':
		case 'p':
		case 'e':
			value = getPawnValue(pieceSquare);
			break;

		case 'R':
		case 'r':
			value = getRookValue(pieceSquare);
			break;

		case 'N':
		case 'n':
			value = getKnightValue(pieceSquare);
			break;

		case 'B':
		case 'b':
			value = getBishopValue(pieceSquare);
			break;

		case 'Q':
		case 'q':
			value = getQueenValue(pieceSquare);
			break;

		case '3':
		case '4':
		case '5':
		case 'K':
		case '0':
		case '1':
		case '2':
		case 'k':
			value = getKingValue(pieceSquare);
			break;
		}
		if (isBlackPiece(piece))
			value *= -1;
		return value;
	}

	public int getPawnValue(int pawnSquare) {
		int value = 100;
		if ((isFileASqr(pawnSquare)) || (isFileHSqr(pawnSquare)))
			value -= 25;
		else if (isInner4Sqr(pawnSquare))
			value += 60;
		return value;
	}

	public int getRookValue(int rookSquare) {
		int value = 500;

		return value;
	}

	public int getKnightValue(int knightSquare) {
		int value = 275;
		if (isOuterEdgeSqr(knightSquare))
			value -= 35;
		else if (isInner16Sqr(knightSquare))
			value += 20;
		return value;
	}

	public int getBishopValue(int bishopSquare) {
		int value = 300;
		if (isInner16Sqr(bishopSquare))
			value += 15;
		return value;
	}

	public int getQueenValue(int queenSquare) {
		int value = 900;

		return value;
	}

	public int getKingValue(int kingSquare) {
		int value = 1000000;
		if ((kingSquare == 62) || (kingSquare == 58) || (kingSquare == 2) || (kingSquare == 7))
			value += 20;
		return value;
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
