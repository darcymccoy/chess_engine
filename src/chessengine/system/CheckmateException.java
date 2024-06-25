package chessengine.system;

import java.io.Serial;

/**
 * Exception that is thrown whenever a color has no legal moves and that color's king is attacked.
 * 
 * @author Darcy McCoy
 * @since 1.0
 */
public class CheckmateException extends NoLegalMovesException {

	/** Version number. */
	@Serial
	private static final long serialVersionUID = -1632580352922300641L;

	/**
	 * Constructs a <code>CheckmateException</code> without a message.
	 */
	public CheckmateException() {
		super();
	}

	/**
	 * Constructs a <code>CheckmateException</code> with a detail message.
	 * 
	 * @param message String detail message
	 */
	public CheckmateException(String message) {
		super(message);
	}

}
