package ch.bbc.zkillt.states;

import box2dLight.RayHandler;
import ch.bbc.zkillt.Game;
import ch.bbc.zkillt.entities.Coin;
import ch.bbc.zkillt.entities.Player;
import ch.bbc.zkillt.entities.Turtle;
import ch.bbc.zkillt.entities.Water;
import ch.bbc.zkillt.handlers.B2DVars;
import ch.bbc.zkillt.handlers.GameStateManager;
import ch.bbc.zkillt.handlers.MyContactListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
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
	
	private World world;
	private Box2DDebugRenderer b2dr;
	
	private OrthographicCamera b2dCam;
	public static MyContactListener cl;
	
	private TiledMap tileMap;
	private float tileSize;
	private OrthogonalTiledMapRenderer tmr;	
	public static Player player;
	public static Turtle turtle;
	private Array<Coin> coinsArray;
	private Array<Turtle> turtleArray;
	private Array<Water> waterArray;
	
	public static Vector2 movement = new Vector2(0, 1);
	private float speed = 6, gravity = 12, nitro = 0.6f;
	private final float TIMESTEP = 1 / 60;
	private final int VELOCITYITERATIONS = 8, POSITIONITERAIONS = 3;
	
	float x = cam.position.x - cam.viewportWidth * cam.zoom;
	float y = cam.position.y - cam.viewportHeight * cam.zoom;
	float width = cam.viewportWidth * Game.WINDOW_WIDTH;
	float height = cam.viewportHeight * Game.WINDOW_HEIGHT;
	private boolean right = false;
	float desiredVelocity;
 
	
	private RayHandler rayHandler;
	
	public Play(GameStateManager gsm) {
		
		super(gsm);
		
		// set up box2d stuff
		world = new World(new Vector2(0, -9.81f), true);
		cl = new MyContactListener();
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();
		rayHandler = new RayHandler(world, 50, 50);
		rayHandler.setCombinedMatrix(cam.combined);
		
//		new ConeLight(rayHandler, 5000, com.badlogic.gdx.graphics.Color.WHITE, 2220,cam.position.x - 600, cam.position.y + 600, -45, 45);
//		new PointLight(rayHandler, 5000, com.badlogic.gdx.graphics.Color.YELLOW, 1200, cam.position.x, cam.position.y);
		
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

		// set up box2d cam
		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, Game.WINDOW_WIDTH / B2DVars.PPM, Game.WINDOW_HEIGHT / B2DVars.PPM);		
	}
	
	public void update(float dt) {
		
		System.out.println("Turtles to Remove: " + cl.getBodiesToRemove2().size);
		
		if (cl.isInWater()) {
			gravity = 3;
		} else {
			gravity = 12;
		}
		
		
//		System.out.println("Gravity: " + gravity);
		
		tmr.setView(cam.combined, x * 2, y, width, height);
		
		desiredVelocity = Math.min(movement.x, player.getBody().getLinearVelocity().x + nitro);
		
		//apply gravity 
		movement.y -= gravity * dt;
		
		if(movement.y > speed)
			movement.y = speed;
		else if(movement.y < -speed)
			movement.y = -speed;		
		
		// check input
		handleInput();
		
		// set player speed 
		player.getBody().setLinearVelocity(desiredVelocity, movement.y);

		// update box2d
		world.step(dt, 6, 2);
		player.update(dt);

		
		// remove Coins
		Array<Body> bodies = cl.getBodiesToRemove();
		for(int i = 0; i < bodies.size; i++) {
			Body b = bodies.get(i);
			coinsArray.removeValue((Coin) b.getUserData(), true);
			world.destroyBody(b);
			player.collectCoin();
		}
		
		bodies.clear();		
		
		// remove Coins
		Array<Body> bodies2 = cl.getBodiesToRemove2();
		for(int i = 0; i < bodies2.size; i++) {
			Body b = bodies2.get(i);
			turtleArray.removeValue((Turtle) b.getUserData(), true);
			world.destroyBody(b);
			System.out.println("Turtle-Body Destroyed");
		}
		
		bodies2.clear();
				
				
		for (Turtle t : turtleArray) {
			if (t.getBody().getPosition().x  == t.getOldX()) {
				if(right) {
					t.getBody().setLinearVelocity(2.0f, 0);
					t.changeRegion(1, 1 / 12);
					right = false;
				}
				else if (!right) {
					t.getBody().setLinearVelocity(-2.0f, 0);
					t.changeRegion(0, 1 / 12);
					right = true;
				}
			}
			t.setOldX(t.getBody().getPosition().x);
		}
		
		for(int i = 0; i < coinsArray.size; i++) {
			coinsArray.get(i).update(dt);
		}	
		
		for(int i = 0; i < turtleArray.size; i++) {
			turtleArray.get(i).update(dt);
		}
	}
	
	public void render() {
		
		
		// clear screen
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		b2dCam.position.set(cam.position.x , cam.position.y, 0);
		
		sb.begin();
		tmr.getBatch().begin();
		tmr.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get("background"));
		tmr.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get("ground"));
		tmr.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get("schraeg"));
		tmr.getBatch().end();
		sb.end();
		
		// draw Turtle
		for(int i = 0; i < turtleArray.size; i++) {
				turtleArray.get(i).render(sb);	
		}
		
		// draw player
		player.render(sb);
		
		sb.setProjectionMatrix(cam.combined);
		
		sb.begin();
		tmr.getBatch().begin();
		tmr.renderTileLayer((TiledMapTileLayer) tileMap.getLayers().get("Water2"));
		tmr.getBatch().end();
		sb.end();
		
		//draw box2d world
		b2dr.render(world, b2dCam.combined);
		
		// draw Coins
		for(int i = 0; i < coinsArray.size; i++) {
			coinsArray.get(i).render(sb);		
		}
		
		
//		rayHandler.updateAndRender();
		
		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERAIONS);

	}
	
	
	public void handleInput() {
		Gdx.input.setInputProcessor(new InputProcessor() {
			
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean scrolled(int amount) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				// TODO Auto-generated method stub
				return false;
			}
				
			@Override
			public boolean keyUp(int keycode) {
				switch(keycode) {
				case Keys.A:
					player.changeRegion(2, 10000);
					// Sprite für Stehenbleiben ändern
					movement.x = 0;
					break;
				case Keys.D:
					player.changeRegion(3, 10000); // Sprite für Stehenbleiben ändern (1/12f) = delay für Animationen
					movement.x = 0;
					break;
				case Keys.SHIFT_LEFT: 
					if(Gdx.input.isKeyPressed(Keys.A)) {
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
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
				case Keys.SPACE:
					if(cl.isPlayerOnGround()){
						movement.y = speed * 1.2f;
						if(cl.isInWater()){
							System.out.println("Ground + Water!");
							movement.y = speed / 2;
						}
					}
					else if(cl.isInWater()){
						System.out.println("Water!");
						movement.y = speed / 2;
					}
					MyContactListener.water = false;
					break;
					
				case Keys.A:
					if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
						movement.x = -speed;
					}
					player.changeRegion(2, 1/12f);
					movement.x = -speed;
					break;
					
				case Keys.D:					
					if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
						movement.x = speed;
					}
					player.changeRegion(3, 1/12f);
					movement.x = speed;
					break;
					
				case Keys.SHIFT_LEFT:
					if(Gdx.input.isKeyPressed(Keys.A)) {
						player.changeRegion(2, 1 / 18f);
						movement.x = -speed * 1.6f;
					} else if (Gdx.input.isKeyPressed(Keys.D)) {
						player.changeRegion(3, 1 / 18f);
						movement.x = speed * 1.6f;
					}
					break;
				}
				return true;
			}
		});
	}
	
	public void dispose() {}
	
	private void createPlayer() {
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		
		// create player
		bdef.position.set(500 / B2DVars.PPM, 700 / B2DVars.PPM);
		bdef.type = BodyType.DynamicBody;
		Body body = world.createBody(bdef);

		shape.setAsBox(27 / B2DVars.PPM, 50 / B2DVars.PPM);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_COIN | B2DVars.BIT_SCHRAEG | B2DVars.BIT_WATER | B2DVars.BIT_ENEMY;
		body.createFixture(fdef).setUserData("player");
		
		// create foot sensor
		shape.setAsBox(35 / B2DVars.PPM, 2 / B2DVars.PPM, new Vector2(0, -50 / B2DVars.PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_COIN | B2DVars.BIT_SCHRAEG | B2DVars.BIT_WATER | B2DVars.BIT_ENEMY;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("foot");
		
		// create head sensor
		shape.setAsBox(18 / B2DVars.PPM, 18 / B2DVars.PPM, new Vector2(0, 68 / B2DVars.PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_COIN | B2DVars.BIT_SCHRAEG | B2DVars.BIT_WATER | B2DVars.BIT_ENEMY;
		fdef.isSensor = false;
		body.createFixture(fdef).setUserData("head");
		
		// create player
		player = new Player(body);
		body.setUserData(player);
	}
	
	private void createTiles() {
		
		// load tile map
		tileMap = new TmxMapLoader().load("ressources/maps/test.tmx");
		tmr = new OrthogonalTiledMapRenderer(tileMap);
		tileSize = tileMap.getProperties().get("tilewidth", Integer.class);
		TiledMapTileLayer layer;
		
		layer = (TiledMapTileLayer) tileMap.getLayers().get("ground");
		createLayer(layer, false, B2DVars.BIT_GROUND);
		
		layer = (TiledMapTileLayer) tileMap.getLayers().get("schraeg");
		createLayerSchraeg(layer, false, B2DVars.BIT_SCHRAEG);
	}
	
	private void createLayer(TiledMapTileLayer layer, boolean filter, short bits) {
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		
		// go through all the cells in the layer
		for(int row = 0; row < layer.getHeight(); row++) {
			for(int col = 0; col < layer.getWidth(); col++) {
				
				// get cell
				Cell cell = layer.getCell(col, row);
				
				// check if cell exists
				if(cell == null) continue;
				if(cell.getTile() == null) continue;
				
				// create a body + fixture from cell
				bdef.type = BodyType.StaticBody;
				bdef.position.set(
					(col + 0.5f) * tileSize / B2DVars.PPM,
					(row + 0.5f) * tileSize / B2DVars.PPM
				);
				
				ChainShape cs = new ChainShape();
				Vector2[] v = new Vector2[3];
				// Ecke unten Links
				v[0] = new Vector2(
					-tileSize / 2 / B2DVars.PPM, -tileSize / 2 / B2DVars.PPM);
				// Ecke unten Rechts
				v[1] = new Vector2(
					-tileSize / 2 / B2DVars.PPM, tileSize / 2 / B2DVars.PPM);
				// Ecke oben Links
				v[2] = new Vector2(
					tileSize / 2 / B2DVars.PPM, (tileSize / 2 / B2DVars.PPM));
//				// Ecke oben Rechts
//				v[3] = new Vector2(
//						(tileSize / 2 / B2DVars.PPM) / 2, (-tileSize / 2 / B2DVars.PPM) / 2);
				cs.createChain(v);
				fdef.friction = 0;
				fdef.shape = cs;
				fdef.filter.categoryBits = bits;
				fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_ENEMY;;
				fdef.isSensor = filter;
				world.createBody(bdef).createFixture(fdef);
			}
		}
	}	
	
	private void createLayerSchraeg(TiledMapTileLayer layer, boolean filter, short bits) {
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		
		// go through all the cells in the layer
		for(int row = 0; row < layer.getHeight(); row++) {
			for(int col = 0; col < layer.getWidth(); col++) {
				
				// get cell
				Cell cell = layer.getCell(col, row);
				
				// check if cell exists
				if(cell == null) continue;
				if(cell.getTile() == null) continue;
				
				// create a body + fixture from cell
				
				bdef.type = BodyType.StaticBody;
				bdef.position.set(
					(col + 0.5f) * tileSize / B2DVars.PPM,
					(row + 0.5f) * tileSize / B2DVars.PPM
				);
				
				ChainShape cs = new ChainShape();
				Vector2[] v = new Vector2[3];
				// Ecke unten Links
				v[0] = new Vector2(
					-tileSize / 2 / B2DVars.PPM, -tileSize / 2 / B2DVars.PPM);
				// Ecke unten Rechts
				v[1] = new Vector2(
					-tileSize / 2 / B2DVars.PPM, tileSize / 2 / B2DVars.PPM);
				// Ecke oben Links
				v[2] = new Vector2(
					tileSize / 2 / B2DVars.PPM, tileSize / 2 / B2DVars.PPM / 2 - 0.52f);
//				// Ecke oben Rechts
//				v[3] = new Vector2(
//						(tileSize / 2 / B2DVars.PPM) / 2, (-tileSize / 2 / B2DVars.PPM) / 2);
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
	
	private void createTurtle() {
		
		turtleArray = new Array<Turtle>();
		
		MapLayer layer = tileMap.getLayers().get("turtles");
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		
		for(MapObject mo : layer.getObjects()) {
			
			bdef.type = BodyType.DynamicBody;
			
			float x = (float) mo.getProperties().get("x", Float.class) / B2DVars.PPM;
			float y = (float) mo.getProperties().get("y", Float.class) / B2DVars.PPM;
			
			// create enemy
			bdef.position.set(x, y);
			Body body = world.createBody(bdef);
			body.setLinearVelocity(2.0f, 0);

			shape.setAsBox(22 / B2DVars.PPM, 40 / B2DVars.PPM);
			fdef.shape = shape;
			fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
			fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_PLAYER| B2DVars.BIT_SCHRAEG;
			fdef.isSensor = false;
			body.createFixture(fdef).setUserData("turtle");
			
			
			// create Head sensor
			shape.setAsBox(26 / B2DVars.PPM, 5 / B2DVars.PPM, new Vector2(0, 40 / B2DVars.PPM), 0);
			fdef.shape = shape;
			fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_GROUND | B2DVars.BIT_SCHRAEG;
			fdef.isSensor = false;
			body.createFixture(fdef).setUserData("turtleHead");
			
			turtle = new Turtle(body);
			turtleArray.add(turtle);
			
			// create player
			turtle = new Turtle(body);
			body.setUserData(turtle);
		}
	}
	
	private void createCoin() {
		
		coinsArray = new Array<Coin>();
		
		MapLayer layer = tileMap.getLayers().get("coins");
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		
		for(MapObject mo : layer.getObjects()) {
			
			bdef.type = BodyType.StaticBody;
			
			float x = (float) mo.getProperties().get("x", Float.class) / B2DVars.PPM;
			float y = (float) mo.getProperties().get("y", Float.class) / B2DVars.PPM;
			
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
		}
	}
	
	private void createWater() {
		
		waterArray = new Array<Water>();
		
		MapLayer layer = tileMap.getLayers().get("Water");
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		
		for(MapObject mo : layer.getObjects()) {
			
			bdef.type = BodyType.StaticBody;
			
			float x = (float) mo.getProperties().get("x", Float.class) / B2DVars.PPM;
			float y = (float) mo.getProperties().get("y", Float.class) / B2DVars.PPM;
			
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
			System.out.println(body.getUserData().toString());
		}
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
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
}