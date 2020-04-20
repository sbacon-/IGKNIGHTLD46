package dev.chancho.engine;

import java.util.ArrayList;

public class Fire {
	public int health=100,frame=1,framedelta=0,x,y;
	public Fire(int x, int y) {
		this.x=x;
		this.y=y;
	}
	public int getFrame() {
		framedelta++;
		if(framedelta>25) {
			frame++;
			framedelta=0;
		}
		if(frame>8)frame=1;
		if(health<=0)frame=0;
		return this.frame;
	}
	public int getHealth() {
		if(health<=100&&health>66)return 0;
		if(health<=66&&health>33)return 1;
		if(health<=33&&health>0)return 2;
		else return 0;
	}
	public ArrayList<Mob> checkCollision(ArrayList<Mob> mobs) {
		ArrayList<Mob> mobsUp=new ArrayList<Mob>();
		for(Mob m : mobs) {
			if((x-64<m.x && x+64>m.x)&&(y-64<m.y && y+64>m.y)) {
				m.end=true;
				health-=5*m.type;
			}
		}
		for(Mob m : mobs) { if(!m.end) mobsUp.add(m);}
		return mobsUp;
	}
	
}
