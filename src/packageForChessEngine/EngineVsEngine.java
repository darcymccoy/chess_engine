package packageForChessEngine;

//Written by Darcy McCoy
//Starting November 27, 2023

public class EngineVsEngine extends Game {
	public EngineVsEngine() {
		// Default constructor
		super();
	}
	
	public void startGame() {
		// Starts the engineVsEngine game
		super.startGame();
		while(inGame) {
			letEngineMakeMove();
			System.out.println(toString());
		}
	}
}
