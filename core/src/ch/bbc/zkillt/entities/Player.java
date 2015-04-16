package ch.bbc.zkillt.entities;

import ch.bbcag.zkilt.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Player extends B2DSprite {
	
	public static final float MAX_VELOCITY = 10f;
	public static final float JUMP_VELOCITY = 40f;
	public static final float DAMPING = 0.87f;
	
	private int numCoins;
	private int totalCoins;
	private int array = 3;
	private boolean facingRight;
	private State state;
	private float stateTime;
	private Vector2 position;
	private Vector2 velocity;

	
	public enum State {
		Standing, Walking, Jumping
	}
	
	public Texture tex = Game.ressources.getTexture("player");
	TextureRegion[] sprites = TextureRegion.split(tex, 32, 64)[getArray()];
	
	public Player(Body body) {	
		super(body);
		
		position = new Vector2();
		velocity = new Vector2();
		facingRight = true;
		stateTime = 0;
		state = State.Standing;
		setAnimation(sprites, 1 / 12f);
	}
	
	public void collectCrystal() {
		numCoins++;
	}
	
	public int getNumCrystals() {
		return numCoins;
	}
	
	public void setTotalCoins(int i) {
		totalCoins = i;
	}
	
	public int getTotalCoins() {
		return totalCoins;
	}
	public int getArray() {
		return array;
	}
	public void setArray(int array) {
		this.array = array;
	}

	public boolean isFacingRight() {
		return facingRight;
	}
	
	public void setFacingRight(boolean facingRight) {
		this.facingRight = facingRight;
	}

	public int getNumCoins() {
		return numCoins;
	}

	public void setNumCoins(int numCoins) {
		this.numCoins = numCoins;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
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

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
}
