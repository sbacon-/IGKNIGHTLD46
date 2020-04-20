package dev.chancho.engine;

import java.util.Random;

public class Mob {
	public int x, y,aim, type, health, framedelta,dir,targetx,targety;
	public boolean end,chaser;
	public Mob(int type){
		spawn();
		this.type=type;
		this.health=3;
	}
	public int getFrameX(){
		return 11+type;
	}
	public int getFrameY(){
		if(framedelta>=119)framedelta=0;
		framedelta++;
		return 4*(aim/45)+framedelta/30;
	}
	public void spawn() {
		Random rand = new Random();
		if(rand.nextInt(2)==1) {
			y=rand.nextInt(769);
			x=(rand.nextInt(2)==1)?-64:1430;
		}else{
			x=rand.nextInt(1367);
			y=(rand.nextInt(2)==1)?-64:832;
		}
		chaser=rand.nextInt(3)==0;
	}
	public void move(int kx,int ky) {
		if(framedelta%(5-type)==0) {
		targetx=chaser?kx:683;
		targety=chaser?ky:384;
		if(y>targety) {
			y--;
			aim=0;
			if(x<targetx) {
				x++;
				aim=45;
			}
			if(x>targetx) {
				x--;
				aim=315;
			}
		}else if(y<targety) {
			y++;
			aim=180;
			if(x<targetx) {
				x++;
				aim=135;
			}
			if(x>targetx) {
				x--;
				aim=225;
			}
		}else {
			if(x<targetx) {
				x++;
				aim=90;
			}
			if(x>targetx) {
				x--;
				aim=270;
			}
		}
		}
	}
}
