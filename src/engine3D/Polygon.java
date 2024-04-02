package engine3D;
import java.awt.Color;

import engine3D.math3D.Transform;
import engine3D.math3D.Vector;


/* This class represents a polygon as 
 * a series of vertices (or vectors) with a color and a normal vector */
public class Polygon 
{
	private Vector[] vertices;
	private int   numVertices;
	private Vector     normal;
	private Color       color;

	//temporary vectors used for normal calculation
    private static Vector temp1 = new Vector();
    private static Vector temp2 = new Vector();
	 
	/* Creates an empty polygon that can be used as a "scratch" polygon for transforms, projections, etc.
	 * (A scratch polygon is convenient in case you want to copy a polygon and modify it while preserving the original.) */
	public Polygon()
	{
		this(new Vector[0]); //a polygon with no vertices
	}
	
	//Creates a new Polygon with the specified vertices (all vertices are assumed to be in the same plane.
	public Polygon(Vector[] vertices)
	{
		this(vertices, new Color(128, 128, 128, 80)); //gray with 80% opacity by default
	}

	//Creates a new Polygon with the specified vertices and color (all vertices are assumed to be in the same plane.
	public Polygon(Vector[] vertices, Color color)
	{
		this.vertices = vertices;
		numVertices   = vertices.length;
		normal        = new Vector();
		if (numVertices >=3) calcNormal();
		setColor(color);
	}
	
	//sets the color of this polygon
	public void  setColor(Color color) { this.color = color; }

	//sets the opacity of this polygon's color
	public void setOpacityPercentage(double percentage)
	{
		if (percentage > 100.0 || percentage < 0.0) 
		{
			System.out.println("The opacity percentage must be between 0 and 100");
		}
		this.color = new Color(color.getRed(),
							   color.getGreen(),
							   color.getBlue(),
								(int)Math.round((percentage/100) * 255)
		);
	}
	
	//Sets this polygon to have the same vertices as the specified polygon
	public void setTo(Polygon polygon)
	{
		numVertices = polygon.numVertices;
		normal.setTo (polygon.normal);
		ensureCapacity(numVertices);
		setColor(polygon.color);
		
		//make each vertex in this polygon equal to those of the specified polygon
		for (int i = 0; i < numVertices; i++)
		{
			vertices[i].setTo(polygon.vertices[i]);
		}
	}
	
	//Ensures this polygon has enough capacity to hold the specified number of vertices.
	protected void ensureCapacity(int length)
	{
		if (vertices.length < length)
		{
			Vector[] newVertices = new Vector[length];
			
			System.arraycopy(vertices, 0, newVertices, 0, vertices.length);
			
			for (int i = vertices.length; i < newVertices.length; i++)
			{
				newVertices[i] = new Vector();
			}
			vertices = newVertices;
		}
	}

	/* Clips this polygon so all vertices are in front of the clip plane
 	 * In other words, all vertices have (z <= clipZ).
	 * The value of clipZ shouldn't be zero (to avoid divide-by-zero problems).
	 * Returns true if the polygon is at least partially in front of the clip plane. */
	public boolean clip(float clipZ)
	{
		ensureCapacity(numVertices * 3);
		
		boolean isFullyBehindClipPlane = true; //isHidden

		/* insert vertices so all edges are either
		 * in front of or behind the clip plane */
		for (int i = 0; i < numVertices; i++)
		{
			int next = (i + 1) % numVertices; //if i is last vertex's i, next = 0
			
			Vector v1 = vertices[i];
			Vector v2 = vertices[next];
			
			if (v1.z < clipZ) isFullyBehindClipPlane = false;

			//ensure v1.z < v2.z via a swap
			if (v1.z > v2.z)
			{
				Vector temp = v1;
				v1 = v2;
				v2 = temp;
			}
			
			//if after swap, v1 is in front && v2 is behind:
			if (v1.z < clipZ && v2.z > clipZ)
			{
				float scale = (clipZ - v1.z) / (v2.z - v1.z);
				insertVertex(next,							//index
							 v1.x + scale * (v2.x - v1.x),  //x
							 v1.y + scale * (v2.y - v1.y),  //y
							 clipZ);						//z
				i++; //skip the vertex we just created
			}	
		} //end of for loop
		
		if (isFullyBehindClipPlane) return false;

		//delete all vertices that have z > clipZ
		for (int i = numVertices - 1; i >= 0; i--)
		{
			if (vertices[i].z > clipZ) deleteVertex(i); 
		}
		return (numVertices >= 3);
	}

	/* Inserts a new vertex at the specified index */
	protected void insertVertex(int index, float x, float y, float z)
	{//note: this is used in clipping, so there 3x the amount of array space
		Vector newVertex = vertices[vertices.length - 1]; //last vertex
		newVertex.setTo(x, y, z);

		//shift vertices to the right in the array to make space at index
		for (int i = vertices.length - 1; i > index; i--)
		{
			vertices[i] = vertices[i - 1];
		}
		vertices[index] = newVertex;
		numVertices++;
	}

	/* Deletes the vertex at the specified index */
	protected void deleteVertex(int index)
	{
		Vector deleted = vertices[index]; //vertex to be deleted
		
		//shift vertices to the left in the array, on top of index
		for(int i = index; i < vertices.length - 1; i++)
		{
			vertices[i] = vertices[i + 1]; 
		}
		vertices[vertices.length - 1] = deleted;
		numVertices --;
	}
	
	public void project(ViewWindow window)
	{
		for (Vector v : vertices) window.project(v);
	}

	public void setNormal(Vector n)
	{
		if (normal == null) normal = new Vector(n);
		else normal.setTo(n);
	}

	/* Calcualtes the unit-vector normal of this polygon.
	 * This method uses the first, second, and third vertices
	 * to calc the normal, so if these vertices are collinear (lie in the same line)
	 * then this method will not work.
	 * use setNormal() to explicitly set the normal.
	 * This method uses static objects in the Polygon class, so this method is
	 * not thread-safe across all instances of Polygon */
	public Vector calcNormal()
	{
		if (normal == null) normal = new Vector();
		//subtract one vertex from another to get an edge of the polygon
		
		//first edge
		temp1.setTo   (vertices[2]);
		temp1.subtract(vertices[1]);
		
		//second edge
		temp2.setTo   (vertices[0]);
		temp2.subtract(vertices[1]);

		//cross product is perpendicular to temp1 and temp2
		normal.setToCrossProduct(temp1, temp2);
		normal.normalize();
		return normal;
	}

	/* Tests if this polygon is facing the specified location.
	 * This method uses static objects in the Polygon class for
	 * calculations, so this method is not thread-safe across all
	 * instances of Polygon.
	 * If the angle between the specified location and the
	 * normal vector is <= 90, then the dot product between the
	 * two vectors will be >= 0.
	 * We can pass the camera location to check if a polygon is facing the camera. */
	public boolean isFacing(Vector u)
	{
		//get the direction between u and the first polygon vertex
		temp1.setTo(u);
		temp1.subtract(vertices[0]);

		//check the dot product of the two vectors
		return (normal.getDotProduct(temp1) >= 0);
	}
	
	//GETTERS
	public Color getColor()			  {  return color; }

 	public Vector getNormal()		  { return normal; }
	
	public int getNumVertices()		  { return numVertices; }

	public Vector getVertex(int index){ return vertices[index]; }
	
	/* 							TRANSFORM METHODS							*/
	//For adding and subtracting transformation vectors to each polygon vertex
	public void add(Vector u)
	{
		for (Vector v : vertices) v.add(u);
	}
		
	public void subtract(Vector u)
	{
		for (Vector v : vertices) v.subtract(u);
	}
	
	public void add(Transform xform)
	{
		addRotation(xform);				//rotate
		add(xform.getLocation());   	//translate
	}
	
	public void subtract(Transform xform)
	{
		subtract(xform.getLocation()); 	//translate
		subtractRotation(xform);		//rotate
	}
	
	public void addRotation(Transform xform)
	{
		for (Vector v : vertices) v.addRotation(xform);
		normal.addRotation(xform);
	}
	public void subtractRotation(Transform xform)
	{
		for (Vector v : vertices) v.subtractRotation(xform);
		normal.subtractRotation(xform);
	}
}
