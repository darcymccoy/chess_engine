package chessengine.system;

/**
 * Calculation and evaluation engine for a chess position.
 * This class does not contain or store a chess position, and must 
 * be passed a legal chess position to return accurate results.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public class Engine {
	/** Pawn values based on location (each integer corresponds to a square on the board). */
	private static final int[] PAWN_VALUES = {0, 0, 0, 0, 0, 0, 0, 0, 
			140, 140, 140, 140, 140, 140, 140, 140, 
			110, 110, 110, 120, 120, 110, 110, 110, 
			100, 100, 100, 120, 120, 100, 100, 100, 
			80, 100, 100, 120, 120, 100, 100, 80, 
			95, 100, 100, 110, 110, 100, 100, 96, 
			90, 100, 100, 90, 90, 100, 100, 90, 
			0, 0, 0, 0, 0, 0, 0, 0};
	
	/** Rook values based on location (each integer corresponds to a square on the board). */
	private static final int[] ROOK_VALUES = {500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 500, 500, 500, 500, 500, 
			500, 500, 500, 519, 520, 505, 500, 500,};
	
	/** Knight values based on location (each integer corresponds to a square on the board). */
	private static final int[] KNIGHT_VALUES = {200, 220, 220, 220, 220, 220, 220, 200, 
			200, 270, 300, 300, 300, 300, 270, 200, 
			200, 250, 320, 320, 320, 320, 250, 200, 
			200, 250, 310, 326, 326, 310, 250, 200, 
			200, 250, 310, 325, 325, 310, 250, 200, 
			200, 250, 309, 310, 310, 310, 250, 200, 
			200, 250, 250, 250, 250, 250, 250, 200, 
			100, 225, 215, 215, 215, 215, 225, 100};
	
	/** Bishop values based on location (each integer corresponds to a square on the board). */
	private static final int[] BISHOP_VALUES = {280, 280, 280, 280, 280, 280, 280, 280, 
			280, 310, 310, 310, 310, 310, 310, 280, 
			280, 315, 315, 315, 315, 315, 315, 280, 
			290, 320, 320, 320, 320, 320, 320, 290, 
			290, 320, 335, 320, 320, 335, 320, 290, 
			280, 320, 320, 330, 330, 320, 320, 280, 
			280, 340, 310, 310, 310, 310, 340, 280, 
			280, 280, 280, 280, 280, 280, 280, 280};
	
	/** Queen values based on location (each integer corresponds to a square on the board). */
	private static final int[] QUEEN_VALUES = {900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900,
			900, 900, 900, 906, 906, 900, 900, 900,
			900, 900, 900, 900, 900, 900, 900, 900};
	
	/** King values based on location (each integer corresponds to a square on the board). */
	private static final int[] KING_VALUES = {100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100040, 100030, 100000, 100000, 100000, 100040, 100000};
	
	/** Pawn endgame values based on location (each integer corresponds to a square on the board). */
	private static final int[] PAWN_ENDGAME_VALUES = {0, 0, 0, 0, 0, 0, 0, 0, 
			160, 160, 160, 160, 160, 160, 160, 160, 
			140, 135, 130, 130, 130, 130, 135, 140, 
			120, 120, 120, 120, 120, 120, 120, 120, 
			110, 110, 110, 120, 120, 110, 110, 110, 
			100, 105, 105, 110, 110, 105, 105, 100, 
			90, 100, 100, 90, 90, 100, 100, 90, 
			0, 0, 0, 0, 0, 0, 0, 0};
	
	/** King endgame values based on location (each integer corresponds to a square on the board). */
	private static final int[] KING_ENDGAME_VALUES = {100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000, 
			100000, 100010, 100010, 100010, 100010, 100010, 100010, 100000, 
			100000, 100010, 100030, 100030, 100030, 100030, 100010, 100000, 
			100000, 100010, 100030, 100040, 100040, 100030, 100010, 100000, 
			100000, 100010, 100030, 100040, 100040, 100030, 100010, 100000, 
			100000, 100010, 100030, 100030, 100030, 100030, 100010, 100000, 
			100000, 100010, 100010, 100010, 100010, 100010, 100010, 100000, 
			100000, 100000, 100000, 100000, 100000, 100000, 100000, 100000};
	
	/**
	 * Default constructor.
	 */
	public Engine() {
	}
	
	/**
	 * Returns the top move (as decided by the engine) for a position. 
	 * The engine searches to a hard coded depth of 3. 
	 * If no legal moves are found, a {@code NoLegalMovesException} will be thrown.
	 * 
	 * @param currentPosition the <code>Position</code> to be searched for the top move
	 * @return <code>Move</code> the top move
	 * @throws NoLegalMovesException if there are no legal moves in the position
	 */
	public Move findTopMoveDepth3(Position currentPosition) throws NoLegalMovesException {
		Move topMove = null;
		int topMoveMaxReply = 1000000;
		int maxReplyDepth1 = -1000000;
		int maxReplyDepth2 = 1000000;
		
		Move[] legalMovesDepth1 = currentPosition.findLegalMoves();
		for (int i = 0; i < legalMovesDepth1.length; i++) {
			maxReplyDepth1 = -1000000;
			Position tempPositionDepth1 = currentPosition.clone();
			tempPositionDepth1.makeMove(legalMovesDepth1[i]);
			
			Move[] legalMovesDepth2 = tempPositionDepth1.findLegalMoves();
			for (int j = 0; j < legalMovesDepth2.length; j++) {
				maxReplyDepth2 = 1000000;
				Position tempPositionDepth2 = tempPositionDepth1.clone();
				tempPositionDepth2.makeMove(legalMovesDepth2[j]);
			
				Move[] legalMovesDepth3 = tempPositionDepth2.findLegalMoves();
				for (int k = 0; k < legalMovesDepth3.length; k++) {
					Position tempPositionDepth3 = tempPositionDepth2.clone();
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
	
	/**
	 * Returns the top move (as decided by the engine) for a position. 
	 * The engine searches to a hard coded depth of 1. 
	 * If no legal moves are found, a {@code NoLegalMovesException} will be thrown.
	 * 
	 * @param currentPosition the <code>Position</code> to be searched for the top move
	 * @return <code>Move</code> the top move
	 * @throws NoLegalMovesException if there are no legal moves in the position
	 */
	public Move findTopMoveDepth1(Position currentPosition) throws NoLegalMovesException {
		Move topMove = null;
		int topMoveEvaluation = 10000;
		Move[] legalMovesDepth1 = currentPosition.findLegalMoves();
		
		for (int i = 0; i < legalMovesDepth1.length; i++) {
			Position tempPositionDepth1 = currentPosition.clone();
			tempPositionDepth1.makeMove(legalMovesDepth1[i]);
			
			if (evaluatePosition(tempPositionDepth1) < topMoveEvaluation) {
				topMove = legalMovesDepth1[i];
				topMoveEvaluation = evaluatePosition(tempPositionDepth1);
			}
		}
		return topMove;
	}
	
	/**
	 * Returns an integer which is an evaluation of the position
	 * from the perspective of the color who is to play (in centipawns). A centipawn is one one-hundredth
	 * of a pawns value. For example, conventionally a bishop is worth 3 points, but in a centipawn 
	 * system a bishop is worth 300.
	 * 
	 * @param position the <code>Position</code> to be evaluated from the perspective of the color who is to play
	 * @return <code>int</code> that will be positive if the position is better for the color who is to play; 
	 * 			negative otherwise
	 */
	public int evaluatePosition(Position position) {
		int positionEvaluation = 0;
		for (int i = 0; i < position.getBoard().length(); i++) {
			positionEvaluation += getPieceValue(position.atSqr(i), i);
		}
		if (position.isWhiteToPlay())
			return positionEvaluation;
		else
			return positionEvaluation * -1;
	}
	
	/**
	 * Returns an integer which represents the bonuses and penalties for
	 * a move (that has been played on the position). This integer is from the 
	 * perspective of the color who is to play after the move has been made.
	 * For example, a move which puts the opposing king into check is (generally) bad for the 
	 * checked color so, for this move, some negative integer would be added to the bonus and penalty total.
	 * This negative integer represents that the move aided the color making it and put the opposing 
	 * color into a worse position.
	 * 
	 * @param position the <code>Position</code> after the move has been made on it
	 * @param move the <code>Move</code> that has been made
	 * @return an <code>int</code> total of the bonuses and penalties that the move incurred
	 */
	public int moveBonusesAndPenalties(Position position, Move move) {
		int bonusesAndPenalties = 0;
		
		if (position.isCheck())
			bonusesAndPenalties -= 15;
		if (position.isAttackedSqr(move.getEndSqr(), position.isWhiteToPlay()))
			bonusesAndPenalties += 10;
		if (position.isAttackedSqr(move.getEndSqr(), !position.isWhiteToPlay()))
			bonusesAndPenalties -= 10;
		
		return bonusesAndPenalties;
	}
	
	/**
	 * Returns an integer based on the type and location of a piece (in centipawns).
	 * This integer is positive if the piece is white and negative if black.
	 * 
	 * 
	 * @param piece a character representing the piece of the piece value to be returned
	 * @param sqr int value of the square the piece is on in the current position
	 * @return an integer which is the value of the piece based on type and location
	 */
	public int getPieceValue(char piece, int sqr) {
		switch (piece) {
		case 'P':
		case 'E':
			return PAWN_VALUES[sqr];
		case 'p':
		case 'e':
			return PAWN_VALUES[PAWN_VALUES.length - 1 - sqr] * -1;
		case 'R':
			return ROOK_VALUES[sqr];
		case 'r':
			return ROOK_VALUES[ROOK_VALUES.length - 1 - sqr] * -1;
		case 'N':
			return KNIGHT_VALUES[sqr];
		case 'n':
			return KNIGHT_VALUES[KNIGHT_VALUES.length - 1 - sqr] * -1;
		case 'B':
			return BISHOP_VALUES[sqr];
		case 'b':
			return BISHOP_VALUES[BISHOP_VALUES.length - 1 - sqr] * -1;
		case 'Q':
			return QUEEN_VALUES[sqr];
		case 'q':
			return QUEEN_VALUES[QUEEN_VALUES.length - 1 - sqr] * -1;
		case 'K':
		case '5':
		case '4':
		case '3':
			return KING_VALUES[sqr];
		case 'k':
		case '2':
		case '1':
		case '0':
			return KING_VALUES[KING_VALUES.length - 1 - sqr] * -1;
		default:
			return 0;
		}
	}
}
