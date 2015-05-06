package ch.bbc.zkillt.desktop;

import ch.bbc.zkillt.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {

	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

		cfg.width = Game.WINDOW_WIDTH * Game.SCALE;
		cfg.height = Game.WINDOW_HEIGHT * Game.SCALE;
		cfg.fullscreen = false;
     
		new LwjglApplication(new Game(), cfg);
	}
}