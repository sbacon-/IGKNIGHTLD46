package dev.chancho.engine;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class KListener implements KeyListener, MouseListener, MouseWheelListener{
	Point mousePosition = null;
	int rot = 0,xdir=0,ydir=0;
	boolean mouseClicked = false,
			W=false,
			A=false,
			S=false,
			D=false,
			ESC=false;
	public void updateKeyState() {
		if(W && !S)ydir = -1;
		else if(S && !W)ydir = 1;
		else ydir=0;
		
		if(D && !A)xdir = 1;
		else if(A && !D)xdir = -1;
		else xdir=0;
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		rot=(e.getWheelRotation()>0?3:-3);
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseClicked=true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_S)S=true;
		if(e.getKeyCode()==KeyEvent.VK_D)D=true;
		if(e.getKeyCode()==KeyEvent.VK_A)A=true;
		if(e.getKeyCode()==KeyEvent.VK_W)W=true;
		if(e.getKeyCode()==KeyEvent.VK_SPACE)mouseClicked=true;
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE)ESC=true;
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
		case KeyEvent.VK_RIGHT:
			rot=3;
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_LEFT:
			rot=-3;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_S)S=false;
		if(e.getKeyCode()==KeyEvent.VK_D)D=false;
		if(e.getKeyCode()==KeyEvent.VK_A)A=false;
		if(e.getKeyCode()==KeyEvent.VK_W)W=false;
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE)ESC=false;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	

}
