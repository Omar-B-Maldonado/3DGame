package engine3D;
/*
 * This class implements a z-buffer (or depth-bufffer) that records the depth
 * of every pixel in a 3D view window. The value recorded for each pixel is the
 * inverse of the depth (1/z) so there is higher precision for close objects
 * and lower precision for far objects (where high-depth visual precision is not as important)
 */
public class ZBuffer 
{
    private short[] depthBuffer;
    private int width, height;

    //Creates a new z-buffer with the specified width and height
    public ZBuffer(int width, int height)
    {
        depthBuffer = new short[width*height];
        this.width = width;
        this.height = height;
        clear();
    }
    
    //getters
    public int getWidth() { return width; }
    public int getHeight(){ return height;}

    /*
     * Sets the depth of the pixel at specified offset,
     * overwrriting its current depth 
     */
    public void setDepth(int offset, short depth)
    {
        depthBuffer[offset] = depth;
    }

    /*
     * Checks the depth at the specified offset. If the specified depth is closer 
     * (its inverse is greater than or equal to the current depth's inverse at that offset),
     * then the depth is set and the method returns true. Otherwise, nothing occurs and this
     * medthod returns false.
     */
    public boolean checkDepth(int offset, short depth)
    {
        if (depth >= depthBuffer[offset])
        {
            depthBuffer[offset] = depth;
            return true;
        }
        else return false;
    }

    //Clears the z-buffer, all depth values are set to 0
    public void clear()
    {
        for (short depthValue : depthBuffer) depthValue = 0;
    }
}
