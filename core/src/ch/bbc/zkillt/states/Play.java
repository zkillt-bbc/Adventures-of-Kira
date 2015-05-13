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
import ch.bbc.zkillt.handlers.WorldLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Play extends GameState {
	
	//---------- Debugging ---------------------//

	private Box2DDebugRenderer b2dr;
	
	//---------- Listener properties -----------//

	public static MyContactListener cl;
	
	//---------- Class Instances ---------------//

	public static Player player;
	public static Turtle turtle;
	private Array<Coin> coinsArray;
	private Array<Turtle> turtleArray;
	private Array<Water> waterArray;
	
	//---------- World properties --------------//
		
	private OrthogonalTiledMapRenderer tmr;	
	private static World world;
	public static Vector2 movement = new Vector2(0, 1);
	private float speed = 6, gravity = 12, nitro = 0.6f;
	private final float TIMESTEP = 1 / 60;
	private final int VELOCITYITERATIONS = 8, POSITIONITERAIONS = 3;
		private boolean right = false;
	private float desiredVelocity;
	
	//---------- Camera Positioning ------------//
	
	private OrthographicCamera b2dCam;
	float x = cam.position.x - cam.viewportWidth * cam.zoom;
	float y = cam.position.y - cam.viewportHeight * cam.zoom;
	float width = cam.viewportWidth * Game.WINDOW_WIDTH;
	float height = cam.viewportHeight * Game.WINDOW_HEIGHT;

	//---------- Tilemap properties ------------//
	
	private TiledMap tileMap;
	private float tileSize;
	
	private MapProperties prop;
	private int mapWidth;
	private int mapHeight;
	private int tilePixelWidth;
	private int tilePixelHeight;
	private int mapPixelWidth;
	private int mapPixelHeight;
	
	private RayHandler rayHandler;
	
	public Play(GameStateManager gsm) {
		
		super(gsm);
		
		// set up box2d stuff
		b2dr = new Box2DDebugRenderer();
		new WorldLoader(gsm);

		rayHandler = new RayHandler(world, 50, 50);
		rayHandler.setCombinedMatrix(cam.combined);
		
//		new ConeLight(rayHandler, 5000, com.badlogic.gdx.graphics.Color.WHITE, 2220,cam.position.x - 600, cam.position.y + 600, -45, 45);
//		new PointLight(rayHandler, 5000, com.badlogic.gdx.graphics.Color.YELLOW, 1200, cam.position.x, cam.position.y);
		
		
		// set up box2d cam
		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, Game.WINDOW_WIDTH / B2DVars.PPM, Game.WINDOW_HEIGHT / B2DVars.PPM);		
	}
	
	public void update(float dt) {

		//--------- Player states -----------//
				
		// in water
		if (WorldLoader.getCl().isInWater()) {
			gravity = 3;
		} else {
			gravity = 12f;
		}
		
		if(WorldLoader.getCl().isEnemyCollision()) {
			movement.y = speed / 1.25f;
			WorldLoader.getCl().setEnemyCollision(false);
		}


//		System.out.println("Gravity: " + gravity);
		
		WorldLoader.tmr.setView(cam.combined, x * 2, y, width, height);
		
		desiredVelocity = Math.min(movement.x, WorldLoader.player.getBody().getLinearVelocity().x + nitro);		
		
		//apply gravity 
		movement.y -= gravity * dt;
		
		if(movement.y > speed)
			movement.y = speed;
		else if(movement.y < -speed)
			movement.y = -speed;		
		
		// check input
		handleInput();
		
		// set player speed 
		WorldLoader.player.getBody().setLinearVelocity(desiredVelocity, movement.y);

		// update box2d
		WorldLoader.world.step(dt, 6, 2);
		WorldLoader.player.update(dt);

		
		// remove Coins
		Array<Body> bodies = WorldLoader.getCl().getBodiesToRemove();
		for(int i = 0; i < bodies.size; i++) {
			Body b = bodies.get(i);
			WorldLoader.getCoinsArray().removeValue((Coin) b.getUserData(), true);
			WorldLoader.world.destroyBody(b);
			WorldLoader.player.collectCoin();
		}
		
		bodies.clear();		
		
		// remove Coins
		Array<Body> bodies2 = WorldLoader.getCl().getBodiesToRemove2();
		for(int i = 0; i < bodies2.size; i++) {
			Body b = bodies2.get(i);
			WorldLoader.getTurtleArray().removeValue((Turtle) b.getUserData(), false);
			WorldLoader.world.destroyBody(b);
			WorldLoader.player.numTurtles++;
		}
		
		bodies2.clear();
				
				
		for (Turtle t : WorldLoader.getTurtleArray()) {
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
		
		for(int i = 0; i < WorldLoader.getCoinsArray().size; i++) {
			WorldLoader.getCoinsArray().get(i).update(dt);
		}
		
		for(int i = 0; i < WorldLoader.getTurtleArray().size; i++) {
			WorldLoader.getTurtleArray().get(i).update(dt);
		}
	}
	
	public void render() {
		
		
		// clear screen
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		b2dCam.position.set(cam.position.x , cam.position.y, 0);
		
		sb.begin();
		WorldLoader.tmr.getBatch().begin();
		WorldLoader.tmr.renderTileLayer((TiledMapTileLayer) WorldLoader.tileMap.getLayers().get("background"));
		WorldLoader.tmr.renderTileLayer((TiledMapTileLayer) WorldLoader.tileMap.getLayers().get("bg_deko"));
		WorldLoader.tmr.renderTileLayer((TiledMapTileLayer) WorldLoader.tileMap.getLayers().get("ground"));
		WorldLoader.tmr.renderTileLayer((TiledMapTileLayer) WorldLoader.tileMap.getLayers().get("schraeg"));
		WorldLoader.tmr.renderTileLayer((TiledMapTileLayer) WorldLoader.tileMap.getLayers().get("decoration"));
		WorldLoader.tmr.getBatch().end();
		sb.end();
		
		// draw Turtle
		for(int i = 0; i < WorldLoader.getTurtleArray().size; i++) {
				WorldLoader.getTurtleArray().get(i).render(sb);	
		}
		
		// draw Coins
		for(int i = 0; i < WorldLoader.coinsArray.size; i++) {
			WorldLoader.coinsArray.get(i).render(sb);		
		}
		
		// draw player
		WorldLoader.player.render(sb);
		
		sb.setProjectionMatrix(cam.combined);
		
		sb.begin();
		WorldLoader.tmr.getBatch().begin();
		WorldLoader.tmr.renderTileLayer((TiledMapTileLayer) WorldLoader.tileMap.getLayers().get("Water2"));
		WorldLoader.tmr.getBatch().end();
		sb.end();
		
		//draw box2d world
		b2dr.render(WorldLoader.world, b2dCam.combined);
		
		
//		rayHandler.updateAndRender();
		
		WorldLoader.world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERAIONS);

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
					WorldLoader.player.changeRegion(2, 10000);
					// Sprite für Stehenbleiben ändern
					movement.x = 0;
					break;
				case Keys.D:
					WorldLoader.player.changeRegion(3, 10000); // Sprite für Stehenbleiben ändern (1/12f) = delay für Animationen
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
					if(WorldLoader.getCl().isPlayerOnGround()){
						movement.y = speed * 1.2f;
						if(WorldLoader.getCl().isInWater()){
							movement.y = speed / 2;
						}
					}
					else if(WorldLoader.getCl().isInWater()){
						movement.y = speed / 2;
					}
					WorldLoader.getCl().setWater(false);
					break;
					
				case Keys.A:
					if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
						movement.x = -speed;
					}
					WorldLoader.player.changeRegion(2, 1/12f);
					movement.x = -speed;
					break;
					
				case Keys.D:					
					if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
						movement.x = speed;
					}
					WorldLoader.player.changeRegion(3, 1/12f);
					movement.x = speed;
					break;
					
				case Keys.SHIFT_LEFT:
					if(Gdx.input.isKeyPressed(Keys.A)) {
						WorldLoader.player.changeRegion(2, 1 / 18f);
						movement.x = -speed * 1.6f;
					} else if (Gdx.input.isKeyPressed(Keys.D)) {
						WorldLoader.player.changeRegion(3, 1 / 18f);
						movement.x = speed * 1.6f;
					}
					break;
				}
				return true;
			}
		});
	}
	
	public void dispose() {}

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