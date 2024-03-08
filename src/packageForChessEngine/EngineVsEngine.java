package packageForChessEngine;

// Written by Darcy McCoy
// Starting November 27, 2023

// A game of type: engine playing against engine

public class EngineVsEngine extends Game {
	public EngineVsEngine() {
		// Default constructor
		super();
	}
	
	public void play() {
		// Starts the engineVsEngine game
		startGame();
		while(inGame) {
			letEngineMakeMove();
			System.out.println(this);
		}
	}
}
