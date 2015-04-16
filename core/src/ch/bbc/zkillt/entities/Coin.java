package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class Coin extends B2DSprite {
	
	public Coin(Body body) {
		
		super(body);
		
		Texture tex = Game.ressources.getTexture("coin");
		TextureRegion[] sprites = TextureRegion.split(tex, 44, 40)[0];
		
		setAnimation(sprites, 1 / 6f);
		
	}
	
}
