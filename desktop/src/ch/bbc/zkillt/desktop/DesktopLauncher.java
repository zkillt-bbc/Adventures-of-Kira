package ch.bbc.zkillt.desktop;

import ch.bbc.zkillt.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {

	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

		
		cfg.width = Game.WINDOW_WIDTH;		// start-up window-with
		cfg.height = Game.WINDOW_HEIGHT;	// start-up window-height
		cfg.fullscreen = false; 			// possible fullscreen
     
		// launches a new Application with the given config
		new LwjglApplication(new Game(), cfg);
	}
}