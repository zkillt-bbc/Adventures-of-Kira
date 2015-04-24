package ch.bbc.zkillt.handlers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

public class MyContactListener implements ContactListener {
	
	private int numFootContacts;
	private Array<Body> bodiesToRemove;
	private boolean water;
	private int counter = 0;
	
	public MyContactListener() {
		super();
		bodiesToRemove = new Array<Body>();
	}
	
	// called when two fixtures start to collide
	public void beginContact(Contact c) {
		
		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();
		
		if(fa == null || fb == null) return;
		
		if(fa.getUserData() != null && fa.getUserData().equals("foot")) {
			numFootContacts++;
			water = false;
		}
		if(fb.getUserData() != null && fb.getUserData().equals("foot")) {
			numFootContacts++;
			water = false;
		}
		
		if(fa.getUserData() != null && fa.getUserData().equals("coin")) {
			bodiesToRemove.add(fa.getBody());
		}
		if(fb.getUserData() != null && fb.getUserData().equals("coin")) {
			bodiesToRemove.add(fb.getBody());
		}
		if (fa.getUserData() != null && fb.getUserData().equals("water"))
		{
			if(counter < 1) {
				water = true;
				counter++;
			}
			else if (counter <= 1)
			{
				water = false;
				System.out.println("counter = 1");
				this.counter = 0;
			}
//			System.out.println("Wasser: " + water);
		}
//		if (fb.getUserData() != null && fa.getUserData().equals("water"))
//		{
//			water = true;
//		}
	}
	
	// called when two fixtures no longer collide
	public void endContact(Contact c) {
		
		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();
		
		if(fa == null || fb == null) return;
		
		if(fa.getUserData() != null && fa.getUserData().equals("foot")) {
			numFootContacts--;
		}
		if(fb.getUserData() != null && fb.getUserData().equals("foot")) {
			numFootContacts--;
		}
		
	}
	
	public boolean isPlayerOnGround() { return numFootContacts > 0; }
	public boolean isInWater() { return water; }
	public Array<Body> getBodiesToRemove() { return bodiesToRemove; }
	
	
	
	public void preSolve(Contact c, Manifold m) {}
	public void postSolve(Contact c, ContactImpulse ci) {}
	
}