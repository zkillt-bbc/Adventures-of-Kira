package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class Player extends B2DSprite {
	
	public static int numCoins;
	public static int totalCoins;
	public static int numTurtles;
	public static int totalTurtles;
	public Texture tex;
	public TextureRegion[] sprites;
	private int x = 3;
	public static int hp = 3;

	public Player(Body body) {
		
		super(body);
		tex = Game.ressources.getTexture("player");
		sprites = TextureRegion.split(tex, 32, 64)[x];
		setAnimation(sprites, 1 / 12f);
		System.out.println("Sprites: " + sprites.length);
	}
	
	public void changeRegion(int x, float delay) {
		sprites = TextureRegion.split(tex, 32, 64)[x];
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
	
}
