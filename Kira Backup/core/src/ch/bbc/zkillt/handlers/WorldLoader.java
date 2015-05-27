package ch.bbc.zkillt.handlers;

import java.util.ArrayList;

import ch.bbc.zkillt.entities.Coin;
import ch.bbc.zkillt.entities.Player;
import ch.bbc.zkillt.states.Play;

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
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class WorldLoader {

	public World world;
	public TiledMap tileMap;
	public float tileSize;
	public OrthogonalTiledMapRenderer tmr;	
	
	public ArrayList<Object> worldObjects;
	
	public WorldLoader(GameStateManager gsm) {
		world = new World(new Vector2(0, -9.81f), true);
	}

	public void createPlayer() {
		
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		
		// create player
		bdef.position.set(500 / B2DVars.PPM, 700 / B2DVars.PPM);
		bdef.type = BodyType.DynamicBody;
		Body body = world.createBody(bdef);

		shape.setAsBox(44 / B2DVars.PPM, 96 / B2DVars.PPM);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_COIN | B2DVars.BIT_SCHRAEG | B2DVars.BIT_WATER;
		body.createFixture(fdef).setUserData("player");
		
		// create foot sensor
		shape.setAsBox(13 / B2DVars.PPM, 2 / B2DVars.PPM, new Vector2(0, -97 / B2DVars.PPM), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("foot");
		
		// create foot sensor
		shape.setAsBox(25 / B2DVars.PPM, 25 / B2DVars.PPM, new Vector2(0, 0.5f), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
		fdef.filter.maskBits = B2DVars.BIT_GROUND;
		fdef.isSensor = true;
		body.createFixture(fdef).setUserData("head");
		
		// create player
		Play.setPlayer(new Player(body));
		body.setUserData(Play.getPlayer());
	}

	public void createTiles(String path, TiledMap tileMapVariable, String layername, boolean isSensor, short isBits, short collideBits) {
		
		// load tile map
		tileMap = new TmxMapLoader().load(path);
		tmr = new OrthogonalTiledMapRenderer(tileMapVariable);
		tileSize = tileMap.getProperties().get("tilewidth", Integer.class);
		TiledMapTileLayer layer;
		
		layer = (TiledMapTileLayer) tileMap.getLayers().get(layername);
		createLayer(layer, isSensor, isBits, collideBits);
	}
	
	public void createLayer(TiledMapTileLayer layer, boolean isSensor, short isBits, short collideBits) {
		
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
				fdef.filter.categoryBits = isBits;
				fdef.filter.maskBits = collideBits;
				fdef.isSensor = isSensor;
				world.createBody(bdef).createFixture(fdef);
			}
		}
	}
	
	public void createLayerSchraeg(TiledMapTileLayer layer, boolean isSensor, short isBits, short collideBits) {
		
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
				fdef.filter.categoryBits = isBits;
				fdef.filter.maskBits = collideBits;
				fdef.isSensor = isSensor;
				world.createBody(bdef).createFixture(fdef);		
			}
		}
	}
	
	public void createWorldObject(Object object, String objectName, boolean isSensor, short isBits, short collisionBits) {
		
		worldObjects = new ArrayList<Object>();
		
		MapLayer layer = tileMap.getLayers().get(objectName);
		
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
			fdef.isSensor = isSensor;
			fdef.filter.categoryBits = isBits;
			fdef.filter.maskBits = collisionBits;
			
			Body body = world.createBody(bdef);
			body.createFixture(fdef).setUserData(objectName);
			
			if(object instanceof Coin) {
			Coin c = new Coin(body);
			worldObjects.add(c);
			body.setUserData(c);
			}
			if (object instanceof ch.bbc.zkillt.entities.Water) {
			ch.bbc.zkillt.entities.Water w = new ch.bbc.zkillt.entities.Water(body);
			worldObjects.add(w);
			body.setUserData(w);
			}
			
		}
	}

}
