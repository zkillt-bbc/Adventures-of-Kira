package ch.bbc.zkillt.handlers;

import ch.bbc.zkillt.entities.Player;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

public class MyContactListener implements ContactListener {

	private int numFootContacts;
	private int numEnemyFootContacts;
	private Array<Body> bodiesToRemove;
	private Array<Body> bodiesToRemove2;
	private boolean water;
	private boolean enemyCollision = false;
	private Player player;
//	private int counter = 0;

	public MyContactListener() {
		super();
		bodiesToRemove = new Array<Body>();
		bodiesToRemove2 = new Array<Body>();

	}

	// called when two fixtures start to collide
	public void beginContact(Contact c) {

		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();

		if (fa == null || fb == null)
			return;

		if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
			numFootContacts++;
			water = false;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
			numFootContacts++;
			water = false;
		}
		if (fa.getUserData() != null && fa.getUserData().equals("coin")) {
			bodiesToRemove.add(fa.getBody());
		}
		if (fb.getUserData() != null && fb.getUserData().equals("coin")) {
			bodiesToRemove.add(fb.getBody());
		}
		if (fb.getUserData() != null && fb.getUserData().equals("turtle") && fa.getUserData() != null && fa.getUserData().equals("foot")) {
			bodiesToRemove2.add(fb.getBody());
			this.enemyCollision = true;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("turtleHead") && fa.getUserData() != null && fa.getUserData().equals("foot")) {
			System.out.println("DAMAGE!!!!");
			Player.hp -= 1;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("finish") && fa.getUserData() != null && fa.getUserData().equals("foot")) {
			System.out.println("FINISH!!!!");
		}	
		if (fa.getUserData() != null && fa.getUserData().equals("water")) {
			water = true;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("powerup") && fa.getUserData() != null && fa.getUserData().equals("head")) {
			System.out.println("POWERUP!!!!");
		}
	}
	

	// called when two fixtures no longer collide
	public void endContact(Contact c) {

		Fixture fa = c.getFixtureA();
		Fixture fb = c.getFixtureB();

		if (fa == null || fb == null)
			return;

		if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
			numFootContacts--;
		}
		if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
			numFootContacts--;
		}

	}

	public boolean isPlayerOnGround() {
		return numFootContacts > 0;
	}
	
	public boolean isEnemyOnGround() {
		return numEnemyFootContacts > 0;
	}

	public boolean isInWater() {
		return water;
	}

	public Array<Body> getBodiesToRemove() {
		return bodiesToRemove;
	}
	
	public Array<Body> getBodiesToRemove2() {
		return bodiesToRemove2;
	}
	
	public void preSolve(Contact c, Manifold m) {
	}

	public void postSolve(Contact c, ContactImpulse ci) {
	}

	public int getNumFootContacts() {
		return numFootContacts;
	}

	public void setNumFootContacts(int numFootContacts) {
		this.numFootContacts = numFootContacts;
	}

	public int getNumEnemyFootContacts() {
		return numEnemyFootContacts;
	}

	public void setNumEnemyFootContacts(int numEnemyFootContacts) {
		this.numEnemyFootContacts = numEnemyFootContacts;
	}

	public boolean isWater() {
		return water;
	}

	public void setWater(boolean water) {
		this.water = water;
	}

	public boolean isEnemyCollision() {
		return enemyCollision;
	}

	public void setEnemyCollision(boolean enemyCollision) {
		this.enemyCollision = enemyCollision;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setBodiesToRemove(Array<Body> bodiesToRemove) {
		this.bodiesToRemove = bodiesToRemove;
	}

	public void setBodiesToRemove2(Array<Body> bodiesToRemove2) {
		this.bodiesToRemove2 = bodiesToRemove2;
	}

}