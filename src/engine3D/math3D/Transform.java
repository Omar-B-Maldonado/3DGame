
package engine3D.math3D;

/* This class represents a rotation and translation. */

/*Translation (moving a polygon around in space) is achieved by adding the translation vector to each vertex in the polygon
 *
 *Rotation involves rotating a polygon around the x-axis, y-axis, z-axis, or some combination of the three axes.
 *A complete rotation transform involves 3 rotations (one for each axis). 
 *When transforming a polygon via rotation, we rotate each point in the polygon. */

//Note: This class is also used to pre-compute and encapsulate the sin and cosine values of the angle we wish to rotate around an axis
public class Transform 
{
	protected Vector location;
	
	private float sinAngleX, sinAngleY, sinAngleZ;
	private float cosAngleX, cosAngleY, cosAngleZ;
	
	//Creates a new Transform with no translation or rotation
	public Transform()
	{
		this(0,0,0);
	}
	
	//Creates a new Transform with the specified translation and no rotation
	public Transform(float x, float y, float z)
	{
		location = new Vector(x, y, z);
		setAngle(0,0,0);
	}
	
	//Creates a new Transform
	public Transform(Transform v)
	{
		location = new Vector();
		setTo(v);
	}
	
	public Object clone()
	{
		return new Transform(this);
	}
	
	//Sets this transform to the specified Transform
	public void setTo(Transform v)
	{
		location.setTo(v.location);
		
		this.cosAngleX = v.cosAngleX;
		this.cosAngleY = v.cosAngleY;
		this.cosAngleZ = v.cosAngleZ;
		
		this.sinAngleX = v.sinAngleX;
		this.sinAngleY = v.sinAngleY;
		this.sinAngleZ = v.sinAngleZ;
	}
	
	public Vector getLocation ()  { return location;  }
	
	public float getCosAngleX()  { return cosAngleX; }
	public float getCosAngleY()  { return cosAngleY; }
	public float getCosAngleZ()  { return cosAngleZ; }
	
	public float getSinAngleX()  { return sinAngleX; }
	public float getSinAngleY()  { return sinAngleY; }
	public float getSinAngleZ()  { return sinAngleZ; }
	
	//Note: in the Math.atan2 function, the first argument is the y-coordinate and the 2nd is the x-coordinate
	public float getAngleX() { return (float)Math.atan2(sinAngleX, cosAngleX); }
	public float getAngleY() { return (float)Math.atan2(sinAngleY, cosAngleY); }
	public float getAngleZ() { return (float)Math.atan2(sinAngleZ, cosAngleZ); }
	
	public void setAngleX(float angleX)
	{
		sinAngleX = (float)Math.sin(angleX);
		cosAngleX = (float)Math.cos(angleX);
	}
	public void setAngleY(float angleY)
	{
		sinAngleY = (float)Math.sin(angleY);
		cosAngleY = (float)Math.cos(angleY);
	}
	public void setAngleZ(float angleZ)
	{
		sinAngleZ = (float)Math.sin(angleZ);
		cosAngleZ = (float)Math.cos(angleZ);
	}
	public void setAngle(float angleX, float angleY, float angleZ)
	{
		setAngleX(angleX);
		setAngleY(angleY);
		setAngleZ(angleZ);
	}
	
	public void rotateAngleX(float angle) 
	{
		if (angle != 0) setAngleX(getAngleX() + angle);
	}
	public void rotateAngleY(float angle) 
	{
		if (angle != 0) setAngleY(getAngleY() + angle);
	}
	public void rotateAngleZ(float angle) 
	{
		if (angle != 0) setAngleZ(getAngleZ() + angle);
	}
	public void rotateAngle(float angleX, float angleY, float angleZ)
	{
		rotateAngleX(angleX);
		rotateAngleY(angleY);
		rotateAngleZ(angleZ);
	}	
}
