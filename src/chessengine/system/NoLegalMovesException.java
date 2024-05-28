package chessengine.system;

import java.io.Serial;

/**
 * Exception that is thrown whenever an attempt
 * is made to find moves when there are no legal moves to make.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public class NoLegalMovesException extends Exception {

	/** Version number. */
	@Serial
	private static final long serialVersionUID = -8809432195921500767L;

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
