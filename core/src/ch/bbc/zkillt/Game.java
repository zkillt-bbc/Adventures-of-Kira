package ch.bbc.zkillt;

import java.awt.Dimension;
import java.awt.Toolkit;

import ch.bbc.zkillt.entities.Player;
import ch.bbc.zkillt.handlers.Content;
import ch.bbc.zkillt.handlers.GameStateManager;
import ch.bbc.zkillt.handlers.MyInput;
import ch.bbc.zkillt.handlers.MyInputProcessor;
import ch.bbc.zkillt.states.Play;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game implements ApplicationListener {
	
	
	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	static double width = screenSize.getWidth();
	static double height = screenSize.getHeight();
	public static final String TITLE = "Adventures of Kira";
	public static final int WINDOW_WIDTH = (int) width;
	public static final int WINDOW_HEIGHT = (int) height;
	public static final double SCALE = 0.5;
	public static final float STEP = 1 / 60f;
	private float accum;
	private static int score;
	private static String scoreName;
	private static BitmapFont bmf;
	private int space = 0;
	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;
	private Texture heart, coin, gameover, turtleHead;
	Play play;
	
	private GameStateManager gsm;
	
	public static Content ressources;
	
	public void create() {
		
		Gdx.input.setInputProcessor(new MyInputProcessor());
		
		ressources = new Content();
		ressources.loadTexture("ressources/images/player/player.png", "player");
		ressources.loadTexture("ressources/images/coins3.png", "coin");
		ressources.loadTexture("ressources/images/coins3.png", "wasser");
		ressources.loadTexture("ressources/images/enemy/turtle_sheet.png", "turtle");
		ressources.loadTexture("ressources/images/block.png", "powerup");

		
		this.heart = new Texture(Gdx.files.internal("ressources/images/heart.png"));
		this.coin = new Texture(Gdx.files.internal("ressources/images/single_coin.png"));
		this.gameover = new Texture(Gdx.files.internal("ressources/images/gameover2.png"));
		this.turtleHead = new Texture(Gdx.files.internal("ressources/images/enemy/turtle_head.png"));

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
	
	public void render() {
		
		accum += Gdx.graphics.getDeltaTime();
		while(accum >= STEP) {
			accum -= STEP;
			gsm.update(STEP);
			gsm.render();
			MyInput.update();
			
			
			bmf.setScale(2);
			
			//begins drawing
			sb.begin();
			sb.draw(coin, cam.position.x - 140, cam.position.y + 470, 30, 30);
			sb.draw(turtleHead, cam.position.x + 20, cam.position.y + 470, 30, 30);
			drawString(sb, "x  " + Player.getNumCoins(), cam.position.x - 100, cam.position.y + 500);
			drawString(sb, " " + Play.getPlayer().numTurtles + " / " + Play.getPlayer().totalTurtles, cam.position.x + 55, cam.position.y + 500);
			
			// draws the hearts depending on the player hp
			if(Player.hp > 0) {
				for(int hp = Player.hp; hp > 0; hp--) {
					sb.draw(heart, cam.position.x + space - 910, cam.position.y + 480, 30, 30);
					space += 40;
				}
			} else {
				sb.draw(gameover, cam.position.x - 850, cam.position.y - 150, 1754, 385);
			}
			
			space = 0;
			sb.end();
			
			// start cam position
			cam.position.set(Play.getPlayer().getPosition().x * 100 + 480, Play.getPlayer().getPosition().y * 100 + 100, 0);
			cam.update();
			}
		}
	
	// draws an a string at the screen
	private void drawString(SpriteBatch sb, String s, float x, float y) {
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == '/') c = 10;
			else if(c >= '0' && c <= '9') c -= '0';
			else continue;
			bmf.draw(sb, s, x, y);
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

	public float getAccum() {
		return accum;
	}

	public void setAccum(float accum) {
		this.accum = accum;
	}

	public static int getScore() {
		return score;
	}

	public static void setScore(int score) {
		Game.score = score;
	}

	public static String getScoreName() {
		return scoreName;
	}

	public static void setScoreName(String scoreName) {
		Game.scoreName = scoreName;
	}

	public static BitmapFont getBmf() {
		return bmf;
	}

	public static void setBmf(BitmapFont bmf) {
		Game.bmf = bmf;
	}

	public int getSpace() {
		return space;
	}

	public void setSpace(int space) {
		this.space = space;
	}

	public SpriteBatch getSb() {
		return sb;
	}

	public void setSb(SpriteBatch sb) {
		this.sb = sb;
	}

	public OrthographicCamera getCam() {
		return cam;
	}

	public void setCam(OrthographicCamera cam) {
		this.cam = cam;
	}

	public OrthographicCamera getHudCam() {
		return hudCam;
	}

	public void setHudCam(OrthographicCamera hudCam) {
		this.hudCam = hudCam;
	}

	public Texture getHeart() {
		return heart;
	}

	public void setHeart(Texture heart) {
		this.heart = heart;
	}

	public GameStateManager getGsm() {
		return gsm;
	}

	public void setGsm(GameStateManager gsm) {
		this.gsm = gsm;
	}

	public static Content getRessources() {
		return ressources;
	}

	public static void setRessources(Content ressources) {
		Game.ressources = ressources;
	}

	public static float getStep() {
		return STEP;
	}
}