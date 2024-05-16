package engine3D.renderers;

import engine3D.Texture;

public class PowerOf2Texture extends Texture
{
    private short[] buffer;
    private int  widthBits,  widthMask,
                heightBits, heightMask;
    
    /* This creates a new PowerOf2Texture with the specified buffer.
     * The width of the bitmap is 2 to the power of widthBits, or 1<<widthBits.
     * Likewise, the height of the bitmap is 2**heightBits, or 1<<heightBits.*/
    public PowerOf2Texture(short[] buffer, int widthBits, int heightBits)
    {
        super(1 << widthBits, 1 << heightBits);
        this.buffer     = buffer;
        this.widthBits  = widthBits;
        this.heightBits = heightBits;
        this.widthMask  = getWidth () - 1;
        this.heightMask = getHeight() - 1;
    }

    public short getColor(int x, int y)
    {
        return buffer[(x & widthMask) + ((y & heightMask) << widthBits)];
    }
    /*
     * This class keeps track of a short array to hold the texture and also the width and height mask.
     * The getColor() method uses the masks to get the correct x and y values
     * so you can tile the texture across the polygon.
     */
}
