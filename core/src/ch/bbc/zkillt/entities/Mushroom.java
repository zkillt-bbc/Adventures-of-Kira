package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.Game;
import ch.bbc.zkillt.handlers.B2DVars;
import ch.bbc.zkillt.states.Play;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;

public class Mushroom extends B2DSprite {

	public static Array<Mushroom> mushArray;
	public Texture tex = Game.ressources.getTexture("turtle");
	public TextureRegion[] sprites;
	Mushroom mushroom;

	public Mushroom(Body body) {
		
		super(body);
		sprites = TextureRegion.split(tex, 23, 35)[0];
		setAnimation(sprites, 1 / 12f);
	}
	
	public void createMush() {
		
		System.out.println("Ich bin en Pilz");
		
		mushArray = new Array<Mushroom>();

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();

		bdef.type = BodyType.DynamicBody;

		float x = Play.getPlayer().getPosition().x + 20;
		float y = Play.getPlayer().getPosition().y + 50;

		// create enemy
		bdef.position.set(x, y);
		Body body = Play.getWorld().createBody(bdef);
		body.setLinearVelocity(2.0f, 0);

		CircleShape cshape = new CircleShape();
		cshape.setRadius(18 / B2DVars.PPM);
		fdef.shape = cshape;
		fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
		fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_GROUND | B2DVars.BIT_SCHRAEG;
		fdef.isSensor = true;


		body.createFixture(fdef).setUserData("mushroom");

		mushroom = new Mushroom(body);
		mushArray.add(mushroom);
		body.setUserData(mushroom);
	}
}