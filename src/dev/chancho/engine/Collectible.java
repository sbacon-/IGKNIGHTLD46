package dev.chancho.engine;

import java.awt.Rectangle;

public class Collectible {
	public int x,y,type,deltax;
	public boolean vex, end;
	public Collectible(int x, int y, int type) {
		this.x=x;
		this.y=y;
		this.type=type;
		this.vex=true;
		this.end=false;
	}
	public boolean tick(int kx, int ky){
		deltax+=vex?-1:1;
		if (deltax > 30) {
			vex = true;
		}
		if (deltax < 0) {
			vex = false;
		}
		if(deltax%10==0)y+=vex?-1:1;
		
		Rectangle colBounds = new Rectangle (x,y,64,64);
		Rectangle kBounds = new Rectangle (kx, ky, 128,128);
		return (colBounds.intersects(kBounds));
	}
	
}
