package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class PowerupBlock extends B2DSprite {

	public Texture tex = Game.ressources.getTexture("powerup");
	public TextureRegion[] sprites;
	public static int hp = 3;
	private int x = 0;
	
	public PowerupBlock(Body body) {
		
		super(body);
		sprites = TextureRegion.split(tex, tex.getWidth(), tex.getHeight())[x];
		setAnimation(sprites, 1 / 12f);
	}
	
	public void changeRegion(int x, float delay) {
		this.x = x;
		sprites = TextureRegion.split(tex, 23, 35)[x];
		setAnimation(sprites, delay);
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
	
}
