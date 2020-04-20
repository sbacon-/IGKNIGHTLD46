package dev.chancho.engine;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Knight {
	public int health=3,frame=0,framedelta=0,x,y,dir,speed,velx,vely,aim,aimdeltax,aimdeltay,foot=0;
	public ArrayList<Point> footprint = new ArrayList<Point>();
	public Knight(int x, int y) {
		this.x=x;
		this.y=y;
		this.dir=2;
		this.speed=6;
		this.velx=0;
		this.vely=0;
		this.aim=0;
	}
	public int getFrameX(){
		framedelta++;
		if(framedelta>=120)framedelta=0;
		if(velx==0 && vely==0) {
			return 3+framedelta/40;
		}
		return(framedelta/40);
	}
	public int getFrameY(){
		return 3+dir;
	}
	public void move(int xdir, int ydir) {
		speed=4;
		velx=xdir;
		vely=ydir;
		if(xdir ==0 || ydir ==0)speed=7;
		x+=xdir*speed;
		y+=ydir*speed;
		if(x<0)x=0;
		if(y<0)y=0;
		if(x>1366)x=1366;
		if(y>768)y=768;
		foot++;
		if(foot%10==0) {
			footprint.add(new Point(x-64,y-64));
			if(footprint.size()>10)footprint.remove(0);
		}
	}
	public ArrayList<Mob> checkCollision(ArrayList<Mob> mobs,Fire fire) {
		ArrayList<Mob> mUpdate = new ArrayList<Mob>();
		for(Mob m : mobs) {
			Rectangle mBounds = new Rectangle(m.x,m.y,128,128);
			Rectangle kBounds = new Rectangle(x,y,72,96);
			if(mBounds.intersects(kBounds)) {
				m.end=true;
				health--;
			}
			if(!m.end)mUpdate.add(m);
		}
		return mUpdate;
	}
	public Point prov(){
		return (new Point(aimdeltax/30,aimdeltay/-30));
	}
	public void aim(int rot) {
		aim+=rot*10;
		if(aim<0)aim=330;
		if(aim>=360)aim=0;
		if(aim>=60 && aim <=120)dir=1;
		if(aim>=150 && aim <=210)dir=0;
		if(aim>=240 && aim <=300)dir=2;
		if(aim>=330 || aim <=30)dir=3;
		switch(aim) {
		case 0:
			aimdeltax=0;
			aimdeltay=90;
			break;
		case 30:
			aimdeltax=30;
			aimdeltay=60;
			break;
		case 60:
			aimdeltax=60;
			aimdeltay=30;
			break;
		case 90:
			aimdeltax=90;
			aimdeltay=0;
			break;
		case 120:
			aimdeltax=60;
			aimdeltay=-30;
			break;
		case 150:
			aimdeltax=30;
			aimdeltay=-60;
			break;
		case 180:
			aimdeltax=0;
			aimdeltay=-90;
			break;
		case 210:
			aimdeltax=-30;
			aimdeltay=-60;
			break;
		case 240:
			aimdeltax=-60;
			aimdeltay=-30;
			break;
		case 270:
			aimdeltax=-90;
			aimdeltay=0;
			break;
		case 300:
			aimdeltax=-60;
			aimdeltay=30;
			break;
		case 330:
			aimdeltax=-30;
			aimdeltay=60;
			break;
		}
	}
	
}
