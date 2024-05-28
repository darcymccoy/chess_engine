package chessengine.system;

import java.util.LinkedList;
import java.util.Scanner;

/**
 * Abstract class for any type of chess game. 
 * To play a game, this class must be extended and the <code>play()</code> method be implemented.
 * 
 * @author Darcy McCoy
 * @version %I%
 * @since 1.0
 */
public abstract class Game {
	
	/** <code>Scanner</code> to get user input. */
	private static Scanner scanner = new Scanner(System.in);
	
	/** The chess engine which can play against a user or against itself. */
	private static Engine engine = new Engine();
	
	/** The current position in this game. */
	private Position currentPosition;
	
	/** The moves that have previously been made in this game. */
	private LinkedList<Move> movesMade;
	
	/** Whether this game is currently being played. */
	protected boolean inGame;

	private DrawRules drawRules;
	
	/**
	 * Default constructor (Standard starting position, 
	 * no moves have been made and the game hasn't been started.
	 */
	public Game() {
		this(new Position(), new LinkedList<Move>(), false, new DrawRules());
	}

	/**
	 * Parameterized constructor to set the current position, the moves that have been made and whether 
	 * the game is currently.
	 * 
	 * @param currentPosition the <code>Position</code> that the game will start from
	 * @param movesMade <code>LinkedList</code> for the moves that have already been made
	 * @param inGame <code>boolean</code> whether the game is currently being played
	 */
	public Game(Position currentPosition, LinkedList<Move> movesMade, boolean inGame, DrawRules drawRules) {
		this.currentPosition = currentPosition;
		this.movesMade = movesMade;
		this.inGame = inGame;
		this.drawRules = drawRules;
	}
	
	/**
	 * Starts and plays whichever game is extending this class.
	 */
	public abstract void play();

	/**
	 * Sets inGame to true.
	 */
	public void startGame() {
		inGame = true;
	}

	/**
	 * Sets inGame to false.
	 */
	public void stopGame() {
		inGame = false;
	}
	
	/**
	 * Finds the best move according to the engine and 
	 * makes that move on the current position.
	 * If there are no legal moves then the game is ended.
	 */
	public void letEngineMakeMove() {
		try {
			makeGameMove(engine.findTopMove(currentPosition));
		} catch (NoLegalMovesException e) {
			stopGame();
		}
	}
	
	/**
	 * Makes a move on the current game position.
	 * 
	 * @param move the <code>Move</code> to be made
	 */
	private void makeGameMove(Move move) {
		currentPosition.makeMove(move);
		movesMade.add(move);
		if (drawRules.isDraw(movesMade, currentPosition))
			stopGame();
	}
	
	/**
	 * Prompts the user for a move and makes that move on the current position.
	 */
	public void letUserMakeMove() {
		Move userMove = null;
		
		while (inGame) {
			try {
				System.out.print("Enter the starting square of your move: ");
				int startSqr = scanner.nextInt();
				System.out.print("Enter the ending square of your move: ");
				int endSqr = scanner.nextInt();
				userMove = new Move(currentPosition.getSqr(startSqr), startSqr, endSqr, currentPosition.getSqr(endSqr));
				if (userMove.isPromotion()) {
					letUserChoosePromotionType(userMove);
				}
			} catch(RuntimeException e) {
				scanner.nextLine();
				System.out.println("This isn't a legal move. Try again.");
				continue;
			}
			
			if (currentPosition.isLegalMove(userMove)) {
				makeGameMove(userMove);
				break;
			} else {
				System.out.println("This isn't a legal move. Try again.");
			}
		}
	}

	/**
	 * Allows the user to set the piece they want to promote to.
	 * 
	 * @param userMove the <code>Move</code> to have its promotion type set
	 */
	private void letUserChoosePromotionType(Move userMove) {
		System.out.print("Enter the piece you're promoting to "
				+ "('r', 'b', 'n' or 'q'); uppercase for white, lowercase for black: ");
		userMove.setPromoteTo(scanner.next().charAt(0));
	}
	
	/**
	 * Allows the user to choose whether to play white or black against the engine.
	 * 
	 * @return <code>true</code> if the user chooses to play white; <code>false</code> otherwise
	 */
	public boolean userChoosesToPlayWhite() {
		boolean userToPlayWhite = true;
		
		while (true) {
			System.out.print("Enter \"true\" to play as white or \"false\" to play as black: ");
			try {
				userToPlayWhite = scanner.nextBoolean();
			} catch(RuntimeException e) {
				scanner.nextLine();
				System.out.println("This isn't one of the choices. Try again.");
				continue;
			}
			return userToPlayWhite;
		}
	}
	
	/**
	 * Closes the scanner object.
	 */
	public void closeScanner() {
		scanner.close();
	}
	
	/**
	 * Returns a string with the current position and the moves made.
	 * 
	 * @return String representation of the current position and the moves made
	 */
	public String toString() {
		String printGame = currentPosition.toString() + "\n";

		for (int i = 0; i < movesMade.size(); i++) {
			printGame += movesMade.get(i).toString() + " ";
			if (((i + 1) % 2) == 0)
				printGame += "\n";
		}
		if (drawRules.isDraw(movesMade, currentPosition))
			printGame += drawRules.getMessage();
		return printGame;
	}
	
}