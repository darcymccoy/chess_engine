package chessengine.system;

/**
 * Stores the ability for both sides to castle kingside or queenside. 
 * 
 * @author Darcy McCoy
 * @since 1.0
 */
public class CastlingRights {
	
	/** Whether white can castle kingside. */
	private boolean whiteCanKingside;
	
	/** Whether white can castle queenside. */
	private boolean whiteCanQueenside;
	
	/** Whether black can castle kingside. */
	private boolean blackCanKingside;
	
	/** Whether black can castle queenside. */
	private boolean blackCanQueenside;
	
	/**
	 * Class constructor that sets the rights for the standard starting position.
	 */
	public CastlingRights() {
		this(true, true, true, true);
	}
	
	/**
	 * Class constructor that specifies all 4 castling rights.
	 * 
	 * @param whiteCanKingside boolean whether white can castle kingside
	 * @param whiteCanQueenside boolean whether white can castle queenside
	 * @param blackCanKingside boolean whether black can castle kingside
	 * @param blackCanQueenside boolean whether black can castle queenside
	 */
	public CastlingRights(boolean whiteCanKingside, boolean whiteCanQueenside, boolean blackCanKingside,
			boolean blackCanQueenside) {
		this.whiteCanKingside = whiteCanKingside;
		this.whiteCanQueenside = whiteCanQueenside;
		this.blackCanKingside = blackCanKingside;
		this.blackCanQueenside = blackCanQueenside;
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param otherRights CastlingRights to copy
	 */
	public CastlingRights(CastlingRights otherRights) {
		this(otherRights.whiteCanKingside, otherRights.whiteCanQueenside, 
				otherRights.blackCanKingside, otherRights.blackCanQueenside);
	}
	
	@Override
	public CastlingRights clone() {
		return new CastlingRights(this);
	}
	
	/**
	 * Updates both colors' castling abilities for king and non king moves.
	 * 
	 * @param move the <code>Move</code> to update for
	 */
	public void updateRightsForMove(Move move){
		updateWhiteRights(move);
		updateBlackRights(move);
	}
	
	/**
	 * Updates white's castling abilities for king and non king moves.
	 * 
	 * @param move the <code>Move</code> to update for
	 */
	private void updateWhiteRights(Move move) {
		if (move.getPiece() == Chess.WH_KING) {
			whiteCanKingside = false;
			whiteCanQueenside = false;
		} else if (whiteCanKingside && ((move.getStartSqr() == Board.H1_SQR) || (move.getEndSqr() == Board.H1_SQR))) {
			whiteCanKingside = false;
		} else if (whiteCanQueenside && ((move.getStartSqr() == Board.A1_SQR) || (move.getEndSqr() == Board.A1_SQR))) {
			whiteCanQueenside = false;
		}
	}
	
	/**
	 * Updates black's castling abilities for king and non king moves.
	 * 
	 * @param move the <code>Move</code> to update for
	 */
	private void updateBlackRights(Move move) {
		if (move.getPiece() == Chess.WH_KING) {
			whiteCanKingside = false;
			whiteCanQueenside = false;
		} else if (blackCanKingside && ((move.getStartSqr() == Board.H8_SQR) || (move.getEndSqr() == Board.H8_SQR))) {
			blackCanKingside = false;
		} else if (blackCanQueenside && ((move.getStartSqr() == Board.A8_SQR) || (move.getEndSqr() == Board.A8_SQR))) {
			blackCanQueenside = false;
		}
	}
	
	/**
	 * Returns true if there is a king with the ability to castle kingside on this square.
	 * 
	 * @param kingSquare int index of the king
	 * @return <code>true</code> if there is a king that can castle kingside on this square;
	 *         <code>false</code> otherwise.
	 */
	public boolean kingCanCastleKingside(int kingSquare) {
		return ((kingSquare == Board.E1_SQR) && whiteCanKingside) || 
				((kingSquare == Board.E8_SQR) && blackCanKingside);
	}
	
	/**
	 * Returns true if there is a king with the ability to castle queenside on this square.
	 * 
	 * @param kingSquare int index of the king
	 * @return <code>true</code> if there is a king that can castle queenside on this square;
	 *         <code>false</code> otherwise.
	 */
	public boolean kingCanCastleQueenside(int kingSquare) {
		return ((kingSquare == Board.E1_SQR) && whiteCanQueenside) || 
				((kingSquare == Board.E8_SQR) && blackCanQueenside);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CastlingRights other = (CastlingRights) obj;
		return blackCanKingside == other.blackCanKingside && blackCanQueenside == other.blackCanQueenside
				&& whiteCanKingside == other.whiteCanKingside && whiteCanQueenside == other.whiteCanQueenside;
	}
	
}
