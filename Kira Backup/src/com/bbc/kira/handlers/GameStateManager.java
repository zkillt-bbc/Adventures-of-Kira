package com.bbc.kira.handlers;

import java.util.Stack;

import com.bbc.kira.main.Game;
import com.bbc.kira.states.GameState;

public class GameStateManager {

	private Game game;
	private Stack<GameState> gameStates;
	public static final int PLAY = 912837;
	
	public GameStateManager(Game game) {
		this.game = game;
		gameStates = new Stack<GameState>();
		pushState(PLAY);
	}
	
	public Game game() {
		return game;
	}
	
	public void update(float dt) {
		gameStates.peek().update(dt);
	}
	
	public void render() {
		gameStates.peek().render();
	}
	
	
	private GameState getState(int state) {
		if (state == PLAY) return new Play(this);
			return null;
	}
	
	private GameState setState(int state) {
		if (state == PLAY) return new Play(this);
			return null;
	}

	public void pushState (int state) {
		gameStates.push(getState(state));
	}
}