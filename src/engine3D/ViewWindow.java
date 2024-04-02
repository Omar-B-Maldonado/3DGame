package engine3D;
import java.awt.Rectangle;

import engine3D.math3D.Vector;

/*  The ViewWindow can be represented as an axis-aligned rectangle, where:
 *		x and y represent the coordinates of its top left corner
 *		w and h represent its width and height 							   */
public class ViewWindow 
{
	private Rectangle window; //the view window can be represented by a rectangle
	private float 	   angle; //viewing angle (or AOV - Angle Of View)
	private float 	       d; //distance between the viewWindow and camera
	
	public ViewWindow(int left, int top, int width, int height, float angle)
	{
		this.angle = angle;
		
		window = new Rectangle();
		setWindowBounds(left, top, width, height);
	}
	
	//SETTERS
	public void setWindowBounds(int left, int top, int width, int height)
	{
		window.x 	     = left	 ;
		window.y         = top	 ;
		window.width     = width ;
		window.height	 = height;
		
		d = (window.width/2) / (float)(Math.tan(angle/2));
	}
	public void setAngle(float angle)
	{
		this.angle = angle;
		d = (window.width/2) / (float)(Math.tan(angle/2));
	}
	
	//GETTERS
	public float getDistance() { return d; 		   	  }
	public float getAngle   () { return angle;        }	
	public int   getY  		() { return window.y;     }	
	public int   getX		() { return window.x;     }	
	public int   getWidth   () { return window.width; }	
	public int   getHeight  () { return window.height;}		
	
	/* this method is called to project a vector from 3D space onto the 2D viewWindow
	 * (the viewWindow coordinate is then converted to a screen coordinate) */
	public void project(Vector v)
	{
		/* 1) convert from 3D to viewPort
		 *   (below are the formulas for projecting onto the view window from 3D space) */
		v.x = d * v.x / -v.z;
		v.y = d * v.y / -v.z;
		
		/* 2) convert to screen coordinates */
		v.x = convertViewXToScreenX(v.x);
		v.y = convertViewYToScreenY(v.y);
	}
	
	//VIEW TO SCREEN CONVERSION METHODS
	public float convertViewXToScreenX(float x)
	{
		return  x + window.x + (window.width /2);
	}
	public float convertViewYToScreenY(float y)
	{
		return -y + window.y + (window.height/2);
	}
	
	//SCREEN TO VIEW CONVERSION METHODS
	public float convertScreenXToViewX(float x)
	{
		return  x - window.x - (window.width /2);
	}
	public float convertScreenYToViewY(float y)
	{
		return -y + window.y + (window.height/2);
	}	
}
