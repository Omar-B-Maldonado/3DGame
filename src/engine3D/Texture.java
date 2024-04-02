package engine3D;

/* This is an abstract class that represents a 16-bit color texture.
 * This class enables the caller to get the color at
 * a particular (x,y) location within the texture. */
public abstract class Texture 
{
    protected int width, height;

    public Texture(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public int getWidth () { return width;  }
    public int getHeight() { return height; }

    //Gets the 16-bit color of this texture at the specified (x,y) location
    public abstract short getColor(int x, int y);
}
