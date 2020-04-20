package dev.chancho.engine;

import java.awt.Point;

public class Button {
	public boolean hover, click=false;
	int x, y, id;
	String text;
	public Button(int x, int y, String text, int id) {
		this.x=x;
		this.y=y;
		this.text=text;
		this.hover=false;
		this.id = id;
	}
	public boolean determineHover(Point mouse) {
		if(mouse==null)return false;
		hover = mouse.x>x-320/2-64 && mouse.x<x+320/2+64-32 && mouse.y>y-36 && mouse.y<y+32;
		return hover;
	}
}
