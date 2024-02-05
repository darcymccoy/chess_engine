package packageForChessEngine;

//Written by Darcy McCoy
//Starting November 27, 2023

public class Engine {
	// Piece values (each integer corresponds to a square on the board)
	private static final int[] PAWN_VALUES = {0, 0, 0, 0, 0, 0, 0, 0, 
			140, 140, 140, 140, 140, 140, 140, 140, 
			110, 110, 110, 120, 120, 110, 110, 110, 
			100, 100, 100, 120, 120, 100, 100, 100, 
			80, 100, 100, 120, 120, 100, 100, 80, 
			95, 100, 100, 110, 110, 100, 100, 96, 
			90, 100, 100, 90, 90, 100, 100, 90, 
			0, 0, 0, 0, 0, 0, 0, 0};
	private static final int[] ROOK_VALUES = {500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 519, 520, 505, 500, 500,};
	private static final int[] KNIGHT_VALUES = {200, 220, 220, 220, 220, 220, 220, 200, 
			200, 270, 300, 300, 300, 300, 270, 200, 
			200, 250, 320, 320, 320, 320, 250, 200, 
			200, 250, 310, 326, 326, 310, 250, 200, 
			200, 250, 310, 325, 325, 310, 250, 200, 
			200, 250, 309, 310, 310, 310, 250, 200, 
			200, 250, 250, 250, 250, 250, 250, 200, 
			100, 225, 215, 215, 215, 215, 225, 100};
	private static final int[] BISHOP_VALUES = {280, 280, 280, 280, 280, 280, 280, 280, 
			280, 310, 310, 310, 310, 310, 310, 280, 
			280, 315, 315, 315, 315, 315, 315, 280, 
			290, 320, 320, 320, 320, 320, 320, 290, 
			290, 320, 335, 320, 320, 335, 320, 290, 
			280, 320, 320, 330, 330, 320, 320, 280, 
			280, 340, 310, 310, 310, 310, 340, 280, 
			280, 280, 280, 280, 280, 280, 280, 280};
	private static final int[] QUEEN_VALUES = {900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 906, 906, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900};
	private static final int[] KING_VALUES = {100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100040, 100030, 100000, 100000, 100000, 100040, 100000};
	// Piece values for endgames
	private static final int[] PAWN_ENDGAME_VALUES = {0, 0, 0, 0, 0, 0, 0, 0, 
			160, 160, 160, 160, 160, 160, 160, 160, 
			140, 135, 130, 130, 130, 130, 135, 140, 
			120, 120, 120, 120, 120, 120, 120, 120, 
			110, 110, 110, 120, 120, 110, 110, 110, 
			100, 105, 105, 110, 110, 105, 105, 100, 
			90, 100, 100, 90, 90, 100, 100, 90, 
			0, 0, 0, 0, 0, 0, 0, 0};
	private static final int[] KING_ENDGAME_VALUES = {100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100010, 100010, 100010, 100010, 100010, 100010, 100000, 
			100000, 100010, 100030, 100030, 100030, 100030, 100010, 100000, 
			100000, 100010, 100030, 100040, 100040, 100030, 100010, 100000, 
			100000, 100010, 100030, 100040, 100040, 100030, 100010, 100000, 
			100000, 100010, 100030, 100030, 100030, 100030, 100010, 100000, 
			100000, 100010, 100010, 100010, 100010, 100010, 100010, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000};
	
	public Engine() {
		// Default constructor
	}
	
	public int findTopMoveDepth3(Position currentPosition) {
		// Returns the top move for a position
		// **If no legal moves are found, -1 will be returned**
		int topMove = -1;
		int topMoveMaxReply = 100000;
		int maxReplyDepth1 = -100000;
		int maxReplyDepth2 = 100000;
		
		int[] legalMovesDepth1 = currentPosition.findLegalMoves();
		for (int i = 0; i < legalMovesDepth1.length; i++) {
			maxReplyDepth1 = -100000;
			Position tempPositionDepth1 = new Position(currentPosition);
			tempPositionDepth1.makeMove(legalMovesDepth1[i]);
			
			int[] legalMovesDepth2 = tempPositionDepth1.findLegalMoves();
			for (int j = 0; j < legalMovesDepth2.length; j++) {
				maxReplyDepth2 = 100000;
				Position tempPositionDepth2 = new Position(tempPositionDepth1);
				tempPositionDepth2.makeMove(legalMovesDepth2[j]);
			
				int[] legalMovesDepth3 = tempPositionDepth2.findLegalMoves();
				for (int k = 0; k < legalMovesDepth3.length; k++) {
					Position tempPositionDepth3 = new Position(tempPositionDepth2);
					tempPositionDepth3.makeMove(legalMovesDepth3[k]);
					
					int evaluationDepth3 = evaluatePosition(tempPositionDepth3) 
							+ moveBonusesAndPenalties(tempPositionDepth3, legalMovesDepth3[k]);
					if (evaluationDepth3 < maxReplyDepth2)
						maxReplyDepth2 = evaluationDepth3;
				}
				
				if (maxReplyDepth2 > maxReplyDepth1)
					maxReplyDepth1 = maxReplyDepth2;
			}
			
			if (maxReplyDepth1 < topMoveMaxReply) {
				topMoveMaxReply = maxReplyDepth1;
				topMove = legalMovesDepth1[i];
			}
		}
		return topMove;
	}
	
	public int findTopMoveDepth1(Position currentPosition) {
		// Returns an integer which is the top move for a position
		// **If no legal moves are found, -1 will be returned**
		int topMove = -1;
		int topMoveEvaluation = 10000;
		int[] legalMovesDepth1 = currentPosition.findLegalMoves();
		
		for (int i = 0; i < legalMovesDepth1.length; i++) {
			Position tempPositionDepth1 = new Position(currentPosition);
			tempPositionDepth1.makeMove(legalMovesDepth1[i]);
			
			if (evaluatePosition(tempPositionDepth1) < topMoveEvaluation) {
				topMove = legalMovesDepth1[i];
				topMoveEvaluation = evaluatePosition(tempPositionDepth1);
			}
		}
		
		return topMove;
	}
	
	public int moveBonusesAndPenalties(Position position, int move) {
		// Returns an integer which represents the bonuses and penalties for 
		// a move (that has been played on the position)
		int bonusesAndPenalties = 0;
		
		if (position.isCheck())
			bonusesAndPenalties += 15;
		if (position.isAttackedSqr(move % 100, position.isWhiteToPlay()))
			bonusesAndPenalties -= 10;
		if (position.isAttackedSqr(move % 100, !position.isWhiteToPlay()))
			bonusesAndPenalties += 10;
		
		return bonusesAndPenalties;
	}
	
	public int evaluatePosition(Position position) {
		// Returns an integer (in centipawns) which is an evaluation of the position
		// from the perspective of the color who is to play
		int positionEvaluation = 0;
		for (int i = 0; i < position.getBoard().length(); i++) {
			positionEvaluation += getPieceValue(position.atSqr(i), i);
		}
		if (position.isWhiteToPlay())
			return positionEvaluation;
		else
			return positionEvaluation * -1;
	}
	
	public int getPieceValue(char piece, int square) {
		// Returns a int based on the value of any piece
		// This int is positive if the piece is white and negative if black
		switch (piece) {
		case 'P':
		case 'E':
			return PAWN_VALUES[square];
		case 'p':
		case 'e':
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
		case 'Q':
			return QUEEN_VALUES[square];
		case 'q':
			return QUEEN_VALUES[QUEEN_VALUES.length - 1 - square] * -1;
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
