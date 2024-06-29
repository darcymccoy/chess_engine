package chessengine.system;

/**
 * Stores the ability for both sides to castle kingside or queenside. 
 * 
 * @author Darcy McCoy
 * @since 1.0
 */
public class EnPassantRights {
	
	private static final int NO_CAPTURABLE_SQR = -1;
	
	/** Index of the square of a pawn that can be captured en passant. */
	private int capturableSqr;
	
	/**
	 * Class constructor that sets the rights for the standard starting position.
	 */
	public EnPassantRights() {
		this(NO_CAPTURABLE_SQR);
	}
	
	/**
	 * Class constructor specifying the en passant capturable square.
	 * 
	 * @param capturableSqr index of the square of the pawn that can be captured en passant
	 */
	public EnPassantRights(int capturableSqr) {
		this.capturableSqr = capturableSqr;
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param otherRights EnPassantRights to copy
	 */
	public EnPassantRights(EnPassantRights otherRights) {
		this(otherRights.capturableSqr);
	}
	
	@Override
	public EnPassantRights clone() {
		return new EnPassantRights(this);
	}

	/**
	 * Returns true if there is a pawn at this square that can be captured en passant.
	 * 
	 * @param sqr index of the square to be tested
	 * @return <code>true</code> if a pawn at this square can be captured en passant;
	 *         <code>false</code> otherwise.
	 */
	public boolean isCapturableSqr(int sqr) {
		return capturableSqr == sqr;
	}
	
	/**
	 * Sets a square as capturable by en passant.
	 * 
	 * @param sqr index of the square to set as capturable
	 */
	public void setCapturableSqr(int sqr) {
		capturableSqr = sqr;
	}
	
	/**
	 * Updates the rights so that a pawn that could have been captured en passant
	 * cannot be captured en passant anymore.
	 */
	public void removeCapturability() {
		capturableSqr = NO_CAPTURABLE_SQR;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnPassantRights other = (EnPassantRights) obj;
		return capturableSqr == other.capturableSqr;
	}
	
}
