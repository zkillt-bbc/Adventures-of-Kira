

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class KiraDesktop {

	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = Game.TITLE;
		cfg.width = Game.WINDOW_WIDTH;
		cfg.height = Game.WINDOW_HEIGHT;
		
		new LwjglApplication(new Game(), cfg);
	}

}
