package chessengine.system;

import java.io.Serial;

/**
 * Thrown to indicate an attempt to construct an illegal <code>Move</code> when there are legal moves to make in this position.
 * 
 * @author Darcy McCoy
 * @since 1.0
 */
public class IllegalMoveException extends Exception {

	/** Version number. */
	@Serial
	private static final long serialVersionUID = -7663667980792845092L;

	/**
	 *  Constructs an <code>IllegalMoveException</code> without a message.
	 */
	public IllegalMoveException() {
		super();
	}

	/**
	 * Constructs an <code>IllegalMoveException</code> with a detail message.
	 * 
	 * @param message String detail message
	 */
	public IllegalMoveException(String message) {
		super(message);
	}

}
