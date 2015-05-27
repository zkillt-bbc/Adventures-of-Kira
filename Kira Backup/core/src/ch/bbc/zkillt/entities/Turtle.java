package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class Turtle extends B2DSprite {
	
	public Texture tex = Game.ressources.getTexture("turtle");
	public TextureRegion[] sprites;
	public static int hp = 3;
	private int x = 1;
	private float oldX;

	public Turtle(Body body) {
		
		super(body);
		sprites = TextureRegion.split(tex, 23, 35)[x];
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

	public static int getHp() {
		return hp;
	}

	public static void setHp(int hp) {
		Turtle.hp = hp;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public float getOldX() {
		return oldX;
	}

	public void setOldX(float oldX) {
		this.oldX = oldX;
	}
	
}
