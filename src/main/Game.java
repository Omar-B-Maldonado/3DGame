package main;

import java.awt.*;

import states.*;

public class Game extends InputHandler
{	
	static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	public static int SCREEN_WIDTH  = gd.getDisplayMode().getWidth ();
	public static int SCREEN_HEIGHT = gd.getDisplayMode().getHeight();
	
	private 	  Runnable 		   gameLoop;
	public static GameStatePanel   statePanel;
	public static GameStateManager stateManager;

	public static GameState test1, test2, test3;
	
	public Game()
	{	
		//System.out.println(SCREEN_WIDTH + "x" + SCREEN_HEIGHT);
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		setResizable  (false);
		setUndecorated(true);
		addKeyListener(this);
		addMouseMotionListener(this);
		setFocusable  (true);
		requestFocus();	
		
		statePanel = new GameStatePanel(SCREEN_WIDTH, SCREEN_HEIGHT);
		add(statePanel);
		
		setCursorInvisible(true);

		init();
		startGame();
		setVisible(true);
	}
	
	public void init()
	{									
		//make a state manager that communicates with the panel we made
		stateManager = new GameStateManager();
		
		//instantiate game states
		test1 = new My3DTest();
		test2 = new My3DTest2();
		test3 = new TextureMapTest1();
		
		stateManager.pushState(test3);
	}
	
	public void startGame()
	{
		try 				   { bot  = new Robot();} 
		catch (AWTException e) {e.printStackTrace();}

		gameLoop = () -> {
			
			while(true)
			{	
				stateManager.update();
				statePanel.repaint ();	
				try					{Thread.sleep(15);} //~60fps
				catch(Exception x)  {				  }
			}
		};
		new Thread(gameLoop).start();
	}
	
	public static void main(String args[]) 
	{
		new Game();
	}
}

/* Borderless-windowed technique:
 * https://www.edureka.co/community/21336/fetching-screen-resolution-using-java#:~:text=You%20can%20fetch%20the%20screen,Dimension%20screenRes%20%3D%20Toolkit.
 *
 * Runnable lambda technique:
 * https://www.geeksforgeeks.org/how-to-create-thread-using-lambda-expressions-in-java/
 */
