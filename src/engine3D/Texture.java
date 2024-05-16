package engine3D;

import java.awt.image.*;
import java.awt.Graphics;

import engine3D.math3D.MoreMath;
import engine3D.renderers.PowerOf2Texture;

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

    /* This function assumes that the 16-bit display has the same pixel format
     * as the BufferedImage type TYPE_USHORT_565_RGB. Theoretically,
     * a display that doesn't have this pixel format could exist.
     * Alternatively, you could use the createCompatibleImage() method from
     * GraphicsConfiguration to create an image compatible with the display.
     */
    public static Texture createTexture(BufferedImage image)
    {
        int type   = image.getType();
        int width  = image.getWidth();
        int height = image.getHeight();

        if (!MoreMath.isPowerOfTwo(width) || !MoreMath.isPowerOfTwo(height))
        {
            throw new IllegalArgumentException
            (
                "Size of texture must be a power of 2."
            );
        }
        //convert the image to a 16-bit image
        if (type != BufferedImage.TYPE_USHORT_565_RGB)
        {
            BufferedImage newImage = new BufferedImage
            (
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_USHORT_565_RGB
            );
            Graphics pen = newImage.createGraphics();
            pen.drawImage(image, 0, 0, null);
            pen.dispose();
            image = newImage;
        }
        
        DataBuffer dest = image.getRaster().getDataBuffer();
        return new PowerOf2Texture
        (
            ((DataBufferUShort)dest).getData(),
            MoreMath.countbits(width - 1), MoreMath.countbits(height - 1)
        );
    }

    public int getWidth () { return width;  }
    public int getHeight() { return height; }

    //Gets the 16-bit color of this texture at the specified (x,y) location
    public abstract short getColor(int x, int y);
}