package engine3D.math3D;

/* The Vector class implements a 3D vector
   with the (float) values x, y, and z.
  
   Vectors can be thought of as either an
   (x,y,z) point or a vector from (0,0,0) to (x,y,z) 
*/

public class Vector
{
	public float x, y, z;
	
	public Vector() 							  { setTo(  0,   0,   0); }

	public Vector(float x, float y, float z)	  { setTo(  x,   y,   z); }
	
	public Vector  (Vector v) 				      { setTo(v.x, v.y, v.z); } 
	
	public void setTo(Vector v) 				  { setTo(v.x, v.y, v.z); }
	
	public void setTo(float x, float y, float z)
	{
		this.x  = x;
		this.y  = y;
		this.z  = z;
	}
	
	//adding two vectors is achieved by adding their components together
	public void add(float x, float y, float z)
	{
		this.x += x;
		this.y += y; 	
		this.z += z;
	}
	
	public void subtract(float x, float y, float z){ add(  -x,  -y,  -z); }
	
	public void add     (Vector v) 			      	  { add( v.x, v.y, v.z); }
	
	public void subtract(Vector v) 			     	  { add(-v.x,-v.y,-v.z); }
	
	//multiplying a vector by a scalar is achieved by multiplying each of its components by the scaling factor
	public void multiply(float s  ) //note: s is the scaling factor
	{
		x *= s;
		y *= s;
		z *= s;
	}
	
	public void divide  (float s  )
	{
		x /= s;
		y /= s;
		z /= s;
	}
	
	/* a vector's magnitude(or length) can be calculated using
	 * the 3D version of Pythagorean Theorem:
	 *         |V| = (V_x^2 + V_y^2 + V_z^2)^(1/2) 			*/
	public float length()
	{
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	//we can normalize a vector by dividing it by its length
	public void normalize()
	{
		divide(length());
	}

	//performs and returns the dot product between this vector and the specified vector
	public float getDotProduct(Vector v)
	{
		Vector u = this;
		return (u.x * v.x) + (u.y * v.y) +(u.z * v.z);
	}

	public void setToCrossProduct(Vector u, Vector v)
	{
		//assign to local vars first in case u or v is this
		float x = (u.y * v.z) - (u.z * v.y);
		float y = (u.z * v.x) - (u.x * v.z);
		float z = (u.x * v.y) - (u.y * v.x);
		setTo(x, y, z);
	}
	
	//converts this vector3D to a string representation
	public String toString()
	{
		return "(" + x + ", " + y + ", " + z + ")";
	}
	 
	/* Checks if this Vector3D is equal to the specified object. They are
	 * equal if and only if the specified Object is a Vector3D and the
	 * two vector 3D's x, y, and z coordinates are the same. */
	public boolean equals(Object obj) 
	{
		Vector v = (Vector)obj;
		
		return (v.x == x && v.y == y && v.z == z);
	}
	
	//Checks if this vector3D is equal to the specified x, y, and z coordinates
	public boolean equals(float x, float y, float z)
	{
		return (this.x == x && this.y == y && this.z == z);
	}
	
	/* Rotates this vector around the specified axis the specified
	 * amount, using the (transform) pre-computed cosine and sine values of the angle */
	public void rotateX(float cosAngle, float sinAngle)
	{
		float zPrime = z*cosAngle + y*sinAngle;
		float yPrime = y*cosAngle - z*sinAngle;
		z = zPrime;
		y = yPrime;
	}
	public void rotateY(float cosAngle, float sinAngle)
	{
		float xPrime = x*cosAngle + z*sinAngle;
		float zPrime = z*cosAngle - x*sinAngle;
		x = xPrime;
		z = zPrime;
	}
	public void rotateZ(float cosAngle, float sinAngle)
	{
		float xPrime = x*cosAngle - y*sinAngle;
		float yPrime = y*cosAngle + x*sinAngle;
		x = xPrime;
		y = yPrime;
	}
	
	/* Adds the specified transform to this vector. This vector
	 * is first rotated, then translated. */
	public void add(Transform xform)
	{
		addRotation(xform);				//rotate
		add(xform.getLocation());   	//translate
	}
	
	//Subtracts the specified transform to this vector
	public void subtract(Transform xform)
	{
		subtract(xform.getLocation()); 	//translate
		subtractRotation(xform);		//rotate
	}
	
	//Rotates this vector with the angle of the specified transform.
	public void addRotation(Transform xform)
	{
		rotateX(xform.getCosAngleX(), xform.getSinAngleX());
		rotateZ(xform.getCosAngleZ(), xform.getSinAngleZ());
		rotateY(xform.getCosAngleY(), xform.getSinAngleY());
	}
	
	//Rotates this vector with the opposite angle of the specified transform.
	public void subtractRotation(Transform xform)
	{
		//note:  cos(-x) == cos(x)   ,   sin(-x) == -sin(x)
		rotateY(xform.getCosAngleY(), -xform.getSinAngleY());
		rotateZ(xform.getCosAngleZ(), -xform.getSinAngleZ());
		rotateX(xform.getCosAngleX(), -xform.getSinAngleX());
	}
	
}
