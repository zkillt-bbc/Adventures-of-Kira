package ch.bbc.zkillt.handlers;

import ch.bbc.zkillt.entities.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * The Class MusicHandler.
 */

public class MusicPlayer {
	
	/** The background music. */
	public static Sound bgMusic = Gdx.audio.newSound(Gdx.files.internal("ressources/sound/ingame.mp3"));
	public static Sound playerJump = Gdx.audio.newSound(Gdx.files.internal("ressources/sound/jump.mp3"));
	public static Sound coin = Gdx.audio.newSound(Gdx.files.internal("ressources/sound/coin.wav"));
	public static Sound jumpOnEnemy = Gdx.audio.newSound(Gdx.files.internal("ressources/sound/jumpOnEnemy.mp3"));

	/**
	 * Instantiates a new music player.
	 * 
	 * @author Tim Killenberger
	 * @param sound, volume, speed, length
	 */
	public MusicPlayer(final Sound sound, float volume, float speed, float length) {

		final long id = sound.loop();		
		sound.setVolume(id, volume);
		sound.setPitch(id, speed);
		Timer.schedule(new Task(){
		   public void run(){
			   sound.stop(id);
		   }
		}, length);
	}
	
	public void checkSound(Sound sound) {
	if (Player.hp == 0) {
		sound.stop();
	}
	}
}
