package engine3D.renderers;

import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import engine3D.*;
import engine3D.math3D.Transform;
import engine3D.math3D.Vector;

/* This class demonstrates the fundamentals of perspective-correct texture mapping.
 * It is very slow and maps the same texture for every polygon. */
public class SimpleTexturedPolygonRenderer extends PolygonRenderer
{
    protected Vector a = new Vector();
    protected Vector b = new Vector();
    protected Vector c = new Vector();
    protected Vector viewPos = new Vector();
    protected Rectangle3D textureBounds = new Rectangle3D();
    protected BufferedImage texture;

    public SimpleTexturedPolygonRenderer(Transform camera, ViewWindow window,
                                         String textureFile)
    {
        super(camera, window);
        texture = loadTexture(textureFile);
    }

    public BufferedImage loadTexture(String filePath)
    {
        try {return ImageIO.read(new File(filePath));}
        catch (IOException e) {e.printStackTrace(); return null;}
    }

    protected void drawCurrentPolygon(Graphics pen)
    {
        /* Calculate the texture bounds.
         * Ideally the textyre bounds are pre-calculated and stored with the polygon.
         * Coordinates are computed here for demonstration purposes
         */
        Vector textureO = textureBounds.getOrigin();
        Vector textureU = textureBounds.getDirectionU();
        Vector textureV = textureBounds.getDirectionV();

        textureO.setTo(sourcePolygon.getVertex(0));

        textureU.setTo(sourcePolygon.getVertex(3));
        textureU.subtract(textureO);
        textureU.normalize();

        textureV.setTo(sourcePolygon.getVertex(1));
        textureV.subtract(textureO);
        textureV.normalize();

        textureBounds.subtract(camera); //transform the texture bounds

        //start texture-mapping calculations
        a.setToCrossProduct(textureBounds.getDirectionV(), textureBounds.getOrigin());
        b.setToCrossProduct(textureBounds.getOrigin(),     textureBounds.getDirectionU());
        c.setToCrossProduct(textureBounds.getDirectionU(), textureBounds.getDirectionV());

        int y = scanConverter.getTop();
        viewPos.z = -window.getDistance();

        while (y <= scanConverter.getBottom())
        {
            ScanConverter.Scan scan = scanConverter.getScan(y);

            if (scan.isValid())
            {
                viewPos.y = window.convertScreenYToViewY(y);
                for (int x = scan.left; x <= scan.right; x++)
                {
                    viewPos.x = window.convertScreenXToViewX(x);

                    //compute the texture location
                    int tx = (int)(a.getDotProduct(viewPos) / c.getDotProduct(viewPos));
                    int ty = (int)(b.getDotProduct(viewPos) / c.getDotProduct(viewPos));

                    try //get the color to draw
                    {
                        int color = texture.getRGB(tx, ty);
                        pen.setColor(new Color(color));
                    }
                    catch (Exception e) { pen.setColor(Color.red); }

                    //draw the pixel
                    pen.drawLine(x,y,x,y);
                }
            }
            y++;
        }
    }
}
