package engine3D;

import engine3D.math3D.Transform;
import engine3D.math3D.Vector;
//Simply put, this class keeps track of three vectors: O, U, and V
/* 
 * A Rectangle 3D is a rectangle in 3D space, defined as
 * an origin and vectors pointing in the directions of
 * the base (width) and side (height) */
public class Rectangle3D 
{
    private Vector origin;
    private Vector directionU;
    private Vector directionV;
    private Vector normal;
    private float  width;
    private float  height;

    //creates a rectangle at the origin with a width and height of zero
    public Rectangle3D() 
    {
        origin     = new Vector();
        directionU = new Vector(1,0,0);
        directionV = new Vector(0,1,0);
        width      = 0;
        height     = 0;
    }
    
    public Rectangle3D(Vector O, Vector U, Vector V, float w, float h)
    {
        origin     = O;
        directionU = U;
        directionV = V;
        width  = w;
        height = h;
    }

    //Setters
    public void setTo (Rectangle3D rect)
    {
        origin.setTo(rect.origin);
        directionU.setTo(rect.directionU);
        directionV.setTo(rect.directionV);
        width  = rect.width;
        height = rect.height;
    }
    public void setWidth(float width) 
    {
        this.width = width;
    }
    public void setHeight(float height)
    {
        this.height = height;
    }
    public void setNormal(Vector N)
    {
        if (normal == null) normal = new Vector(N);
        else normal.setTo(N);
    }

    //Getters
    public Vector getOrigin()     { return origin;     }
    public Vector getDirectionU() { return directionU; }
    public Vector getDirectionV() { return directionV; }
    public float  getWidth()      { return width;      }
    public float  getHeight()     { return height;     }

    protected Vector calcNormal()
    {
        if (normal == null) normal = new Vector();

        normal.setToCrossProduct(directionU, directionV);
        normal.normalize();
        return normal;
    }

    public Vector getNormal()
    {
        if (normal == null) calcNormal();
        return normal;
    }

    public void add(Vector M) //the choice of letter M is arbitrary
    {
        origin.add(M); //don't translate direction vectors or size
    }
    public void subtract(Vector M)
    {
        origin.subtract(M); //don't translate direction vectors or size
    }

    public void add(Transform xform)
    {
        addRotation(xform);
        add(xform.getLocation());
    }
    public void subtract(Transform xform)
    {
        subtract(xform.getLocation());
        subtractRotation(xform);
    }

    public void addRotation(Transform xform)
    {
        origin.addRotation(xform);
        directionU.addRotation(xform);
        directionV.addRotation(xform);
    }

    public void subtractRotation(Transform xform)
    {
        origin.subtractRotation(xform);
        directionU.subtractRotation(xform);
        directionV.subtractRotation(xform);
    }
}
