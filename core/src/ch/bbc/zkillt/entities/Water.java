package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class Water extends B2DSprite {
	public Texture tex;
	public TextureRegion[] sprites;
	private int x = 0;

	public Water(Body body) {
		super(body);
		
		tex = Game.ressources.getTexture("coin");
		sprites = TextureRegion.split(tex, 1, 1)[x];
		setAnimation(sprites, 1 / 12f);
	}
	
	public void changeRegion(int x, float delay) {
		sprites = TextureRegion.split(tex, 32, 64)[x];
		setAnimation(sprites, delay);
		this.x = x;
	}
}
