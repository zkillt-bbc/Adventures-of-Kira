package ch.bbc.zkillt.entities;

import ch.bbc.zkillt.states.Play;

public class PowerUpEntity {
	
	private Play play;
	
	public void changeSpeed(float speed) {
		Play.getMovement().x = speed;
	}
	
	public void changeHealth(int health) {
		Player.setHp(Player.getHp() + health);
	}
	
	public void changeGravit(float gravity) {
		play.setGravity(gravity);
	}
}
