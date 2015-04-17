package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.handlers.Animation;
import ch.bbc.zkillt.handlers.B2DVars;
import ch.bbc.zkillt.states.Play;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class B2DSprite {
	
	protected Body body;
	protected Animation animation;
	protected float width;
	protected float height;
	protected Play play;

	public B2DSprite(Body body) {
		this.body = body;
		animation = new Animation();
	}
	
	public void setAnimation(TextureRegion[] reg, float delay) {
		animation.setFrames(reg, delay);
		width = reg[0].getRegionWidth();
		height = reg[0].getRegionHeight();
	}
	
	public void update(float dt) {
		animation.update(dt);
	}
	
	public void render(SpriteBatch sb) {
		sb.begin();
		if(body.getUserData().toString().contains("Player") == true) {
		sb.draw(animation.getFrame(), body.getPosition().x * B2DVars.PPM - 50, body.getPosition().y *B2DVars.PPM - 105, animation.getFrame().getRegionWidth() * 3, animation.getFrame().getRegionHeight() * 3);
	} else if (body.getUserData().toString().contains("Coin") == true) {
		sb.draw(animation.getFrame(), body.getPosition().x * B2DVars.PPM - 20, body.getPosition().y *B2DVars.PPM - 15, animation.getFrame().getRegionWidth() * 2, animation.getFrame().getRegionHeight() * 2);
	} else {
		System.out.println("Fehler!");
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
