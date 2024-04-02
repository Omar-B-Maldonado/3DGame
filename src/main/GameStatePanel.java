package main;

import javax.swing.*;
import java.awt.*;

public class GameStatePanel extends JPanel
{	
	private GameState currentState;
	
	public GameStatePanel(int width, int height)
	{
		setPreferredSize (new Dimension(width, height));
	    setDoubleBuffered(true);
	}
	
	public void setState(GameState state)
	{
		currentState = state;
	}
	
	@Override
	protected void paintComponent(Graphics pen)
	{
		super.paintComponent(pen);
		if(currentState != null) currentState.render(pen);
	}
}
