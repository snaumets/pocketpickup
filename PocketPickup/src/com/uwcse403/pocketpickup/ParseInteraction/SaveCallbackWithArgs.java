package com.uwcse403.pocketpickup.ParseInteraction;

import com.parse.SaveCallback;
import com.uwcse403.pocketpickup.game.Game;

public abstract class SaveCallbackWithArgs extends SaveCallback {
	
	private Game g;
	
	public SaveCallbackWithArgs(Game g) {
		this.g = g;
	}
	
	public Game getGame() {
		return g;
	}
}
