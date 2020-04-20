package dev.chancho.engine;

import java.awt.Rectangle;
import java.util.ArrayList;

public class Projectile {
	public int x, y, framedelta,rendery, velx, vely,type;
	public boolean end;
	public Projectile(int x,int y,int velx,int vely,int type){
		this.x=x;
		this.y=y;
		this.velx=velx;
		this.vely=vely;
		this.type=type;
		this.end=false;
		this.rendery=0;
	}
	public void tick(){
		framedelta++;
		if(framedelta%10==0)rendery--;
		if(rendery<0)rendery=11;
		if(x<-100||x>1466||y<-100||y>868)end=true;
		x+=velx;
		y+=vely;
	}
	public ArrayList<Mob> checkCollision(ArrayList<Mob> mobs) {
		ArrayList<Mob> mobsUp = new ArrayList<Mob>();
		for(Mob m : mobs) {
			Rectangle pBounds = new Rectangle(x-16,y-16,32,32);
			Rectangle mBounds = new Rectangle(m.x-64,m.y-64,128,128);
			if(pBounds.intersects(mBounds))m.end=true;
		}for(Mob m : mobs) {if(!m.end)mobsUp.add(m);}
		return mobsUp;
	}
}
