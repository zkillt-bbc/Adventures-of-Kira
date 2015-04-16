package ch.bbc.zkillt;

import ch.bbc.zkillt.handlers.Content;
import ch.bbc.zkillt.handlers.GameStateManager;
import ch.bbc.zkillt.handlers.MyInput;
import ch.bbc.zkillt.handlers.MyInputProcessor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game implements ApplicationListener {
	
	public static final String TITLE = "Adventures of Kira";
	public static final int WINDOW_WIDTH = 1920;
	public static final int WINDOW_HEIGHT = 1080;
	public static final int SCALE = 1;
	
	public static final float STEP = 1 / 60f;
	private float accum;
	
	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;
	
	private GameStateManager gsm;
	
	public static Content ressources;
	
	public void create() {
		
		Gdx.input.setInputProcessor(new MyInputProcessor());
		
		ressources = new Content();
		ressources.loadTexture("ressources/images/player.png", "player");
		ressources.loadTexture("ressources/images/coins2.png", "coin");
		
		sb = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		gsm = new GameStateManager(this);
		
	}
	
	public void render() {
		
		accum += Gdx.graphics.getDeltaTime();
		while(accum >= STEP) {
			accum -= STEP;
			gsm.update(STEP);
			gsm.render();
			MyInput.update();
		}
		
	}
	
	public void dispose() {
		
	}
	
	public SpriteBatch getSpriteBatch() { return sb; }
	public OrthographicCamera getCamera() { return cam; }
	public OrthographicCamera getHUDCamera() { return hudCam; }
	
	public void resize(int w, int h) {}
	public void pause() {}
	public void resume() {}
	
}
