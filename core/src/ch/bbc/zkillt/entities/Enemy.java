package ch.bbc.zkillt.entities;

import com.badlogic.gdx.physics.box2d.Body;

public class Enemy extends B2DSprite {

	public Enemy(Body body) {
		super(body);
		// TODO Auto-generated constructor stub
	}
	
//	public Texture tex;
//	public TextureRegion[] sprites;
//	public static int hp = 3;
//	private int textureRegion = 0;
//	
//	public Enemy(Body body, String ressourceName, int textureRegion) {
//		
//		super(body);
//		tex = Game.ressources.getTexture(ressourceName);
//		this.textureRegion = textureRegion;
//		sprites = TextureRegion.split(tex, 23, 35)[textureRegion];
//		setAnimation(sprites, 1 / 12f);
//		System.out.println("TurtleSprite: " + sprites.length);
//	}
//	
//	public void changeRegion(int textureRegion, float delay) {
//		sprites = TextureRegion.split(tex, 23, 35)[textureRegion];
//		setAnimation(sprites, delay);
//		this.textureRegion = textureRegion;
//	}
//	
//	public void entityMove(BodyDef bodydef, float speed) {
//		bodydef.linearVelocity.set(1.0f, 0.0f);
//	}
//	
}
