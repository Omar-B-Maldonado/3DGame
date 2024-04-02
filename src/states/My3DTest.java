package states;

import java.awt.*;
import java.awt.geom.GeneralPath;

import engine3D.*;
import engine3D.Polygon;
import engine3D.math3D.*;
import main.*;

public class My3DTest extends InputHandler implements GameState
{
	private ViewWindow window; //the viewWindow will fill our whole screen

	private Transform camera;
	
	// create solid-colored polygons
    private Polygon sign, post;
    private Polygon leaves, trunk;
  
    //make a transform to displace
    private Transform stopSignTransform;
    private Transform treeTransform;
    
    //make a scratch polygon to apply transforms to
    private Polygon scratchPolygon;
	
	public void init() 
	{	
		// make the view window the entire window with AOV of 90
        window = new ViewWindow(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, (float)Math.toRadians(90));
        camera = new Transform();
		//renderer = new PolygonRenderer(camera, window);
     
        stopSignTransform  = new Transform(-10,-30,-500); //this transform vector will move the sign back by 500
        treeTransform 	   = new Transform(  0,  0,-650);
        scratchPolygon     = new Polygon(); //the transforms will be applied to this polygon
    
		//define arrays of vertices for our polygons
		Vector[]
		signVertices   = {
    		new Vector(-18,  30, 0),
    		new Vector(-36,  50, 0),
    		new Vector(-36,  80, 0),
    		new Vector(-18, 100, 0),
    		
    		new Vector(18, 100, 0),
    		new Vector(36,  80, 0),
    		new Vector(36,  50, 0),
	    	new Vector(18,  30, 0)	
    	},
    	postVertices   = {
    		new Vector(-5,  0, 0),
	        new Vector( 5,  0, 0),
	        new Vector( 5, 30, 0),
	        new Vector(-5, 30, 0)
    	},
    	leavesVertices = {
    		new Vector(-50, -35, 0),
    	    new Vector( 50, -35, 0),
    	    new Vector(  0, 150, 0)
    	},
		trunkVertices  = {
			new Vector(-5, -50, 0),
    	    new Vector( 5, -50, 0),
    	    new Vector( 5, -35, 0),
    	    new Vector(-5, -35, 0)
		};
		//define the colors for our polygons
		Color stopSignRed = new Color(0xcf142b);
		Color leafGreen   = new Color(0x006400);
		Color barkBrown   = new Color(0x714311);
		
    	//create the polygons with their respective list of vertices
    	sign   = new Polygon(  signVertices, stopSignRed);
    	post   = new Polygon(  postVertices,  Color.GRAY);
    	leaves = new Polygon(leavesVertices,   leafGreen);
    	trunk  = new Polygon( trunkVertices,   barkBrown);
		sign.setOpacityPercentage(80);
	}
	
	@Override
	public void update() 
	{
		checkOptions();
		trackMouse();
		applyMovement();
		
		treeTransform.rotateAngleY(-0.02f);
		stopSignTransform.rotateAngleY(0.015f); //sets the sinAngleY and cosAngleY of the transform to the old angle plus this angle (0 + 0.15)
	}

	@Override
	public void render(Graphics pen) 
	{
		// erase background
        pen.setColor(Color.DARK_GRAY);
        pen.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // draw message
        pen.setColor(Color.white);
        pen.drawString("Press WASD to move. Press Shift to descend. Press Space to ascend. Press Esc to exit.", 5, 20);
		
        //draw the world polygons
        drawPolygon(leaves, treeTransform, pen);
        drawPolygon(trunk,  treeTransform, pen);
        
        drawPolygon(sign, stopSignTransform, pen);
        drawPolygon(post, stopSignTransform, pen);
		//renderer.draw(pen, leaves);
	}
	
	private void drawPolygon(Polygon p, Transform t, Graphics pen)
	{
		drawPolygon(p, t, pen, true);
	}
	
	private void drawPolygon(Polygon p, Transform t, Graphics pen, boolean wireframe)
	{
		/* SIMPLE 3D PIPELINE (for drawing a polygon):
		 * 	1) apply the transform
		 * 	2) project it onto the view window (and convert its view coordinates to screen coordinates)
		 * 	3) draw 																					*/
		//1) apply the transform
		scratchPolygon.setTo(p); //gives scratchedPolygon the vertices and color of the specified polygon
	    scratchPolygon.add(t); //rotates and then translates scratchPolygon to match the treeTransform
	    
	    scratchPolygon.subtract(camera); //account for the camera's location by moving the polygon
		
		/* CLIPPING START BRACKET */ if (scratchPolygon.clip(-1)) {
	    
		//2) project the transformed polygon
	    scratchPolygon.project(window);//projects all of scratchPolygon's vertices to the viewWindow and converts the viewWindow coords to screen coords
	    
		//3) convert the polygon to a Java2D GeneralPath and draw it
	    GeneralPath path = new GeneralPath();
	    
	    Vector v = scratchPolygon.getVertex(0);
	    
	    path.moveTo(v.x, v.y); //moves the path to the first point
	    
	    for (int i = 1; i < scratchPolygon.getNumVertices(); i++) 
	    {
	        v = scratchPolygon.getVertex(i);
	        path.lineTo(v.x, v.y); //moves the path to the next point   
	    }
	    pen.setColor(p.getColor());
	    ((Graphics2D) pen).fill(path); //fills the path in
	    
	    if (wireframe)
		{
	    	path.closePath();
	    	pen.setColor(Color.BLACK);
	    	((Graphics2D) pen).draw(path);
	    }
		}/* CLIPPING END BRACKET */
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
        if (pressing[MINUS])   setViewBounds(window.getWidth() - 8, window.getHeight() - 5);
        if (pressing[EQUALS])  setViewBounds(window.getWidth() + 8, window.getHeight() + 5);
    }

	public void applyMovement()
    {
        Vector camVector = camera.getLocation();
		int r = 3; //moveDistance

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

		float sens = 0.0023f; //sensitivity
		/*
		//rotating around the z-axis is like tilting your head left or right
		if (pressing[_Q]) camera.rotateAngleZ( 0.02f); //tilt left
		if (pressing[_E]) camera.rotateAngleZ(-0.02f); //tilt right
		*/
		//Note: camera looking up/down and tilting needs to be limited

		//rotating around the y-axis is like looking left or right
		if (lookingLeft)  camera.rotateAngleY( sens * mouseXSpeed); //look left
		if (lookingRight) camera.rotateAngleY(-sens * mouseXSpeed); //look right
		
		//rotating around the x-axis is like looking up or down (counter clockwise is positive)
		if (lookingUp)    camera.rotateAngleX( sens * mouseYSpeed); //look up
		if (lookingDown)  camera.rotateAngleX(-sens * mouseYSpeed); //look down
    }
}
