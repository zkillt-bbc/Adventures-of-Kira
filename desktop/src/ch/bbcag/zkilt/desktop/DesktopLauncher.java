package ch.bbcag.zkilt.desktop;

import ch.bbc.zkillt.Game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	
	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		
		cfg.title = Game.TITLE;
		cfg.width = Game.WINDOW_WIDTH * Game.SCALE;
		cfg.height = Game.WINDOW_HEIGHT * Game.SCALE;
		cfg.fullscreen = true;

		new LwjglApplication(new Game(), cfg);
	}
}