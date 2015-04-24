package ch.bbc.zkillt;

import ch.bbc.zkillt.entities.Player;
import ch.bbc.zkillt.handlers.Content;
import ch.bbc.zkillt.handlers.GameStateManager;
import ch.bbc.zkillt.handlers.MyInput;
import ch.bbc.zkillt.handlers.MyInputProcessor;
import ch.bbc.zkillt.states.Play;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game implements ApplicationListener {
	
	public static final String TITLE = "Adventures of Kira";
	public static final int WINDOW_WIDTH = 1920;
	public static final int WINDOW_HEIGHT = 1080;
	public static final int SCALE = 1;
	public static final float STEP = 1 / 60f;
	private float accum;
	public static int score;
	public static String scoreName;
	public static BitmapFont bmf;
	
	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;
	
	private GameStateManager gsm;
	
	public static Content ressources;
	
	public void create() {
		
		Gdx.input.setInputProcessor(new MyInputProcessor());
		
		ressources = new Content();
		ressources.loadTexture("ressources/images/player.png", "player");
		ressources.loadTexture("ressources/images/coins3.png", "coin");
		ressources.loadTexture("ressources/images/coins3.png", "wasser");

		
		this.sb = new SpriteBatch();
		this.cam = new OrthographicCamera();
		this.cam.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
		this.hudCam = new OrthographicCamera();
		this.hudCam.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		gsm = new GameStateManager(this);		
	
	    score = 0;
	    scoreName = "score: " + score;
	    bmf = new BitmapFont();
	}
	
	private void drawString(SpriteBatch sb, String s, float x, float y) {
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == '/') c = 10;
			else if(c >= '0' && c <= '9') c -= '0';
			else continue;
			bmf.draw(sb, "Coins: " + Player.getNumCoins(), cam.position.x -900, cam.position.y + 500);
		}
	}
	
	public void render() {
		
		accum += Gdx.graphics.getDeltaTime();
		while(accum >= STEP) {
			accum -= STEP;
			gsm.update(STEP);
			gsm.render();
			MyInput.update();
			
			
			bmf.setScale(2);
			sb.begin();
			drawString(sb, Player.getNumCoins() + " / 0", cam.position.x * 100, cam.position.y * 100);
			sb.end();
			
			cam.position.set(Play.player.getPosition().x * 100 + 480, Play.player.getPosition().y * 100  - 100, 0);
			cam.update();
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
