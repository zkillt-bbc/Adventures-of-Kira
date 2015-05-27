package ch.bbc.zkillt.handlers;

import java.util.Iterator;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
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

public class CollisionWorking {

	private World world;
	private Array<Body> bodies = new Array<Body>();
	
	public CollisionWorking(World world) {
		this.world = world;
	}
	
	public void createPhysics(TiledMap map) {
		createPhysics(map, "turtles");
	}
	
	public void createPhysics(TiledMap map, String layername) {
		MapLayer layer = map.getLayers().get(layername);
		
		MapObjects objects = layer.getObjects();
		Iterator<MapObject> objectIterator = objects.iterator();
		
		while (objectIterator.hasNext()){
			MapObject object = objectIterator.next();
			
			if(object instanceof TextureMapObject) {
				continue;
			}
			
			Shape shape = new PolygonShape();
			BodyDef bdef = new BodyDef();
			bdef.type = BodyType.StaticBody;
			
			if (object instanceof RectangleMapObject) {
				RectangleMapObject rectangle = (RectangleMapObject)object;
				shape = getRectangle(rectangle);
			}
			else if (object instanceof PolygonMapObject) {
				shape = getPolygon((PolygonMapObject)object);
			}
			else if (object instanceof PolylineMapObject) {
				shape = getPolyLine((PolylineMapObject)object);
			}
			else if (object instanceof CircleMapObject) {
				shape = getCircle((CircleMapObject)object);
			}
			
			FixtureDef fdef = new FixtureDef();
			fdef.shape = shape;
			
			Body body = world.createBody(bdef);
			body.createFixture(fdef);
			
			bodies.add(body);
			
			fdef.shape = null;
			shape.dispose();
		}
	}
	
	public void destroyPhysics() {
		for (Body body : bodies) {
			world.destroyBody(body);
		}
		bodies.clear();		
	}
	
    public static Shape getRectangle(RectangleMapObject rectangleObject)
    {
        System.out.println("Loading rectangle from map file");

        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width) / 1000, (rectangle.y + rectangle.height ) / 1000);
        polygon.setAsBox(rectangle.width / 2 / B2DVars.PPM, rectangle.height / 2 / B2DVars.PPM, size, 0.0f);
        return polygon;
    }
	
	public static Shape getCircle(CircleMapObject circleObject) {
		Circle circle = circleObject.getCircle();
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(circle.radius * B2DVars.PPM);
		circleShape.setPosition(new Vector2(circle.x * B2DVars.PPM, circle.y * B2DVars.PPM));
		
		return circleShape;
	}
	
	public static Shape getPolygon(PolygonMapObject polygonObject) {
		PolygonShape polygon = new PolygonShape();
		float[] vertex = polygonObject.getPolygon().getTransformedVertices();
		float[] worldVertex = new float[vertex.length];
		
		for (int i = 0; i < vertex.length; i++) {
			worldVertex[i] = vertex[i] * B2DVars.PPM;
		}
		
		polygon.set(worldVertex);
		return polygon;
	}
	
	public static Shape getPolyLine(PolylineMapObject polyLineObject) {
		float[] vertex = polyLineObject.getPolyline().getTransformedVertices();
		Vector2[] worldVertex = new Vector2[vertex.length / 2];
		
		for (int i = 0; i < vertex.length / 2; i++) {
			worldVertex[i] = new Vector2();
			worldVertex[i].x = vertex[i * 2] * B2DVars.PPM;
			worldVertex[i].y = vertex[i * 2 + 1] * B2DVars.PPM;
		}
		ChainShape chain = new ChainShape();
		chain.createChain(worldVertex);
		return chain;
		
	}
	
	
	
}
