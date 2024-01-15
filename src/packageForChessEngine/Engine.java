package packageForChessEngine;

//Written by Darcy McCoy
//Starting November 27, 2023

public class Engine {
	// The values for each piece (each integer corresponds to a square on the board)
	static final int[] PAWN_VALUES = {0, 0, 0, 0, 0, 0, 0, 0, 
			140, 140, 140, 140, 140, 140, 140, 140, 
			110, 110, 110, 110, 110, 110, 110, 110, 
			100, 100, 100, 120, 120, 100, 100, 100, 
			80, 100, 100, 120, 120, 100, 100, 80, 
			80, 100, 100, 105, 105, 100, 100, 80, 
			90, 100, 100, 90, 90, 100, 100, 90, 
			0, 0, 0, 0, 0, 0, 0, 0};
	static final int[] ROOK_VALUES = {500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 515, 520, 505, 500, 500,};
	static final int[] KNIGHT_VALUES = {200, 220, 220, 220, 220, 220, 220, 200, 
			200, 270, 300, 300, 300, 300, 270, 200, 
			200, 250, 320, 320, 320, 320, 250, 200, 
			200, 250, 310, 340, 340, 310, 250, 200, 
			200, 250, 310, 340, 340, 310, 250, 200, 
			200, 250, 310, 310, 310, 310, 250, 200, 
			200, 250, 250, 250, 250, 250, 250, 200, 
			100, 210, 210, 210, 210, 210, 210, 100};
	static final int[] BISHOP_VALUES = {280, 280, 280, 280, 280, 280, 280, 280, 
			280, 310, 310, 310, 310, 310, 310, 280, 
			280, 315, 315, 315, 315, 315, 315, 280, 
			290, 320, 320, 320, 320, 320, 320, 290, 
			290, 320, 335, 320, 320, 335, 320, 290, 
			280, 320, 320, 330, 330, 320, 320, 280, 
			280, 340, 310, 310, 310, 310, 340, 280, 
			280, 280, 280, 280, 280, 280, 280, 280};
	static final int[] KING_VALUES = {100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100030, 100020, 100000, 100000, 100000, 100030, 100000};
	
	public Engine() {
		// Default constructor
	}
	
	public int findTopMove(Position currentPosition) {
		// Returns an integer which is the top move for a position
		// **If no legal moves are found, -1 will be returned**
		int topMove = -1;
		int topMoveMaxReply = -10000;
		int maxReply = -10000;
		int[] legalMovesDepth1 = currentPosition.findLegalMoves();
		
		for (int i = 0; i < legalMovesDepth1.length; i++) {
			maxReply = -10000;
			Position tempPositionDepth1 = new Position(currentPosition);
			tempPositionDepth1.makeMove(legalMovesDepth1[i]);
			
			int[] legalMovesDepth2 = tempPositionDepth1.findLegalMoves();
			for (int j = 0; j < legalMovesDepth2.length; j++) {
				Position tempPositionDepth2 = new Position(tempPositionDepth1);
				tempPositionDepth2.makeMove(legalMovesDepth2[j]);
			
				if (evaluatePosition(tempPositionDepth2) > maxReply)
					maxReply = evaluatePosition(tempPositionDepth2);
			}
			
			if (maxReply > topMoveMaxReply) {
				topMove = legalMovesDepth1[i];
				topMoveMaxReply = maxReply;
			}
		}
		return topMove;
	}
	
	public int evaluatePosition(Position currentPosition) {
		// Returns an integer (in centipawns) which is an evaluation of the position
		// from the perspective of the color who is to play
		int positionEvaluation = 0;
		for (int i = 0; i < currentPosition.getBoard().length(); i++) {
			positionEvaluation += getPieceValue(currentPosition.atSqr(i), i);
		}
		if (currentPosition.isWhiteToPlay())
			return positionEvaluation;
		else
			return positionEvaluation * -1;
	}
	
	public int getPieceValue(char piece, int square) {
		// Returns a int based on the value of any piece
		// This int is positive if the piece is white and negative if black
		switch (piece) {
		case 'P':
			return PAWN_VALUES[square];
		case 'p':
			return PAWN_VALUES[PAWN_VALUES.length - 1 - square] * -1;
		case 'R':
			return ROOK_VALUES[square];
		case 'r':
			return ROOK_VALUES[ROOK_VALUES.length - 1 - square] * -1;
		case 'N':
			return KNIGHT_VALUES[square];
		case 'n':
			return KNIGHT_VALUES[KNIGHT_VALUES.length - 1 - square] * -1;
		case 'B':
			return BISHOP_VALUES[square];
		case 'b':
			return BISHOP_VALUES[BISHOP_VALUES.length - 1 - square] * -1;
		case 'K':
		case '5':
		case '4':
		case '3':
			return KING_VALUES[square];
		case 'k':
		case '2':
		case '1':
		case '0':
			return KING_VALUES[KING_VALUES.length - 1 - square] * -1;
		default:
			return 0;
		}
	}
}
