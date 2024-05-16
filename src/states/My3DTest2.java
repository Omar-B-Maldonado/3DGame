package states;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import engine3D.*;
import engine3D.math3D.*;
import engine3D.renderers.*;
import main.*;

public class My3DTest2 extends InputHandler implements GameState 
{
    ArrayList<Polygon> polygons;
    PolygonRenderer    renderer;
    ViewWindow         window;

    public void init() 
    {
        window           = new ViewWindow(0,0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, (float)Math.toRadians(75));
        Transform camera = new Transform(0, 100, 0);
        renderer         = new SolidPolygonRenderer(camera, window);
        polygons         = new ArrayList<>();
        createHouse(-200, 0,-1000);
        createHouse(-800, 50, -600);
    }

     //create a house (convex polyhedra)
     public void createHouse(int x, int y, int z) {
        Polygon poly;
     
        //walls
        poly = new Polygon(new Vector[]{    //front wall
            new Vector( x, y, z),
            new Vector(x + 400, y, z),
            new Vector(x + 400, y + 250, z),
            new Vector( x, y + 250, z)});
        poly.setColor(Color.CYAN);
        polygons.add(poly);

        poly = new Polygon(new Vector[]{    //back wall
            new Vector( x, y, z - 400),
            new Vector( x, y + 250, z - 400),
            new Vector(x + 400, y + 250, z - 400),
            new Vector(x + 400, y, z - 400)});
        poly.setColor(Color.GREEN);
        polygons.add(poly);

        poly = new Polygon(new Vector[]{    //left wall****
            new Vector(x, y, z - 400),
            new Vector(x, y, z),
            new Vector(x, y + 250, z),
            new Vector(x, y + 250, z - 400)});
        poly.setColor(Color.RED);
        polygons.add(poly);

        poly = new Polygon(new Vector[]{    //right wall
            new Vector(x + 400, y, z),
            new Vector(x + 400, y, z - 400),
            new Vector(x + 400, y + 250, z - 400),
            new Vector(x + 400, y + 250, z)});
        poly.setColor(Color.ORANGE);
        polygons.add(poly);

        //roof
        poly = new Polygon(new Vector[]{
            new Vector(x,  y + 250, z),
            new Vector(x + 400, y + 250, z),
            new Vector(x + 275, y + 400, z - 200),
            new Vector(x + 125, y + 400, z - 200)});
        poly.setColor(new Color(0x660000));
        polygons.add(poly);

        poly = new Polygon(new Vector[]{
            new Vector(x, y + 250, z - 400),
            new Vector(x, y + 250, z),
            new Vector(x + 125, y + 400, z - 200)});
        poly.setColor(new Color(0x330000));
        polygons.add(poly);

        poly = new Polygon(new Vector[]{
            new Vector(x + 400, y + 250, z - 400),
            new Vector(x, y + 250, z - 400),
            new Vector(x + 125, y + 400, z - 200),
            new Vector(x + 275, y + 400, z - 200)});
        poly.setColor(new Color(0x660000));
        polygons.add(poly);

        poly = new Polygon(new Vector[]{
            new Vector(x + 400, y + 250, z),
            new Vector(x + 400, y + 250, z - 400),
            new Vector(x + 275, y + 400, z - 200)});
        poly.setColor(new Color(0x330000));
        polygons.add(poly);
        
        //door and window
         poly = new Polygon(new Vector[]{   //door
            new Vector(x + 200, y, z),
            new Vector(x + 275, y, z),
            new Vector(x + 275, y + 125, z),
            new Vector(x + 200, y + 125, z)});
        poly.setColor(Color.DARK_GRAY);
        polygons.add(poly);

        poly = new Polygon(new Vector[]{    //window
            new Vector(x + 50, y + 150, z),
            new Vector(x + 100, y + 150, z),
            new Vector(x + 100, y + 200, z),
            new Vector(x + 50, y + 200, z)});
        poly.setColor(Color.BLUE);
        polygons.add(poly);
    }

    public void update() 
    {
        checkOptions();
        trackMouse();
        applyMovement();
    }

    public void render(Graphics pen) 
    {   
        renderer.startFrame(pen); //essentially just fills the viewWindow with black
        for (Polygon p : polygons) renderer.draw(pen, p);
        drawText(pen);
    }

    public void drawText(Graphics pen)
    {
        pen.setColor(Color.WHITE);
        pen.drawString("Use the mouse/WASD keys to move. " + "Press Esc to exit.", 5, 20);
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
