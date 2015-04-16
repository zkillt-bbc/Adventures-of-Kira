package ch.bbc.zkillt.states;

import static ch.bbc.zkillt.handlers.B2DVars.PPM;
import ch.bbc.zkillt.entities.Coin;
import ch.bbc.zkillt.entities.Player;
import ch.bbc.zkillt.handlers.B2DVars;
import ch.bbc.zkillt.handlers.GameStateManager;
import ch.bbc.zkillt.handlers.MyContactListener;
import ch.bbc.zkillt.handlers.MyInput;
import ch.bbcag.zkilt.Game;

import com.badlogic.gdx.Gdx;
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
	private MyContactListener cl;
	
	private TiledMap tileMap;
	private float tileSize;
	private OrthogonalTiledMapRenderer tmr;
	
	private Player player;
	private Array<Coin> coins;
	
	public Play(GameStateManager gsm) {
		
		super(gsm);
		
		world = new World(new Vector2(0, -9.81f), true);
		cl = new MyContactListener();
		world.setContactListener(cl);
		b2dr = new Box2DDebugRenderer();
		
		// create player
		createPlayer();
		
		// create tiles
		createTiles();
		
		// create cois
		createCoins();
		
		
		// set up box2d cam
		b2dCam = new OrthographicCamera();
		b2dCam.setToOrtho(false, Game.WINDOW_WIDTH / PPM, Game.WINDOW_HEIGHT / PPM);
		
		
	}
	
	public void handleInput() {

		// player jump
		if(MyInput.isPressed(MyInput.JUMP)) {
			if(cl.isPlayerOnGround()) {
				player.getBody().applyForceToCenter(0 , 150, true);
			}
		}

		if(MyInput.isPressed(MyInput.LEFT)) {
				player.getBody().applyForceToCenter(-50 , 0, true);
		} 
		
		if(MyInput.isPressed(MyInput.RIGHT)) {
				player.getBody().applyForceToCenter(50, 0, true);
		} 
	}
	
	public void update(float dt) {
		
		handleInput();
		
		world.step(dt, 6, 2);
		
		// Update player
		player.update(dt);
		
		for(int i = 0; i < coins.size; i++) {
			coins.get(i).update(dt);
		}
	}
	
	public void render() {
		
		// clear screen
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		// draw Tilemap
		tmr.setView(cam);
		tmr.render();
		
		// draw player
		sb.setProjectionMatrix(cam.combined);
		player.render(sb);
		
		for(int i = 0; i < coins.size; i++) {
			coins.get(i).render(sb);
		}
		
		// draw box2d world
		b2dr.render(world, b2dCam.combined);
		
	}
	
	public void dispose() {}
	
	private void createPlayer() {
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		
		// create player
		bdef.position.set(520 / PPM, 800 / PPM);
		bdef.type = BodyType.DynamicBody;
		Body body = world.createBody(bdef);
		
		shape.setAsBox(32 / PPM, 64 / PPM);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND;
		body.createFixture(fdef).setUserData("player");
		
		// create foot sensor
		shape.setAsBox(13 / PPM, 2 / PPM, new Vector2(0, -64 / PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("foot");
		
		// create Player
		player = new Player(body);
		
	}
	
	private void createTiles() {
		
		// load tile map
		tileMap = new TmxMapLoader().load("ressources/maps/test.tmx");
		tmr = new OrthogonalTiledMapRenderer(tileMap);
		tileSize = tileMap.getProperties().get("tilewidth", Integer.class);
		
		TiledMapTileLayer layer;
		
		layer = (TiledMapTileLayer) tileMap.getLayers().get("ground");
		createLayer(layer, B2DVars.BIT_GROUND);		
	}
	
	private void createLayer(TiledMapTileLayer layer, short bits) {
		
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
					(col + 0.5f) * tileSize / PPM,
					(row + 0.5f) * tileSize / PPM
				);
				
				ChainShape cs = new ChainShape();
				Vector2[] v = new Vector2[3];
				v[0] = new Vector2(
					-tileSize / 2 / PPM, -tileSize / 2 / PPM);
				v[1] = new Vector2(
					-tileSize / 2 / PPM, tileSize / 2 / PPM);
				v[2] = new Vector2(
					tileSize / 2 / PPM, tileSize / 2 / PPM);
				cs.createChain(v);
				fdef.friction = 0;
				fdef.shape = cs;
				fdef.filter.categoryBits = bits;
				fdef.filter.maskBits = B2DVars.BIT_PLAYER;
				fdef.isSensor = false;
				world.createBody(bdef).createFixture(fdef);				
			}
		}
	}
	
	public void createCoins() {
		
		coins = new Array<Coin>();
		
		MapLayer layer = tileMap.getLayers().get("coins");
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		
		for(MapObject mo : layer.getObjects()) {
			
			bdef.type = BodyType.StaticBody;
			
			float x = mo.getProperties().get("x", Float.class)  / PPM;
			float y = mo.getProperties().get("y", Float.class) / PPM;	
			
			bdef.position.set(x, y);
			CircleShape circle = new CircleShape();
			circle.setRadius(4.25f / 35);
			fdef.shape= circle;
			fdef.isSensor = true;
			fdef.filter.categoryBits = B2DVars.BIT_COIN;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER;
			
			Body body = world.createBody(bdef);
			body.createFixture(fdef);
			
			Coin c = new Coin(body);
			coins.add(c);
			
			body.setUserData(c);
			
		}
	}
}