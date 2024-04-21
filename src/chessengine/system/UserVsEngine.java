package chessengine.system;

/**
 * A game of type: user playing against engine.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public class UserVsEngine extends Game {
	/**
	 * Default constructor.
	 */
	public UserVsEngine() {
		super();
	}
	
	/**
	 * Starts the userVsEngine game. This game will end automatically when a game ending 
	 * situation is reached.
	 */
	public void play() {
		startGame();
		if (userChoosesToPlayWhite())
			letUserMakeMove();
		
		while(inGame) {
			letEngineMakeMove();
			System.out.println(this);
			letUserMakeMove();
		}
	}
	
}
