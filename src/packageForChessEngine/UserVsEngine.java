package packageForChessEngine;

// Written by Darcy McCoy
// Starting November 27, 2023

// A game of type: user playing engine

public class UserVsEngine extends Game {
	public UserVsEngine() {
		// Default constructor
		super();
	}
	
	public void play() {
		// Starts the userVsEngine game
		startGame();
		if (userChoosesToPlayWhite())
			letUserMakeMove();
		
		while(inGame) {
			letEngineMakeMove();
			System.out.println(toString());
			letUserMakeMove();
		}
	}
	
}
