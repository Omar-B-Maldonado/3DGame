package states;

import java.awt.Graphics;
import java.util.ArrayList;

import engine3D.*;
import engine3D.math3D.*;
import engine3D.renderers.*;
import main.*;

public class TextureMapTest1 extends InputHandler implements GameState{

    ArrayList<Polygon> polygons;
    PolygonRenderer renderer;
    ViewWindow window;

    public void init()
    {
        window = new ViewWindow(0,0,Game.SCREEN_WIDTH,Game.SCREEN_HEIGHT, (float)Math.toRadians(75));
        Transform camera = new Transform(0,100,0);
        renderer = new SimpleTexturedPolygonRenderer(camera, window, "images/test_pattern.png");
        polygons = new ArrayList<>();

        Vector[] vertices = {
            new Vector(-128, 256, -1000),
            new Vector(-128, 0, -1000),
            new Vector(128, 0, -1000),
            new Vector(128, 256, -1000)
        };
        // one wall for now
        polygons.add(new Polygon(vertices));
    }

    public void update()
    {
        checkOptions();
        trackMouse();
        applyMovement();
    }

    public void render(Graphics pen) 
    {
        for (Polygon p : polygons) renderer.draw(pen, p);
    }

    public void setViewBounds(int width, int height)
    {
        width  = Math.min(width, Game.SCREEN_WIDTH);
        height = Math.min(height, Game.SCREEN_HEIGHT);
        width  = Math.max(64, width);
        height = Math.max(48, height);

        window.setWindowBounds((Game.SCREEN_WIDTH - width) / 2, (Game.SCREEN_HEIGHT - height) / 2, width, height);
    }

    public void checkOptions()
    {
        if (pressing[ESC])     System.exit(1);
        if (pressing[MINUS])   setViewBounds(window.getWidth() - 8, window.getHeight() - 5); //mac ratio is 16:10
        if (pressing[EQUALS])  setViewBounds(window.getWidth() + 8, window.getHeight() + 5);
    }

    public void applyMovement()
    {
        Transform camera = renderer.getCamera();
        Vector camVector = camera.getLocation();
        float sens = 0.0023f; //sensitivity
		float r = 4;            //moveDistance

        if (pressing[_W]) { //move forward
			camVector.x -= r * camera.getSinAngleY();
			camVector.z -= r * camera.getCosAngleY(); 
		}
		if (pressing[_S]) { //move backward
			camVector.x += r * camera.getSinAngleY();
			camVector.z += r * camera.getCosAngleY(); 
		}
		if (pressing[_A]){ //move left
			camVector.x -= r * camera.getCosAngleY();
			camVector.z += r * camera.getSinAngleY(); 
		}
		if (pressing[_D]){ //move right
			camVector.x += r * camera.getCosAngleY();
			camVector.z -= r * camera.getSinAngleY(); 
		}
		if (pressing[SPACE]) camVector.y += r; //fly up
		if (pressing[SHIFT]) camVector.y -= r; //fly down

        //rotating around the y-axis is like looking left or right
		if (lookingLeft)  camera.rotateAngleY( sens * mouseXSpeed); //look left
		if (lookingRight) camera.rotateAngleY(-sens * mouseXSpeed); //look right
		
		//rotating around the x-axis is like looking up or down (counter clockwise is positive)
		if (lookingUp)    camera.rotateAngleX( sens * mouseYSpeed); //look up
		if (lookingDown)  camera.rotateAngleX(-sens * mouseYSpeed); //look down
    }
    
}
