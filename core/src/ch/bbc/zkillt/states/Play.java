package ch.bbc.zkillt.states;

import box2dLight.ConeLight;
import box2dLight.RayHandler;
import ch.bbc.zkillt.Game;
import ch.bbc.zkillt.entities.Coin;
import ch.bbc.zkillt.entities.Mushroom;
import ch.bbc.zkillt.entities.Player;
import ch.bbc.zkillt.entities.PowerupBlock;
import ch.bbc.zkillt.entities.Turtle;
import ch.bbc.zkillt.entities.Water;
import ch.bbc.zkillt.handlers.B2DVars;
import ch.bbc.zkillt.handlers.GameStateManager;
import ch.bbc.zkillt.handlers.MusicPlayer;
import ch.bbc.zkillt.handlers.MyContactListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Play extends GameState {

	// ---------- Debugging ---------------------//

	private Box2DDebugRenderer b2dr;

	// ---------- Player States -----------------//
	
	private boolean dead = false;
	
	// ---------- Listener properties -----------//
	

	public static MyContactListener cl;

	// ---------- Class Instances ---------------//
	
	public static MusicPlayer mp; // Musicplayer instance
	public static Player player;	// Player instance
	public static Turtle turtle;	// Turtle instance
	private Array<Coin> coinsArray;	// Coin object array
	private Array<Turtle> turtleArray;	// Turtle object array
	private Array<Water> waterArray; // Water object array
	private Array<PowerupBlock> powerupArray;


	// ---------- World properties --------------//

	private OrthogonalTiledMapRenderer tmr; // render
	private static World world;
	public static Vector2 movement = new Vector2(0, 0); // the player movement (x and y)
	private float speed = 6, gravity, nitro = 0.6f; // world / movement variables
	private final float TIMESTEP = 1 / 60; // refresh rate
	private final int VELOCITYITERATIONS = 8, POSITIONITERAIONS = 3;
	private boolean right = false; // enemy moving-direction boolean
	private float desiredVelocity; // movement of the player

	// ---------- Camera Positioning ------------//

	private OrthographicCamera b2dCam; // the camera of the b2d hitboxes
	float x = cam.position.x - cam.viewportWidth * cam.zoom; // the x position if the camera 
	float y = cam.position.y - cam.viewportHeight * cam.zoom; // the y position if the camera
	float width = cam.viewportWidth * Game.WINDOW_WIDTH; // the width of the camera
	float height = cam.viewportHeight * Game.WINDOW_HEIGHT; // the height of the camera

	// ---------- Tilemap properties ------------//

	private String mapPath; // path to choose the map
	private TiledMap tileMap; // Tilemap instance for the properties and rendering
	private MapProperties prop; // for the properties of the tilemap
	private float tileSize; // the size of a tile in px 
	private int mapWidth; // width in tiled units
	private int mapHeight; // height in tiled units
	private int tilePixelWidth; // tile width in px
	private int tilePixelHeight; // tile height in px
	private int mapPixelWidth; // map width in px
	private int mapPixelHeight; // map height in px

	private RayHandler rayHandler; // lightning handler

	
	
	
	/**
	 * The Constructor.
	 *
	 * @author Tim Killenberger
	 * 
	 * Instantiates a new play.
	 * @param gsm the GameStateManager
	 * @param mapPath the map path
	 * @param gravity the gravity
	 */
	public Play(GameStateManager gsm, String mapPath, float gravity) {

		super(gsm);
		
		new MusicPlayer(MusicPlayer.bgMusic, 0.3f, 1.3f, 1000); // background music
		this.mapPath = mapPath;
		this.gravity = gravity;
		// set up box2d stuff
		world = new World(new Vector2(0, -9.81f), true);
		cl = new MyContactListener();
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();
		rayHandler = new RayHandler(world, 50, 50);
		rayHandler.setCombinedMatrix(cam.combined);

		 new ConeLight(rayHandler, 500,
		 com.badlogic.gdx.graphics.Color.WHITE, 1920, cam.position.x - 200,
		 cam.position.y + 600, -90, 45);

		// create player
		createPlayer();

		// create tiles
		createTiles();

		// create Coins
		createCoin();

		// create Water
		createWater();

		// create Turtle
		createTurtle();
		
		// create Powerup
		createPowerupBlock();

		// set up box2d cam
		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, Game.WINDOW_WIDTH / B2DVars.PPM, Game.WINDOW_HEIGHT / B2DVars.PPM);
	}

	/**
	 * Updates the Game
	 * 
	 * @author Tim Killenberger
	 * @param dt the Delta Time
	 */
	public void update(float dt) {
		
		// --------- Player states -----------//		
		
		 if(Player.hp > 0 && player.getPosition().y > 0) {
			 player.setOldX(player.getPosition().x);
		 }
		
		 
		 // checks if player is on ground
		 if (!cl.isPlayerOnGround()) {
			 gravity = gravity * 1.03f;
		 } else {
			 gravity = 12;
		 }
		 
		 
		// checks if the player is in water
		if (cl.isInWater()) {
			gravity = 3;
		}
		
		//checks if player collides with an enemy
		if (cl.isEnemyCollision()) {
			movement.y = speed / 1.25f;
			cl.setEnemyCollision(false);
		}

		
		// if player is alive the cam stops depending on where the map ends
		if(Player.hp > 0 && player.getPosition().y >  2){
			if (player.getPosition().x > 8 && player.getPosition().x < (getMapPixelWidth() - 1400) / B2DVars.PPM) { // focuses the cam on the player
				cam.position.set(Play.getPlayer().getPosition().x * 100 + Game.WINDOW_WIDTH / 10, Play.getPlayer().getPosition().y * 100 + Game.WINDOW_HEIGHT / 10, 0);
			} else if (player.getPosition().x < 8) { // Cam stops at the left border of the map
				cam.position.set(Game.WINDOW_HEIGHT / 1.1f, Play.getPlayer().getPosition().y * 100 + Game.WINDOW_HEIGHT / 10, 0);
			} else if (player.getPosition().x > ((getMapPixelWidth()) - Game.WINDOW_WIDTH / 1.3f) / B2DVars.PPM) { // Cam stops at the right border of the map
				cam.position.set((getMapPixelWidth()) - Game.WINDOW_WIDTH / 1.6f, player.getPosition().y * 100 + Game.WINDOW_HEIGHT / 10, 0);
			} else { // if cam is at an undefined position
				System.out.println("NOTHING AT ALL!");
			}
		} else { // if player is dead camera stops at his death position
			cam.position.set((float) (player.getOldX() * 100 + Game.WINDOW_WIDTH / 10), 5 + Game.WINDOW_HEIGHT / 1.5f, 0);
			Player.hp = 0;
			gravity = 0;
			dead = true;
			movement.x = 0;
			movement.y = speed / 1.5f;
		}
		
		cam.update();
		tmr.setView(cam.combined, x * 2, y, width, height);
		
		desiredVelocity = Math.min(movement.x, player.getBody().getLinearVelocity().x + nitro);

		// apply gravity
		movement.y -= gravity * dt;

		if (movement.y > speed)
			movement.y = speed;
		else if (movement.y < -speed)
			movement.y = -speed;


		if(!dead) {
		// check input
		handleInput();
		}

		// set player speed
		player.getBody().setLinearVelocity(desiredVelocity, movement.y);

		// update box2d
		world.step(dt, 6, 2);
		player.update(dt);

		// remove Coins
		Array<Body> bodies = cl.getBodiesToRemove();
		for (int i = 0; i < bodies.size; i++) {
			Body b = bodies.get(i);
			coinsArray.removeValue((Coin) b.getUserData(), true);
			world.destroyBody(b);
			player.collectCoin();
			new MusicPlayer(MusicPlayer.coin, 0.4f, 1, 0.3f);
		}

		bodies.clear();

		// remove Turtle
		Array<Body> bodies2 = cl.getBodiesToRemove2();
		for (int i = 0; i < bodies2.size; i++) {
			Body b = bodies2.get(i);
			turtleArray.removeValue((Turtle) b.getUserData(), false);
			world.destroyBody(b);
			player.numTurtles++;
			new MusicPlayer(MusicPlayer.jumpOnEnemy, 0.3f, 1, 0.3f);
		}

		bodies2.clear();

		for (Turtle t : turtleArray) {
			if (t.getBody().getPosition().x == t.getOldX()) {
				if (right) {
					t.getBody().setLinearVelocity(2.0f, 0);
					t.changeRegion(1, 1 / 12);
					right = false;
				} else if (!right) {
					t.getBody().setLinearVelocity(-2.0f, 0);
					t.changeRegion(0, 1 / 12);
					right = true;
				}
			}
			t.setOldX(t.getBody().getPosition().x);
		}

		for (int i = 0; i < coinsArray.size; i++) {
			coinsArray.get(i).update(dt);
		}

		for (int i = 0; i < turtleArray.size; i++) {
			turtleArray.get(i).update(dt);
		}
		
		for (int i = 0; i < powerupArray.size; i++) {
			powerupArray.get(i).update(dt);
		}
	}

	/**
	 * renders the game-graphics and objects
	 * 
	 * @author Tim Killenberger
	 */
	public void render() {
				
		// clear screen
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		b2dCam.position.set(cam.position.x, cam.position.y, 0);

		sb.begin();
		tmr.getBatch().begin();
		tmr.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get(
				"background"));
		tmr.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get(
				"bg_deko"));
		tmr.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get(
				"ground"));
		tmr.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get(
				"schraeg"));
		tmr.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get(
				"decoration"));
		tmr.getBatch().end();
		sb.end();

		// draw Coins
		for (int i = 0; i < coinsArray.size; i++) {
			coinsArray.get(i).render(sb);
		}
		
		// draw PowerUpBlock
		for (int i = 0; i < powerupArray.size; i++) {
			powerupArray.get(i).render(sb);
		}

		// draw Turtle
		for (int i = 0; i < turtleArray.size; i++) {
			turtleArray.get(i).render(sb);
		}
		
		
		// draw player
		if(player != null) {
		player.render(sb);
		} else {
			System.out.println("Player destroyed");
		}
		
		sb.setProjectionMatrix(cam.combined);

		sb.begin();
		tmr.getBatch().begin();
		tmr.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get(
				"Water2"));
		tmr.getBatch().end();
		sb.end();

		 //draw box2d world
//		 b2dr.render(world, b2dCam.combined);
		
		if(Player.hp == 0) {
		 rayHandler.updateAndRender();
		}
		
		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERAIONS);

	}

	/**
	 * Handles keyboard - input
	 *  
	 * @author Tim Killenberger
	 */
	public void handleInput() {
		Gdx.input.setInputProcessor(new InputProcessor() {
	
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer,
					int button) {

				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {

				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer,
					int button) {

				return false;
			}

			@Override
			public boolean scrolled(int amount) {

				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {

				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				switch (keycode) {
				case Keys.A:
					player.changeRegion(2, 32, 64,10000);
					// Sprite für Stehenbleiben ändern
					movement.x = 0;
					break;
				case Keys.D:
					player.changeRegion(3, 32, 64 ,10000); // Sprite für Stehenbleiben
													// ändern (1/12f) = delay
													// für Animationen
					movement.x = 0;
					break;
				case Keys.SHIFT_LEFT:
					if (Gdx.input.isKeyPressed(Keys.A)) {
						movement.x = -speed;
					} else if (Gdx.input.isKeyPressed(Keys.D)) {
						movement.x = speed;
					}
					break;
				}
				return true;
			}

			@Override
			public boolean keyTyped(char character) {

				return false;
			}

			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {
				case Keys.SPACE:
					if (cl.isPlayerOnGround()) {
						new MusicPlayer(MusicPlayer.playerJump, 0.2f, 1, 0.3f);
						movement.y = speed * 1.2f;
						if (cl.isInWater()) {
							movement.y = speed / 2;
						}
					} else if (cl.isInWater()) {
						movement.y = speed / 2;
					}
					cl.setWater(false);
					break;

				case Keys.A:
					if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
						movement.x = -speed;
					}
					player.changeRegion(2, 32, 64,1 / 12f);
					movement.x = -speed;
					break;

				case Keys.D:
					if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
						movement.x = speed;
					}
					player.changeRegion(3, 32, 64,1 / 12f);
					movement.x = speed;
					break;

				case Keys.SHIFT_LEFT:
					if (Gdx.input.isKeyPressed(Keys.A)) {
						player.changeRegion(2, 32, 64,1 / 18f);
						movement.x = -speed * 1.6f;
					} else if (Gdx.input.isKeyPressed(Keys.D)) {
						player.changeRegion(3, 32, 64,1 / 18f);
						movement.x = speed * 1.6f;
					}
					break;
				}
				return true;
			}
		});
	}


	public void dispose() {
	}

	/**
	 * Creates the player.
	 *
	 * @author Tim Killenberger
	 */
	private void createPlayer() {

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();

		// create player
		bdef.position.set(500 / B2DVars.PPM, 1000 / B2DVars.PPM);
		bdef.type = BodyType.DynamicBody;
		Body body = world.createBody(bdef);

		shape.setAsBox(27 / B2DVars.PPM, 50 / B2DVars.PPM);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_COIN
				| B2DVars.BIT_SCHRAEG | B2DVars.BIT_WATER | B2DVars.BIT_ENEMY | B2DVars.BIT_POWERUP;
		body.createFixture(fdef).setUserData("player");

		// create foot sensor
		shape.setAsBox(30 / B2DVars.PPM, 2 / B2DVars.PPM, new Vector2(0, -50
				/ B2DVars.PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_COIN
				| B2DVars.BIT_SCHRAEG | B2DVars.BIT_WATER | B2DVars.BIT_ENEMY;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("foot");

		// create head sensor
		shape.setAsBox(18 / B2DVars.PPM, 50 / B2DVars.PPM, new Vector2(0,
				68 / B2DVars.PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_COIN
				| B2DVars.BIT_SCHRAEG | B2DVars.BIT_WATER | B2DVars.BIT_ENEMY | B2DVars.BIT_POWERUP;
		fdef.isSensor = false;
		body.createFixture(fdef).setUserData("head");

		// create player
		player = new Player(body);
		body.setUserData(player);
	}

	/**
	 * Creates the Tiles.
	 *
	 * @author Tim Killenberger
	 */
	private void createTiles() {

		// load tile map
		tileMap = new TmxMapLoader().load(mapPath);
		tmr = new OrthogonalTiledMapRenderer(tileMap);
		tileSize = tileMap.getProperties().get("tilewidth", Integer.class);
		TiledMapTileLayer layer;

		layer = (TiledMapTileLayer) tileMap.getLayers().get("ground");
		createLayer(layer, false, B2DVars.BIT_GROUND);

		layer = (TiledMapTileLayer) tileMap.getLayers().get("schraeg");
		createLayerSchraeg(layer, false, B2DVars.BIT_SCHRAEG);

		prop = tileMap.getProperties();
		mapWidth = prop.get("width", Integer.class);
		mapHeight = prop.get("height", Integer.class);
		tilePixelWidth = prop.get("tilewidth", Integer.class);
		tilePixelHeight = prop.get("tileheight", Integer.class);
		mapPixelWidth = mapWidth * tilePixelWidth;
		mapPixelHeight = mapHeight * tilePixelHeight;
	}

	/**
	 * Creates the layer.
	 *
	 * @param layer the layer
	 * @param filter the filter
	 * @param bits the bits
	 */
	private void createLayer(TiledMapTileLayer layer, boolean filter, short bits) {

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();

		// go through all the cells in the layer
		for (int row = 0; row < layer.getHeight(); row++) {
			for (int col = 0; col < layer.getWidth(); col++) {

				// get cell
				Cell cell = layer.getCell(col, row);

				// check if cell exists
				if (cell == null)
					continue;
				if (cell.getTile() == null)
					continue;

				// create a body + fixture from cell
				bdef.type = BodyType.StaticBody;
				bdef.position.set((col + 0.5f) * tileSize / B2DVars.PPM,
						(row + 0.5f) * tileSize / B2DVars.PPM);

				ChainShape cs = new ChainShape();
				Vector2[] v = new Vector2[5];
				// von unten links nach oben links
				v[0] = new Vector2(-tileSize / 2 / B2DVars.PPM, -tileSize / 2
						/ B2DVars.PPM);
				// Von oben links nach oben rechts
				v[1] = new Vector2(-tileSize / 2 / B2DVars.PPM, tileSize / 2
						/ B2DVars.PPM);
				// Ecke oben Links
				v[2] = new Vector2(tileSize / 2 / B2DVars.PPM, tileSize / 2
						/ B2DVars.PPM);
				// Ecke oben Rechts
				v[3] = new Vector2(tileSize / 2 / B2DVars.PPM, -tileSize / 2
						/ B2DVars.PPM);
				// von unten links nach oben links
				v[4] = new Vector2(-tileSize / 2 / B2DVars.PPM, -tileSize / 2
						/ B2DVars.PPM);

				cs.createChain(v);
				fdef.friction = 0;
				fdef.shape = cs;
				fdef.filter.categoryBits = bits;
				fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_ENEMY;
				;
				fdef.isSensor = filter;
				world.createBody(bdef).createFixture(fdef);
			}
		}
	}

	/**
	 * Creates the layer schraeg.
	 *
	 * @param layer the Layername
	 * @param filter the filter boolean
	 * @param bits the CollisionBits
	 */
	private void createLayerSchraeg(TiledMapTileLayer layer, boolean filter,
			short bits) {

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();

		// go through all the cells in the layer
		for (int row = 0; row < layer.getHeight(); row++) {
			for (int col = 0; col < layer.getWidth(); col++) {

				// get cell
				Cell cell = layer.getCell(col, row);

				// check if cell exists
				if (cell == null)
					continue;
				if (cell.getTile() == null)
					continue;

				// create a body + fixture from cell

				bdef.type = BodyType.StaticBody;
				bdef.position.set((col + 0.5f) * tileSize / B2DVars.PPM,
						(row + 0.5f) * tileSize / B2DVars.PPM);

				ChainShape cs = new ChainShape();
				Vector2[] v = new Vector2[3];
				// Ecke unten Links
				v[0] = new Vector2(-tileSize / 2 / B2DVars.PPM, -tileSize / 2
						/ B2DVars.PPM);
				// Ecke unten Rechts
				v[1] = new Vector2(-tileSize / 2 / B2DVars.PPM, tileSize / 2
						/ B2DVars.PPM);
				// Ecke oben Links
				v[2] = new Vector2(tileSize / 2 / B2DVars.PPM, tileSize / 2
						/ B2DVars.PPM / 2 - 0.52f);
				// // Ecke oben Rechts
				// v[3] = new Vector2(
				// (tileSize / 2 / B2DVars.PPM) / 2, (-tileSize / 2 /
				// B2DVars.PPM) / 2);
				cs.createChain(v);
				fdef.friction = 0;
				fdef.shape = cs;
				fdef.filter.categoryBits = bits;
				fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_ENEMY;
				fdef.isSensor = filter;
				world.createBody(bdef).createFixture(fdef);
			}
		}
	}

	/**
	 * Creates the turtle.
	 *
	 * @author Tim Killenberger
	 */
	private void createTurtle() {

		turtleArray = new Array<Turtle>();
		MapLayer layer = tileMap.getLayers().get("turtles");

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();

		for (MapObject mo : layer.getObjects()) {

			bdef.type = BodyType.DynamicBody;

			float x = (float) mo.getProperties().get("x", Float.class)
					/ B2DVars.PPM;
			float y = (float) mo.getProperties().get("y", Float.class)
					/ B2DVars.PPM;

			// create enemy
			bdef.position.set(x, y);
			Body body = world.createBody(bdef);
			body.setLinearVelocity(2.0f, 0);

			shape.setAsBox(22 / B2DVars.PPM, 5 / B2DVars.PPM, new Vector2(0,
					60 / B2DVars.PPM), 0);
			fdef.shape = shape;
			fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_GROUND
					| B2DVars.BIT_SCHRAEG;
			fdef.isSensor = false;

			body.createFixture(fdef).setUserData("turtle");

			// create Head sensor
			shape.setAsBox(22 / B2DVars.PPM, 40 / B2DVars.PPM, new Vector2(0,
					15 / B2DVars.PPM), 0);
			fdef.shape = shape;
			fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_GROUND
					| B2DVars.BIT_SCHRAEG;
			fdef.isSensor = false;
			body.createFixture(fdef).setUserData("turtleHead");

			// create player
			turtle = new Turtle(body);
			turtleArray.add(turtle);
			body.setUserData(turtle);
		}
		player.totalTurtles = turtleArray.size;

	}

	/**
	 * Creates the coin.
	 *
	 * @author Tim Killenberger
	 */
	private void createCoin() {

		coinsArray = new Array<Coin>();

		MapLayer layer = tileMap.getLayers().get("coins");

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();

		for (MapObject mo : layer.getObjects()) {

			bdef.type = BodyType.StaticBody;

			float x = (float) mo.getProperties().get("x", Float.class)
					/ B2DVars.PPM;
			float y = (float) mo.getProperties().get("y", Float.class)
					/ B2DVars.PPM;

			bdef.position.set(x, y);

			CircleShape cshape = new CircleShape();
			cshape.setRadius(18 / B2DVars.PPM);
			fdef.shape = cshape;
			fdef.isSensor = true;
			fdef.filter.categoryBits = B2DVars.BIT_COIN;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER;

			Body body = world.createBody(bdef);
			body.createFixture(fdef).setUserData("coin");

			Coin c = new Coin(body);
			coinsArray.add(c);
			

			body.setUserData(c);
				Mushroom mush = new Mushroom(body);
				mush.createMush();
		}
	}
	
	/**
	 * Creates the coin.
	 *
	 * @author Tim Killenberger
	 */
	private void createPowerupBlock() {

		powerupArray = new Array<PowerupBlock>();

		MapLayer layer = tileMap.getLayers().get("powerup");

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();

		for (MapObject mo : layer.getObjects()) {

			bdef.type = BodyType.StaticBody;

			float x = (float) mo.getProperties().get("x", Float.class)
					/ B2DVars.PPM;
			float y = (float) mo.getProperties().get("y", Float.class)
					/ B2DVars.PPM;

			bdef.position.set(x, y);

			CircleShape cshape = new CircleShape();
			cshape.setRadius(18 / B2DVars.PPM);
			fdef.shape = cshape;
			fdef.isSensor = false;
			fdef.filter.categoryBits = B2DVars.BIT_POWERUP;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER;

			Body body = world.createBody(bdef);
			body.createFixture(fdef).setUserData("powerup");

			PowerupBlock p = new PowerupBlock(body);
			powerupArray.add(p);

			body.setUserData(p);
		}
	}

	/**
	 * Creates the water.
	 *
	 * @author Tim Killenberger
	 */
	private void createWater() {

		waterArray = new Array<Water>();

		MapLayer layer = tileMap.getLayers().get("Water");

		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();

		for (MapObject mo : layer.getObjects()) {

			bdef.type = BodyType.StaticBody;

			float x = (float) mo.getProperties().get("x", Float.class)
					/ B2DVars.PPM;
			float y = (float) mo.getProperties().get("y", Float.class)
					/ B2DVars.PPM;

			bdef.position.set(x, y);

			CircleShape cshape = new CircleShape();
			cshape.setRadius(35 / B2DVars.PPM);
			fdef.shape = cshape;
			fdef.isSensor = true;
			fdef.filter.categoryBits = B2DVars.BIT_WATER;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER;

			Body body = world.createBody(bdef);
			body.createFixture(fdef).setUserData("water");

			Water w = new Water(body);
			waterArray.add(w);

			body.setUserData(w);
		}
	}

	public static World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		Play.world = world;
	}

	public Box2DDebugRenderer getB2dr() {
		return b2dr;
	}

	public void setB2dr(Box2DDebugRenderer b2dr) {
		this.b2dr = b2dr;
	}

	public OrthographicCamera getB2dCam() {
		return b2dCam;
	}

	public void setB2dCam(OrthographicCamera b2dCam) {
		this.b2dCam = b2dCam;
	}

	public MyContactListener getCl() {
		return cl;
	}

	public void setCl(MyContactListener cl) {
		Play.cl = cl;
	}

	public TiledMap getTileMap() {
		return tileMap;
	}

	public void setTileMap(TiledMap tileMap) {
		this.tileMap = tileMap;
	}

	public float getTileSize() {
		return tileSize;
	}

	public void setTileSize(float tileSize) {
		this.tileSize = tileSize;
	}

	public OrthogonalTiledMapRenderer getTmr() {
		return tmr;
	}

	public void setTmr(OrthogonalTiledMapRenderer tmr) {
		this.tmr = tmr;
	}

	public static Player getPlayer() {
		return player;
	}

	public static void setPlayer(Player player) {
		Play.player = player;
	}

	public Array<Coin> getCoins() {
		return coinsArray;
	}

	public void setCoins(Array<Coin> coins) {
		coinsArray = coins;
	}

	public static Turtle getTurtle() {
		return turtle;
	}

	public static void setTurtle(Turtle turtle) {
		Play.turtle = turtle;
	}

	public Array<Coin> getCoinsArray() {
		return coinsArray;
	}

	public void setCoinsArray(Array<Coin> coinsArray) {
		this.coinsArray = coinsArray;
	}

	public Array<Turtle> getTurtleArray() {
		return turtleArray;
	}

	public void setTurtleArray(Array<Turtle> turtleArray) {
		this.turtleArray = turtleArray;
	}

	public Array<Water> getWaterArray() {
		return waterArray;
	}

	public void setWaterArray(Array<Water> waterArray) {
		this.waterArray = waterArray;
	}

	public static Vector2 getMovement() {
		return movement;
	}

	public static void setMovement(Vector2 movement) {
		Play.movement = movement;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getGravity() {
		return gravity;
	}

	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

	public float getNitro() {
		return nitro;
	}

	public void setNitro(float nitro) {
		this.nitro = nitro;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public float getDesiredVelocity() {
		return desiredVelocity;
	}

	public void setDesiredVelocity(float desiredVelocity) {
		this.desiredVelocity = desiredVelocity;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public MapProperties getProp() {
		return prop;
	}

	public void setProp(MapProperties prop) {
		this.prop = prop;
	}

	public int getMapWidth() {
		return mapWidth;
	}

	public void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}

	public int getMapHeight() {
		return mapHeight;
	}

	public void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}

	public int getTilePixelWidth() {
		return tilePixelWidth;
	}

	public void setTilePixelWidth(int tilePixelWidth) {
		this.tilePixelWidth = tilePixelWidth;
	}

	public int getTilePixelHeight() {
		return tilePixelHeight;
	}

	public void setTilePixelHeight(int tilePixelHeight) {
		this.tilePixelHeight = tilePixelHeight;
	}

	public int getMapPixelWidth() {
		return mapPixelWidth;
	}

	public void setMapPixelWidth(int mapPixelWidth) {
		this.mapPixelWidth = mapPixelWidth;
	}

	public int getMapPixelHeight() {
		return mapPixelHeight;
	}

	public void setMapPixelHeight(int mapPixelHeight) {
		this.mapPixelHeight = mapPixelHeight;
	}

	public RayHandler getRayHandler() {
		return rayHandler;
	}

	public void setRayHandler(RayHandler rayHandler) {
		this.rayHandler = rayHandler;
	}

	public float getTIMESTEP() {
		return TIMESTEP;
	}

	public int getVELOCITYITERATIONS() {
		return VELOCITYITERATIONS;
	}

	public int getPOSITIONITERAIONS() {
		return POSITIONITERAIONS;
	}
}