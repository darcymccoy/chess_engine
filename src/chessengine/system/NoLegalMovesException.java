package chessengine.system;

/**
 * Custom exception that is thrown whenever an attempt
 * is made to find moves when there are no legal moves to make.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public class NoLegalMovesException extends Exception {
	/** The serial version. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a <code>NoLegalMovesException</code> without a message.
	 */
	public NoLegalMovesException() {
		super();
	}

	/**
	 * Constructs a <code>NoLegalMovesException</code> with a detail message.
	 * 
	 * @param message String detail message
	 */
	public NoLegalMovesException(String message) {
		super(message);
	}
}
