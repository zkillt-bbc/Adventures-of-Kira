package ch.bbc.zkillt.handlers;

import java.util.ArrayList;

import ch.bbc.zkillt.entities.Coin;
import ch.bbc.zkillt.entities.Player;
import ch.bbc.zkillt.entities.Turtle;
import ch.bbc.zkillt.entities.Water;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class WorldLoader {

	public static World world;
	public static TiledMap tileMap;
	public static float tileSize;
	public static OrthogonalTiledMapRenderer tmr;
	public static Player player;
	public static Turtle turtle;
	public static Coin coin;
	public static Array<Coin> coinsArray;

	public ArrayList<Object> worldObjects;
	private static Array<Turtle> turtleArray;
	private static Array<Water> waterArray;
	private static MyContactListener cl;
	
	
	private static MapProperties prop;
	private static int mapWidth;
	private static int mapHeight;
	private static int tilePixelWidth;
	private static int tilePixelHeight;
	private static int mapPixelWidth;
	private static int mapPixelHeight;
	
	
	public WorldLoader(GameStateManager gsm) {
		world = new World(new Vector2(0, -9.81f), true);
		setCl(new MyContactListener());
		world.setContactListener(getCl());

		// create player
		createPlayer();
		
		// create tiles
		createTiles();
		
		// create Water
		createWater();
		
		// create Turtle
		createTurtle();
		
		// create Coins
		createCoin();
		
		//create Finish line
		createFinish();
	}

	public static void createPlayer() {
		
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
		fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_COIN | B2DVars.BIT_SCHRAEG | B2DVars.BIT_WATER | B2DVars.BIT_ENEMY | B2DVars.BIT_FINISH;
		body.createFixture(fdef).setUserData("player");
		
		// create foot sensor
		shape.setAsBox(30 / B2DVars.PPM, 2 / B2DVars.PPM, new Vector2(0, -50 / B2DVars.PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_COIN | B2DVars.BIT_SCHRAEG | B2DVars.BIT_WATER | B2DVars.BIT_ENEMY | B2DVars.BIT_FINISH;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("foot");
		
		// create head sensor
		shape.setAsBox(18 / B2DVars.PPM, 50 / B2DVars.PPM, new Vector2(0, 68 / B2DVars.PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_COIN | B2DVars.BIT_SCHRAEG | B2DVars.BIT_WATER | B2DVars.BIT_ENEMY | B2DVars.BIT_FINISH;
		fdef.isSensor = false;
		body.createFixture(fdef).setUserData("head");
		
		// create player
		player = new Player(body);
		body.setUserData(player);
	}
	
	public static void createTiles() {
		
		// load tile map
		tileMap = new TmxMapLoader().load("ressources/maps/test.tmx");
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
	
	public static void createLayer(TiledMapTileLayer layer, boolean filter, short bits) {
		
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
				Vector2[] v = new Vector2[5];
				// von unten links nach oben links
				v[0] = new Vector2(-tileSize / 2 / B2DVars.PPM, -tileSize / 2 / B2DVars.PPM);
				// Von oben links nach oben rechts
				v[1] = new Vector2(-tileSize / 2 / B2DVars.PPM, tileSize / 2 / B2DVars.PPM);
				// Ecke oben Links
				v[2] = new Vector2(tileSize / 2 / B2DVars.PPM, tileSize / 2 / B2DVars.PPM);
				// Ecke oben Rechts
				v[3] = new Vector2(tileSize / 2 / B2DVars.PPM, -tileSize / 2 / B2DVars.PPM);
				// von unten links nach oben links
				v[4] = new Vector2(-tileSize / 2 / B2DVars.PPM, -tileSize / 2 / B2DVars.PPM);
				
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
	
	public static void createLayerSchraeg(TiledMapTileLayer layer, boolean filter, short bits) {
		
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
	
	public static void createTurtle() {
		
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

			shape.setAsBox(22 / B2DVars.PPM, 5 / B2DVars.PPM, new Vector2(0, 60 / B2DVars.PPM), 0);
			fdef.shape = shape;
			fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_GROUND | B2DVars.BIT_SCHRAEG;
			fdef.isSensor = false;
			
			body.createFixture(fdef).setUserData("turtle");
			
			// create Head sensor
			shape.setAsBox(22 / B2DVars.PPM, 40 / B2DVars.PPM, new Vector2(0, 15 / B2DVars.PPM), 0);
			fdef.shape = shape;
			fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER | B2DVars.BIT_GROUND | B2DVars.BIT_SCHRAEG;
			fdef.isSensor = false;
			body.createFixture(fdef).setUserData("turtleHead");
			
			
			// create player
			turtle = new Turtle(body);
			turtleArray.add(turtle);
			body.setUserData(turtle);
		}
		player.totalTurtles = turtleArray.size;

	}
	
	public static void createCoin() {
		
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
			
			coin = new Coin(body);
			coinsArray.add(coin);
			
			body.setUserData(coin);
		}
	}
	
	public static void createFinish() {
		
		coinsArray = new Array<Coin>();
		
		MapLayer layer = tileMap.getLayers().get("finish");
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		Shape shape;
		
		for(MapObject mo : layer.getObjects()) {
			if(mo instanceof RectangleMapObject)
			{
			shape = CollisionWorking.getRectangle((RectangleMapObject)mo);
			bdef.type = BodyType.StaticBody;
			
			float x = (float) mo.getProperties().get("x", Float.class) / B2DVars.PPM;
			float y = (float) mo.getProperties().get("y", Float.class) / B2DVars.PPM;
			
			bdef.position.set(x, y);
			
			CircleShape cshape = new CircleShape();
			cshape.setRadius(18 / B2DVars.PPM);
			fdef.shape = shape;
			fdef.isSensor = true;
			fdef.filter.categoryBits = B2DVars.BIT_FINISH;
			fdef.filter.maskBits = B2DVars.BIT_PLAYER;
			
			Body body = world.createBody(bdef);
			body.createFixture(fdef).setUserData("finish");
			
			body.setUserData("finish");
			System.out.println( mo.getProperties().get("width"));
		}
		}
	}
	
	public static void createWater() {
		
		waterArray = new Array<Water>();
		
		MapLayer layer = tileMap.getLayers().get("Water");
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		Shape shape;
		
		for(MapObject mo : layer.getObjects()) {
			if(mo instanceof RectangleMapObject)
			{
				shape = CollisionWorking.getRectangle((RectangleMapObject)mo);
				bdef.type = BodyType.StaticBody;
				
				float x = mo.getProperties().get("x", Float.class) / B2DVars.PPM;
				float y = mo.getProperties().get("y", Float.class) / B2DVars.PPM;
				
				bdef.position.set(x, y);
				
				shape.setRadius(35 / B2DVars.PPM);
				fdef.shape = shape;
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
	}

	public static World getWorld() {
		return world;
	}

	public static void setWorld(World world) {
		WorldLoader.world = world;
	}

	public static TiledMap getTileMap() {
		return tileMap;
	}

	public static void setTileMap(TiledMap tileMap) {
		WorldLoader.tileMap = tileMap;
	}

	public static float getTileSize() {
		return tileSize;
	}

	public static void setTileSize(float tileSize) {
		WorldLoader.tileSize = tileSize;
	}

	public static OrthogonalTiledMapRenderer getTmr() {
		return tmr;
	}

	public static void setTmr(OrthogonalTiledMapRenderer tmr) {
		WorldLoader.tmr = tmr;
	}

	public static Player getPlayer() {
		return player;
	}

	public static void setPlayer(Player player) {
		WorldLoader.player = player;
	}

	public static Turtle getTurtle() {
		return turtle;
	}

	public static void setTurtle(Turtle turtle) {
		WorldLoader.turtle = turtle;
	}

	public ArrayList<Object> getWorldObjects() {
		return worldObjects;
	}

	public void setWorldObjects(ArrayList<Object> worldObjects) {
		this.worldObjects = worldObjects;
	}

	public static Array<Coin> getCoinsArray() {
		return coinsArray;
	}

	public static void setCoinsArray(Array<Coin> coinsArray) {
		WorldLoader.coinsArray = coinsArray;
	}

	public static Array<Turtle> getTurtleArray() {
		return turtleArray;
	}

	public static void setTurtleArray(Array<Turtle> turtleArray) {
		WorldLoader.turtleArray = turtleArray;
	}

	public static Array<Water> getWaterArray() {
		return waterArray;
	}

	public static void setWaterArray(Array<Water> waterArray) {
		WorldLoader.waterArray = waterArray;
	}

	public static MapProperties getProp() {
		return prop;
	}

	public static void setProp(MapProperties prop) {
		WorldLoader.prop = prop;
	}

	public static int getMapWidth() {
		return mapWidth;
	}

	public static void setMapWidth(int mapWidth) {
		WorldLoader.mapWidth = mapWidth;
	}

	public static int getMapHeight() {
		return mapHeight;
	}

	public static void setMapHeight(int mapHeight) {
		WorldLoader.mapHeight = mapHeight;
	}

	public static int getTilePixelWidth() {
		return tilePixelWidth;
	}

	public static void setTilePixelWidth(int tilePixelWidth) {
		WorldLoader.tilePixelWidth = tilePixelWidth;
	}

	public static int getTilePixelHeight() {
		return tilePixelHeight;
	}

	public static void setTilePixelHeight(int tilePixelHeight) {
		WorldLoader.tilePixelHeight = tilePixelHeight;
	}

	public static int getMapPixelWidth() {
		return mapPixelWidth;
	}

	public static void setMapPixelWidth(int mapPixelWidth) {
		WorldLoader.mapPixelWidth = mapPixelWidth;
	}

	public static int getMapPixelHeight() {
		return mapPixelHeight;
	}

	public static void setMapPixelHeight(int mapPixelHeight) {
		WorldLoader.mapPixelHeight = mapPixelHeight;
	}

	/**
	 * @return the cl
	 */
	public static MyContactListener getCl() {
		return cl;
	}

	/**
	 * @param cl the cl to set
	 */
	public static void setCl(MyContactListener cl) {
		WorldLoader.cl = cl;
	}
}
