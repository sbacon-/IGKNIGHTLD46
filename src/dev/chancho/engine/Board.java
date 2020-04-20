package dev.chancho.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

public class Board extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	public int WIDTH = 1366;
	public int HEIGHT= 768;
	public int DELAY = 60;
	public int ticks = 0;
	public String title;
	
	public Thread timer;
	public KListener kAdapter = new KListener();
	public Etch e;
	public Opus o;
	public Fire fire;
	public Knight knight;
	public ArrayList<Projectile> playerproj = new ArrayList<Projectile>();
	public ArrayList<Button> buttons = new ArrayList<Button>();
	public ArrayList<Mob> mobs = new ArrayList<Mob>();
	public ArrayList<Collectible> collect = new ArrayList<Collectible>();
	public boolean running = false, cb=false;
	public int mobTypeFilter = 1,mobBaseFilter=1;
	
	public Board(String title){
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setBackground(Color.decode("#fffffff"));
		addKeyListener(kAdapter);
		addMouseListener(kAdapter);
		addMouseWheelListener(kAdapter);
		setFocusable(true);
		requestFocus();
		
		this.title = title;
		init();
		o.play(o.boot);
	}
	private void init() {
		e = new Etch();
		o = new Opus();
		resetGameState();
	}
	private void startGame() {
		o.stop(o.main);
		o.play(o.music);
		e.gameover=0;
		mobs.clear();
		collect.clear();
		playerproj.clear();
		ticks=0;mobTypeFilter = 1;mobBaseFilter=1;
		running=true;
		e.mmalpha=255;
		e.generateGrass();
		fire = new Fire(WIDTH/2,HEIGHT/2);
		knight = new Knight(2*WIDTH/3, HEIGHT/2);
		playerproj.add(new Projectile(-300,-300,0,0,0));
		
	}
	@SuppressWarnings("unchecked")
	public void tick() {
		ticks++;
		for(Button b : buttons) {
			if(b.determineHover(kAdapter.mousePosition)&&kAdapter.mouseClicked)b.click=true;
			if(b.click) {
				switch(b.id){
					case 0:
						e.menu=false;
						e.gameover=0;
						o.play(o.start);
						startGame();
						cb=true;
						break;
					case 1:
						System.exit(0);
						break;
					case 2:
						startGame();
						cb=true;
						break;
					case 3:
						kAdapter.ESC=true;
						cb=true;
						break;
					case 4:
						e.controls=true;
						cb=true;
						break;
					case 5:
						o.stop(o.music);
						o.stop(o.main);
						o.mute=!o.mute;
						b.click=false;
						break;
					case 6:
						e.snowrend=!e.snowrend;
						b.click=false;
						break;
					case 7:
						e.splash=true;
						e.splash_impact=0;
						b.click=false;
						cb=true;
						break;
						
				}
			}
		}
		if(cb) {
			buttons.clear();
			cb=!cb;
		}
		if(running && e.gameover==0) {
			kAdapter.updateKeyState();
			if(ticks%5==0)knight.move(kAdapter.xdir,kAdapter.ydir);
			knight.aim(kAdapter.rot);
			kAdapter.rot=0;
			Random r = new Random();
			if(ticks%3000==0 && mobTypeFilter<4)mobTypeFilter++;
			else if(ticks%3000==0 && mobTypeFilter==4)mobBaseFilter++;
			int spawner = r.nextInt(mobTypeFilter)+mobBaseFilter;
			if(spawner>4)spawner=4;
			if(mobs.size()<10&&ticks%(1000/mobBaseFilter)==0)mobs.add(new Mob(spawner));
			for(Mob m : mobs) {
				m.move(knight.x,knight.y);
			}
			for(Projectile pp : playerproj)pp.tick();
			if(kAdapter.mouseClicked) {
				playerproj.add(new Projectile(knight.x,knight.y,knight.prov().x,knight.prov().y,0));
				if(playerproj.size()>3)playerproj.remove(0);
				kAdapter.mouseClicked=false;
			}
			int mobsize = mobs.size();
			ArrayList<Mob> mobcopy = (ArrayList<Mob>) mobs.clone();
			for(Projectile pp : playerproj)mobs=(ArrayList<Mob>)pp.checkCollision(mobs).clone();
			if(mobsize>mobs.size()) {
				o.play(o.axe);
				for(Mob m:mobcopy)if(!mobs.contains(m)&&r.nextInt(100)>fire.health)collect.add(new Collectible(m.x,m.y,0));
			}
			mobsize = mobs.size();
			mobs=(ArrayList<Mob>) fire.checkCollision(mobs).clone();
			if(mobsize>mobs.size())o.play(o.explode);
			
			mobsize = mobs.size();
			mobs=(ArrayList<Mob>) knight.checkCollision(mobs,fire).clone();
			if(mobsize>mobs.size())o.play(o.hurt);
			ArrayList<Collectible> collectUp = new ArrayList<Collectible>();
			for(Collectible c : collect) {
				if(!c.tick(knight.x, knight.y))collectUp.add(c);
				else {
					fire.health+=20;
					o.play(o.branch);
				}
			}
			collect=(ArrayList<Collectible>) collectUp.clone();
			if((knight.health==0||fire.health==0)&&e.gameover==0) {
				buttons.add(new Button(WIDTH/2,3*(HEIGHT/4)-100,"RETRY",2));
				buttons.add(new Button(WIDTH/2,3*(HEIGHT/4),"MAIN MENU",3));
				e.gameover=ticks;
				o.play(o.gameover);
			}
		}
		if(kAdapter.mouseClicked) {
			kAdapter.mouseClicked=false;
		}
		if(kAdapter.ESC) {
			kAdapter.ESC=!kAdapter.ESC;
			resetGameState();
		}
		if(ticks%100==0) {
			fire.health--;
		}
		kAdapter.mousePosition=this.getMousePosition();
		if(fire.health>100)fire.health=100;
		if(fire.health<0)fire.health=0;
		if(!e.menu && !running) {
			startGame();
		}
		if(e.gameover>0) {
			o.stop(o.music);
		}
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		e.render(g,this);
	}
	
	@Override
	public void addNotify(){
		super.addNotify();
		timer = new Thread(this);
		timer.start();
	}
	public void resetGameState() {
		fire= new Fire(0,0);
		e.menu=true;
		o.stop(o.main);
		o.play(o.main);
		e.controls=false;
		mobs.clear();
		running=false;
		buttons.clear();
		if(!e.splash) {
			buttons.add(new Button(WIDTH/2,HEIGHT/2-64,"New Game",0));
			buttons.add(new Button(WIDTH/2,HEIGHT/2-64+80,"Controls",4));
			buttons.add(new Button(WIDTH-10,HEIGHT-210,"Mute       ",5));
			buttons.add(new Button(WIDTH-10,HEIGHT-140,"Snow       ",6));
			buttons.add(new Button(WIDTH-10,HEIGHT-70,"LD46       ",7));
			buttons.add(new Button(WIDTH/2,HEIGHT/2-64+160,"Exit",1));
		}
	}
	@Override
	public void run() {
		long current,delta,sleep;
		current = System.currentTimeMillis();
		while(true) {
			tick();
			repaint();
			delta=System.currentTimeMillis()-current;
			sleep=DELAY-delta;
			if(sleep<0)sleep=2;
			try{
				Thread.sleep(sleep);
			}catch(InterruptedException e){
				String msg = String.format("Thread interrupted: %s", e.getMessage());
				System.out.println(msg);
			}
		}
	}	
}
