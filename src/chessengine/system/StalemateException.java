/**
 * 
 */
package chessengine.system;

import java.io.Serial;

/**
 * Exception that is thrown whenever a color has no legal moves and that color's king is not attacked.
 * 
 * @author Darcy McCoy
 * @since 1.0
 */
public class StalemateException extends NoLegalMovesException {

	/** Version number. */
	@Serial
	private static final long serialVersionUID = 8713615460515876547L;

	/**
	 * Constructs a <code>StalemateException</code> without a message.
	 */
	public StalemateException() {
		super();
	}

	/**
	 * Constructs a <code>StalemateException</code> with a detail message.
	 * 
	 * @param message String detail message
	 */
	public StalemateException(String message) {
		super(message);
	}

}
