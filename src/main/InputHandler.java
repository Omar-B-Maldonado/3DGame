package main;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class InputHandler extends JFrame implements KeyListener, MouseMotionListener 
{	
	public static boolean[] pressing = new boolean[1024];
	public static boolean[] typed    = new boolean[1024];
	
	public static final int
	UP          = KeyEvent.VK_UP,
	DN          = KeyEvent.VK_DOWN,
	LT          = KeyEvent.VK_LEFT,
	RT          = KeyEvent.VK_RIGHT,
	
	_A          = KeyEvent.VK_A,
	_B          = KeyEvent.VK_B,
	_C          = KeyEvent.VK_C,
	_D          = KeyEvent.VK_D,
	_E          = KeyEvent.VK_E,
	_F          = KeyEvent.VK_F,
	_G          = KeyEvent.VK_G,
	_H          = KeyEvent.VK_H,
	_I          = KeyEvent.VK_I,
	_J          = KeyEvent.VK_J,
	_K          = KeyEvent.VK_K,
	_L          = KeyEvent.VK_L,
	_M          = KeyEvent.VK_M,
	_N          = KeyEvent.VK_N,
	_O          = KeyEvent.VK_O,
	_P          = KeyEvent.VK_P,
	_Q          = KeyEvent.VK_Q,
	_R          = KeyEvent.VK_R,
	_S          = KeyEvent.VK_S,
	_T          = KeyEvent.VK_T,
	_U          = KeyEvent.VK_U,
	_V          = KeyEvent.VK_V,
	_W          = KeyEvent.VK_W,
	_X          = KeyEvent.VK_X,
	_Y          = KeyEvent.VK_Y,
	_Z          = KeyEvent.VK_Z,

	_1          = KeyEvent.VK_1,
	_2          = KeyEvent.VK_2,
	_3          = KeyEvent.VK_3,
	_4          = KeyEvent.VK_4,
	_5          = KeyEvent.VK_5,
	_6          = KeyEvent.VK_6,
	_7          = KeyEvent.VK_7,
	_8          = KeyEvent.VK_8,
	_9          = KeyEvent.VK_9,
	_0          = KeyEvent.VK_0,
	
	CTRL        = KeyEvent.VK_CONTROL,
	SHIFT       = KeyEvent.VK_SHIFT,
	ALT         = KeyEvent.VK_ALT,
	
	SPACE       = KeyEvent.VK_SPACE,
	
	COMMA       = KeyEvent.VK_COMMA,
	PERIOD      = KeyEvent.VK_PERIOD,
	SLASH       = KeyEvent.VK_SLASH,
	SEMICOLON   = KeyEvent.VK_SEMICOLON,
	COLON       = KeyEvent.VK_COLON,
	QUOTE       = KeyEvent.VK_QUOTE,
	MINUS		= KeyEvent.VK_MINUS,
	EQUALS		= KeyEvent.VK_EQUALS,
	
	F1          = KeyEvent.VK_F1,
	F2          = KeyEvent.VK_F2,
	F3          = KeyEvent.VK_F3,
	F4          = KeyEvent.VK_F4,
	F5          = KeyEvent.VK_F5,
	F6          = KeyEvent.VK_F6,
	F7          = KeyEvent.VK_F7,
	F8          = KeyEvent.VK_F8,
	F9          = KeyEvent.VK_F9,
	F10         = KeyEvent.VK_F10,
	F11         = KeyEvent.VK_F11,
	F12         = KeyEvent.VK_F12,
	
	ESC			= KeyEvent.VK_ESCAPE,
	ENTER		= KeyEvent.VK_ENTER;

	public void keyPressed (KeyEvent e)
	{
		pressing[e.getKeyCode()] = true;
	}
	public void keyReleased(KeyEvent e)
	{
		pressing[e.getKeyCode()] = false;
	}
	public void keyTyped   (KeyEvent e)
	{
		char keyChar = Character.toLowerCase(e.getKeyChar());
		
		if (keyChar == '1') Game.stateManager.pushState(Game.test1);
		if (keyChar == '2') Game.stateManager.pushState(Game.test2);
		if (keyChar == 'p') Game.stateManager.popState();
	}

	//-------------------- MOUSE SECTION BELOW --------------------------//
	
	static Point mousePoint = MouseInfo.getPointerInfo().getLocation();

	public static int 
	mouseX = (int) mousePoint.getX(),
	mouseY = (int) mousePoint.getY(),

	lastMouseX = mouseX,
	lastMouseY = mouseY,

	mouseXSpeed,
	mouseYSpeed;

	Robot bot;
	int paddingX = 20;
	int paddingY = 20;
	int rightBound  = Game.SCREEN_WIDTH  - paddingX;
	int bottomBound = Game.SCREEN_HEIGHT - paddingY;

	public static boolean  lookingRight = false,
			               lookingLeft  = false,
						   lookingUp    = false,
						   lookingDown  = false;
	
	public void mouseDragged(MouseEvent e) 
	{
		mouseX = e.getX();
    	mouseY = e.getY();

		loopMouseIfNeeded();
	}
	public void mouseMoved(MouseEvent e) 
	{
		mouseX = e.getX();
    	mouseY = e.getY();

		loopMouseIfNeeded();
	}
	public void loopMouseIfNeeded()
	{	
		if (mouseX <= paddingX)   bot.mouseMove(rightBound - 1, mouseY);
		else 
		if (mouseX >= rightBound) bot.mouseMove(paddingX+1, mouseY);
		
	  	if (mouseY <= paddingY)   bot.mouseMove(mouseX, bottomBound - 1);
		else 
		if (mouseY >= bottomBound)bot.mouseMove(mouseX, paddingY + 1);
	}

	public void trackMouse()
	{
		int deltaX = mouseX - lastMouseX;
    	int deltaY = mouseY - lastMouseY;

		if (Math.abs(deltaX) > (paddingX + rightBound) / 2)
		{
			if (deltaX > 0) deltaX -= rightBound - paddingX;
			else 			deltaX += rightBound - paddingX;
		}
		if (Math.abs(deltaY) > (paddingY + bottomBound) / 2)
		{
			if (deltaY > 0) deltaY -= bottomBound - paddingY;
			else 			deltaY += bottomBound - paddingY;
		}

		mouseXSpeed  = Math.abs(deltaX);
		mouseYSpeed  = Math.abs(deltaY);

        lookingRight = deltaX > 0;
        lookingLeft  = deltaX < 0;
        lookingDown  = deltaY > 0;
        lookingUp    = deltaY < 0;

		lastMouseX   = mouseX;
		lastMouseY   = mouseY;
	}

	public void setCursorInvisible()
	{
		BufferedImage cursor = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0,0), "blank");
		setCursor(blank);
	}
	
	//from: https://stackoverflow.com/questions/24476496/drag-and-resize-undecorated-jframe
	
	public void allowDragging(Component component)
	{
		component.addMouseListener(new MouseAdapter() 
		{
			public void mousePressed(MouseEvent e) 		//when you click
		    {
				mouseX = e.getX();						//gets x and y of click
				mouseY = e.getY();
		    }
		});
			
		component.addMouseMotionListener(new MouseAdapter()
		{
			public void mouseDragged(MouseEvent e) 	//when you drag
		    {
		        //sets frame position when mouse dragged            
				Rectangle rectangle = getBounds();
		        setBounds(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY, rectangle.width, rectangle.height);
		    }
		});
	}	
}
