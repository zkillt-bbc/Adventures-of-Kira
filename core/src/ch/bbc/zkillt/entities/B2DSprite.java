package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.handlers.Animation;
import ch.bbc.zkillt.handlers.B2DVars;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class B2DSprite extends TextureRegion {
	
	protected Body body;
	protected Animation animation;
	protected float width;
	protected float height;


	
	public B2DSprite(Body body) {
		this.body = body;
		animation = new Animation();
	}
	
	public void setAnimation(TextureRegion[] sprites, float delay) {
		animation.setFrames(sprites, delay);
		width = sprites[0].getRegionWidth();
		height = sprites[0].getRegionHeight();
	}
	
	public void update(float dt) {
	animation.update(dt);	
	}
	
	public void render(SpriteBatch sb) {
		sb.begin();
	if(body.getUserData().toString().contains("Player") == true) {
		sb.draw(animation.getFrame(), body.getPosition().x * B2DVars.PPM - 50, body.getPosition().y *B2DVars.PPM - 65, animation.getFrame().getRegionWidth() * 3, animation.getFrame().getRegionHeight() * 3);
	}
	else if (body.getUserData().toString().contains("Coin") == true) {
		sb.draw(animation.getFrame(), body.getPosition().x * B2DVars.PPM - 20, body.getPosition().y *B2DVars.PPM - 15, animation.getFrame().getRegionWidth() * 2, animation.getFrame().getRegionHeight() * 2);
	} 
	else if (body.getUserData().toString().contains("Water") == true) {
		sb.draw(animation.getFrame(), body.getPosition().x * B2DVars.PPM, body.getPosition().y *B2DVars.PPM , animation.getFrame().getRegionWidth() * 2, animation.getFrame().getRegionHeight() * 2);
	}
	else if (body.getUserData().toString().contains("Turtle") == true) {
		sb.draw(animation.getFrame(), body.getPosition().x * B2DVars.PPM - 20, body.getPosition().y *B2DVars.PPM - 38, animation.getFrame().getRegionWidth() * 3, animation.getFrame().getRegionHeight() * 3);
	}
	else if (body.getUserData().toString().contains("Powerup") == true) {
		sb.draw(animation.getFrame(), body.getPosition().x * B2DVars.PPM - 20, body.getPosition().y *B2DVars.PPM - 38, 69, 69);
	} else {
		System.out.println("Fehler: Zu renderndes Objekt wurde nicht gefunden!");
	}
	sb.end();

}

	
	public Body getBody() {
		return body;
	}
	
	public Vector2 getPosition() {
		return body.getPosition();
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
}
