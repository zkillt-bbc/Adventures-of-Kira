package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class Player extends B2DSprite {
	
	private int numCoins;
	private int totalCoins;
	
	public Player(Body body) {
		
		super(body);
		
		Texture tex = Game.ressources.getTexture("player");
		TextureRegion[] sprites = TextureRegion.split(tex, 32, 64)[3];
		
		setAnimation(sprites, 1 / 12f);
		
	}
	
	public void collectCoin() { numCoins++; }
	public int getNumCoin() { return numCoins; }
	public void setTotalCoin(int i) { totalCoins = i; }
	public int getTotalCoin() { return totalCoins; }
	
}
