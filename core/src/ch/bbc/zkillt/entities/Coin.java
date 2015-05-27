package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class Coin extends B2DSprite {
	

	public Texture tex = Game.ressources.getTexture("coin");
	public TextureRegion[] sprites;
	public static int hp = 3;
	
	public Coin(Body body) {
		super(body);
		sprites = TextureRegion.split(tex, 22, 20)[0];				
		setAnimation(sprites, 1 / 12f);				
	}	
}