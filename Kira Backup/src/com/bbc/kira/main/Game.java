package com.bbc.kira.main;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game implements ApplicationListener{

	public static final String TITLE = "Adventures of Kira";
	public static final int WINDOW_WIDTH = 1920;
	public static final int WINDOW_HEIGHT = 1080;

	
	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;
	
	public SpriteBatch getSpriteBatch() {
		return sb;
	}
	
	public OrthographicCamera getCamera() {
		return cam;
	}
	
	public OrthographicCamera getHUDCamera() {
		return hudCam;
	}
	
	@Override
	public void create() {
	
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void render() {
		
	}

	@Override
	public void resize(int arg0, int arg1) {
	}
	
	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

}
