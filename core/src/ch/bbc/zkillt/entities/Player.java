package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class Player extends B2DSprite {
	
	public static int numCoins;
	public static int totalCoins;
	public int numTurtles;
	public int totalTurtles;
	public Texture tex;
	private TextureRegion[] sprites;
	private double oldX;
	private int x = 3;
	public static int hp = 3;

	public Player(Body body) {
		
		super(body);
		tex = Game.ressources.getTexture("player");
		sprites = TextureRegion.split(tex, 32, 64)[x];
		setAnimation(sprites, 1 / 12f);
		System.out.println("Sprites: " + sprites.length);
	}
	
	public void changeRegion(int x, int width, int height, float delay) {
		sprites = TextureRegion.split(tex, width, height)[x];
		setAnimation(sprites, delay);
		this.x = x;
	}
	
	public void collectCoin() { setNumCoins(getNumCoins() + 1); System.out.println(getNumCoins()); }
	public int getNumCoin() { return getNumCoins(); }
	public void setTotalCoin(int i) { totalCoins = i; }
	public int getTotalCoin() { return totalCoins; }

	/**
	 * @return the numCoins
	 */
	public static int getNumCoins() {
		return numCoins;
	}

	/**
	 * @param numCoins the numCoins to set
	 */
	public void setNumCoins(int numCoins) {
		Player.numCoins = numCoins;
	}

	public static int getTotalCoins() {
		return totalCoins;
	}

	public static void setTotalCoins(int totalCoins) {
		Player.totalCoins = totalCoins;
	}

	public int getNumTurtles() {
		return numTurtles;
	}

	public void setNumTurtles(int numTurtles) {
		this.numTurtles = numTurtles;
	}

	public int getTotalTurtles() {
		return totalTurtles;
	}

	public void setTotalTurtles(int totalTurtles) {
		this.totalTurtles = totalTurtles;
	}

	public Texture getTex() {
		return tex;
	}

	public void setTex(Texture tex) {
		this.tex = tex;
	}

	public TextureRegion[] getSprites() {
		return sprites;
	}

	public void setSprites(TextureRegion[] sprites) {
		this.sprites = sprites;
	}

	public double getOldX() {
		return oldX;
	}

	public void setOldX(double oldX) {
		this.oldX = oldX;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public static int getHp() {
		return hp;
	}

	public static void setHp(int hp) {
		Player.hp = hp;
	}
	
}
