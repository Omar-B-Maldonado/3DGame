package main;

import java.awt.Graphics;

public interface GameState
{
	void init();
	void update();
	void render(Graphics pen);
}
