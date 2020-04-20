package dev.chancho.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

public class Etch {
	public BufferedImage tilesetPNG;
	
	public boolean splash = true,controls=false,menu = false, deltaFont=false, flip=false, snowrend=true;
	int splash_impact=0,mmalpha=255,mmFont=86, SCALE = 128 , gameover=0, highscore=0,xoffset=0,yoffset=0,frame=0;
	FontMetrics fm;
	List<Point> grass = new ArrayList<Point>();
	Color 
		yellow = Color.decode("#fac000"),
		orange = Color.decode("#ff7500"),
		snowpart = Color.decode("#eeeeee"),
		bg=Color.black,
		menuBG = Color.decode("#801100"),
		menuFont = Color.decode("#b62203");
	BufferedImage
		snow,
		textblock,
		textblockleft,
		textblockright;
	ArrayList<ArrayList<BufferedImage>> tiles = new ArrayList<ArrayList<BufferedImage>>();
	Font pcs;
	@SuppressWarnings("unchecked")
	public Etch() {
		try {
			pcs = Font.createFont(Font.TRUETYPE_FONT,Etch.class.getResourceAsStream("res/pcsenior.ttf"));
			tilesetPNG = toBI(new ImageIcon(Etch.class.getResource("res/tileset.png")).getImage());
			snow = toBI(new ImageIcon(Etch.class.getResource("res/bg.jpg")).getImage());
			textblock = toBI(getTile(8,9).getScaledInstance(2*32, 2*32, Image.SCALE_DEFAULT));
			textblockleft = toBI(getTile(7,9).getScaledInstance(2*32, 2*32, Image.SCALE_DEFAULT));
			textblockright = createRotated(toBI(getTile(7,9).getScaledInstance(2*32, 2*32, Image.SCALE_DEFAULT)));
			for(int x=0; x<10; x++) {
				ArrayList<BufferedImage> row = new ArrayList<BufferedImage>(); 
				for(int y =0; y<10; y++) {
					row.add(toBI(getTile(x,y).getScaledInstance(SCALE, SCALE, Image.SCALE_DEFAULT)));
				}
				tiles.add(row);
			}
			ArrayList<BufferedImage> heroProjectiles = new ArrayList<BufferedImage>();
			ArrayList<BufferedImage> mobProjectiles = new ArrayList<BufferedImage>();
			for(int aim = 0; aim < 360; aim+=30) {
				heroProjectiles.add(rotateAim(toBI(getTile(9,0).getScaledInstance(SCALE/2, SCALE/2, Image.SCALE_DEFAULT)),aim));
				mobProjectiles.add(rotateAim(toBI(getTile(9,1).getScaledInstance(SCALE/2, SCALE/2, Image.SCALE_DEFAULT)),aim));
			}
			tiles.add(mobProjectiles);
			tiles.add(heroProjectiles);
			ArrayList<BufferedImage> mobRotations = new ArrayList<BufferedImage>();
			for(int mobtype = 3; mobtype <7; mobtype++) {
				mobRotations.clear();
				for(int rot = 0; rot<360; rot+=45) {
					mobRotations.add(rotateAim(toBI(getTile(8,mobtype).getScaledInstance(SCALE, SCALE, Image.SCALE_DEFAULT)),rot));
					mobRotations.add(rotateAim(toBI(getTile(6,mobtype).getScaledInstance(SCALE, SCALE, Image.SCALE_DEFAULT)),rot));
					mobRotations.add(rotateAim(toBI(getTile(7,mobtype).getScaledInstance(SCALE, SCALE, Image.SCALE_DEFAULT)),rot));
					mobRotations.add(rotateAim(toBI(getTile(8,mobtype).getScaledInstance(SCALE, SCALE, Image.SCALE_DEFAULT)),rot));
				}
				tiles.add((ArrayList<BufferedImage>) mobRotations.clone());	
				System.out.println(mobRotations.size());
				System.out.println(tiles.size());
			}
			//X=(11+mobtype),Y=(3*ROT/45)+FRAMEDELTA
			generateGrass();
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	public void render(Graphics g,Board b) {
		g.setFont(pcs.deriveFont(32.0f));
		fm=g.getFontMetrics();
		if(this.splash) splash(g,b);
		else if(this.controls)renderControls(g,b);
		else if(this.menu)renderMainMenu(g,b);
		else if(this.gameover>0)renderGameOver(g,b);
		else renderGame(g,b);
		for(Button button : b.buttons) {
			g.setColor(button.hover?yellow:orange);
			g.drawImage(textblockleft, button.x-g.getFontMetrics().stringWidth("__________")/2-64-32, button.y-g.getFontMetrics().getHeight()-8,null);
			g.drawImage(textblockright, button.x+g.getFontMetrics().stringWidth("__________")/2+64-32, button.y-g.getFontMetrics().getHeight()-8,null);
			for(int x = 0; x<3; x++) {
				g.drawImage(textblock, button.x-g.getFontMetrics().stringWidth("__________")/2+(x*64)-32, button.y-g.getFontMetrics().getHeight()-8,null);
				g.drawImage(textblock, button.x+g.getFontMetrics().stringWidth("__________")/2-(x*64)-32, button.y-g.getFontMetrics().getHeight()-8,null);	
			}
			g.drawString(button.text, button.x-g.getFontMetrics().stringWidth(button.text)/2, button.y);
		}
		Toolkit.getDefaultToolkit().sync();
	}
	private void renderControls(Graphics g, Board b) {
		g.drawImage(snow,0,0,null);
		
		for(int bb = 0; bb<grass.size(); bb++) {
			g.drawImage(getImage(bb%4,8).getScaledInstance(2*32, 2*32, Image.SCALE_DEFAULT), grass.get(bb).x, grass.get(bb).y, null);
		}
		int toastx = 320;
		int toasty = 250;
		int toastw = b.WIDTH-640;
		int toasth = b.HEIGHT/2;


		g.drawImage(getImage(b.fire.getFrame(),b.fire.getHealth()),155,300,null);
		
		//SNOW
		if(snowrend) {
				flip=false;
				Random rand = new Random();
				for(int x = 0; x<b.WIDTH; x+=200) {
					for(int y=0; y<b.HEIGHT;y+=100) {
						g.setColor(snowpart);
						g.fillRect(x+xoffset,y+yoffset,rand.nextInt(8)+1,rand.nextInt(8)+1);
						x+=flip?100:-100;
						y+=flip?-50:50;
						flip=!flip;
					}
				}
				xoffset+=1;
				yoffset+=2;
				frame++;
				if(frame==100) {
					xoffset=0;
					yoffset=0;
					frame=0;
				}
		}
		g.setColor(menuBG);
		g.fillRect(toastx,toasty,toastw,toasth);

		g.setFont(pcs.deriveFont((float)mmFont));
		g.drawString("CONTROLS",b.WIDTH/2-g.getFontMetrics().stringWidth("CONTROLS")/2,200);
		g.setColor(yellow);

		g.setFont(pcs.deriveFont((float)mmFont/4));
		String line1 = "WASD TO MOVE";
		g.drawString(line1, toastx+toastw/2-g.getFontMetrics().stringWidth(line1)/2, toasty+g.getFontMetrics().getHeight()*5);
		String line2 = "MOUSE WHEEL OR ARROWS TO AIM";
		g.drawString(line2, toastx+toastw/2-g.getFontMetrics().stringWidth(line2)/2, toasty+g.getFontMetrics().getHeight()*7);
		String line3 = "CLICK OR SPACE TO THROW";
		g.drawString(line3, toastx+toastw/2-g.getFontMetrics().stringWidth(line3)/2, toasty+g.getFontMetrics().getHeight()*9);
		String line4 = "ESC TO RESET";
		g.drawString(line4, toastx+toastw/2-g.getFontMetrics().stringWidth(line4)/2, toasty+g.getFontMetrics().getHeight()*11);
		String line0 = "KILL DRAGONS AND COLLECT WOOD";
		g.drawString(line0, toastx+toastw/2-g.getFontMetrics().stringWidth(line0)/2, toasty+g.getFontMetrics().getHeight()*2);
		String line01= "TO KEEP THE FIRE LIT";
		g.drawString(line01, toastx+toastw/2-g.getFontMetrics().stringWidth(line0)/2, toasty+g.getFontMetrics().getHeight()*3);
		String line5 = "GOOD LUCK! (PRESS ESC)";
		g.drawString(line5, toastx+toastw/2-g.getFontMetrics().stringWidth(line5)/2, toasty+g.getFontMetrics().getHeight()*13);
		
		
				
		Toolkit.getDefaultToolkit().sync();
	}private void renderGameOver(Graphics g, Board b) {
		g.drawString("Game Over",b.WIDTH/2-g.getFontMetrics().stringWidth("Game Over")/2,200);
		if(highscore<=gameover) {
			highscore=gameover;
			g.drawString("NEW PERSONAL BEST:"+highscore/1000.0,b.WIDTH/2-g.getFontMetrics().stringWidth("NEW PERSONAL BEST"+highscore/1000.0)/2,400);
		}
		
		
		//FADE IN
		if(mmalpha>0)mmalpha-=5;
		g.setColor(new Color(255,255,255,mmalpha));
		g.fillRect(0, 0, b.WIDTH, b.HEIGHT);
		
		Toolkit.getDefaultToolkit().sync();
	}
	private void renderGame(Graphics g, Board b) {
		//BACKGROUND
		g.drawImage(snow,0,0,null);
		
		//FOOTPRINTS
		@SuppressWarnings("unchecked")
		ArrayList<Point> prints= (ArrayList<Point>) b.knight.footprint.clone();
		for(Point p : prints) {
			g.drawImage(getImage(3,7),p.x,p.y,null);
		}		
		
		//GRASS
		for(int bb = 0; bb<grass.size(); bb++) {
			g.drawImage(getImage(bb%4,8).getScaledInstance(2*32, 2*32, Image.SCALE_DEFAULT), grass.get(bb).x, grass.get(bb).y, null);
		}
		
		//FIRE
		
		switch(b.fire.getHealth()) {
		case 0:
			g.drawImage(getImage(2,7), b.fire.x-SCALE/2, b.fire.y-SCALE/2, null);
			g.drawImage(getImage(1,7), b.fire.x-SCALE/2, b.fire.y-SCALE/2, null);
		case 1:
			g.drawImage(getImage(2,7), b.fire.x-SCALE/2, b.fire.y-SCALE/2, null);
		case 2:
			g.drawImage(getImage(1,7), b.fire.x-SCALE/2, b.fire.y-SCALE/2, null);
		}
		g.drawImage(getImage(0,7), b.knight.x-SCALE/2,b.knight.y-SCALE/2, null);
		if(b.knight.y<=358)g.drawImage(getImage(b.knight.getFrameX(),b.knight.getFrameY()), b.knight.x-SCALE/2,b.knight.y-SCALE/2,null);
		g.drawImage(getImage(b.fire.getFrame(),b.fire.getHealth()),b.fire.x-SCALE/2,b.fire.y-SCALE/2,null);
		
		
		if(b.knight.y>358)g.drawImage(getImage(b.knight.getFrameX(),b.knight.getFrameY()), b.knight.x-SCALE/2,b.knight.y-SCALE/2,null);
		
		
		g.drawImage(rotateAim(getImage(9,8),b.knight.aim), b.knight.x-SCALE/2+b.knight.aimdeltax,b.knight.y-SCALE/2-b.knight.aimdeltay,null);
		
		
		//Mobs
		for(Mob m : b.mobs) {
			g.drawImage(getImage(m.getFrameX(),m.getFrameY()),m.x-SCALE/2, m.y-SCALE/2, null);
		}
		
		//PROJECTILES
		for(Projectile pp : b.playerproj)g.drawImage(getImage(11,pp.rendery),pp.x-SCALE/4,pp.y-SCALE/4,null);
		
		//Collectbles
		for(Collectible c : b.collect) {
			g.drawImage(getImage(0,1).getScaledInstance(SCALE/2, SCALE/2, Image.SCALE_DEFAULT),c.x-32,c.y-32,null);
		}
		
		//FADE IN
		if(mmalpha>0)mmalpha-=5;
		g.setColor(new Color(255,255,255,mmalpha));
		g.fillRect(0, 0, b.WIDTH, b.HEIGHT);
		
		Toolkit.getDefaultToolkit().sync();
		
		//SNOW
		if(snowrend) {
			flip=false;
			Random rand = new Random();
			for(int x = 0; x<b.WIDTH; x+=200) {
				for(int y=0; y<b.HEIGHT;y+=100) {
					g.setColor(snowpart);
					g.fillRect(x+xoffset,y+yoffset,rand.nextInt(8)+1,rand.nextInt(8)+1);
					x+=flip?100:-100;
					y+=flip?-50:50;
					flip=!flip;
				}
			}
			xoffset+=1;
			yoffset+=2;
			frame++;
			if(frame==100) {
				xoffset=0;
				yoffset=0;
				frame=0;
			}
		}
		//HUD
		g.setColor(menuBG);
		g.drawString("SCORE:"+b.ticks/1000.0, b.WIDTH-g.getFontMetrics().stringWidth("SCORE: 0.000")-16,50);
		g.drawString("BEST :"+highscore/1000.0,b.WIDTH-g.getFontMetrics().stringWidth("SCORE: 0.000")-16,100);
		g.fillRect(183, b.HEIGHT-36, b.fire.health*10, 20);
		for(int i=0;i<b.knight.health;i++)g.drawImage(getImage(0,2),16+(SCALE/3*i),16,null);

	}
	private void renderMainMenu(Graphics g, Board b) {

		g.drawImage(snow,0,0,null);
		
		
		g.setColor(menuFont);
		g.setFont(pcs.deriveFont((float)mmFont));
		g.drawString(b.title,b.WIDTH/2-g.getFontMetrics().stringWidth(b.title)/2,200);
		

		g.setFont(pcs.deriveFont(32.0f));
		g.drawImage(getImage(b.fire.getFrame(),b.fire.getHealth()),155,300,null);
		
		
		//SNOW
		if(snowrend) {
			flip=false;
			Random rand = new Random();
			for(int x = 0; x<b.WIDTH; x+=200) {
				for(int y=0; y<b.HEIGHT;y+=100) {
					g.setColor(snowpart);
					g.fillRect(x+xoffset,y+yoffset,rand.nextInt(8)+1,rand.nextInt(8)+1);
					x+=flip?100:-100;
					y+=flip?-50:50;
					flip=!flip;
				}
			}
			xoffset+=1;
			yoffset+=2;
			frame++;
			if(frame==100) {
				xoffset=0;
				yoffset=0;
				frame=0;
			}
		}
			
		for(int bb = 0; bb<grass.size(); bb++) {
			g.drawImage(getImage(bb%4,8).getScaledInstance(2*32, 2*32, Image.SCALE_DEFAULT), grass.get(bb).x, grass.get(bb).y, null);
		}
		//FADE IN
		if(mmalpha>0)mmalpha-=5;
		g.setColor(new Color(255,255,255,mmalpha));
		g.fillRect(0, 0, b.WIDTH, b.HEIGHT);
		
		Toolkit.getDefaultToolkit().sync();
	}
	private void splash(Graphics g,Board b) {
		String studio = "Chancho.dev";
		int delta = (b.HEIGHT-(b.ticks*2)>0)?b.HEIGHT-(b.ticks*2):0;
		g.setColor(Color.white);
		g.fillRect(0, 0, b.WIDTH, b.HEIGHT);
		g.setColor(Color.black);		
		g.drawString(studio,b.WIDTH/2-g.getFontMetrics(g.getFont()).stringWidth(studio)/2,b.HEIGHT/2-delta);
		g.setColor(new Color(255,255,255,(delta<300)?50-delta/6:0));
		g.fillOval(
				b.WIDTH/2-g.getFontMetrics(g.getFont()).stringWidth(studio)/2,
				b.HEIGHT/2-g.getFontMetrics().getHeight()/8,
				g.getFontMetrics(g.getFont()).stringWidth(studio),
				g.getFontMetrics().getHeight()/2
		);
		if(delta==0 && splash_impact<=6) {
			g.drawImage(
					getTile(splash_impact,9).getScaledInstance(SCALE, SCALE, Image.SCALE_DEFAULT), 
					b.WIDTH/2-g.getFontMetrics(g.getFont()).stringWidth(studio)/2-SCALE,
					b.HEIGHT/2-SCALE/2,
					null);
			g.drawImage(
					createRotated(createFlipped(getTile(splash_impact,9))).getScaledInstance(SCALE, SCALE, Image.SCALE_DEFAULT), 
					b.WIDTH/2+g.getFontMetrics(g.getFont()).stringWidth(studio)/2,
					b.HEIGHT/2-SCALE/2,
					null);
			if(splash_impact==0)b.o.play(b.o.explode);
			if(b.ticks%10==0)splash_impact++;
		}
		if (splash_impact>6){
			if(b.ticks%60==0)splash_impact++;
			if(splash_impact>12) {
				splash=false;
				b.resetGameState();
				g.setColor(new Color(0,0,0));
			}
		}
		
		Toolkit.getDefaultToolkit().sync();
	}
	
	public void generateGrass() {
		grass.clear();
		Random rand = new Random();
		for(int b = 0; b < 10; b++) {
			grass.add(new Point(rand.nextInt(1366),rand.nextInt(768)));
		}
	}
	
	//Buffered Image Tools
	public BufferedImage toBI(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    return bimage;
	}	
	public BufferedImage getTile(int x,int y) {
		BufferedImage ts = toBI(tilesetPNG);
		return (BufferedImage) toBI(ts.getSubimage(x*32, y*32, 32, 32));
	}
	public BufferedImage getImage(int x,int y) {
		return tiles.get(x).get(y);
	}
	
	private static BufferedImage createTransformed(
	        BufferedImage image, AffineTransform at)
	    {
	        BufferedImage newImage = new BufferedImage(
	            image.getWidth(), image.getHeight(),
	            BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g = newImage.createGraphics();
	        g.transform(at);
	        g.drawImage(image, 0, 0, null);
	        return newImage;
	    }
	private static BufferedImage createFlipped(BufferedImage image)
    {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at);
    }
	private static BufferedImage rotateAim(BufferedImage image, int aim)
    {
	    AffineTransform at = AffineTransform.getRotateInstance(
            Math.toRadians(aim), image.getWidth()/2, image.getHeight()/2);
        return createTransformed(image, at);
    }
	private static BufferedImage createRotated(BufferedImage image)
    {
        AffineTransform at = AffineTransform.getRotateInstance(
            Math.PI, image.getWidth()/2, image.getHeight()/2.0);
        return createTransformed(image, at);
    }
	public BufferedImage hover(BufferedImage bi) {
		for(int x = 0; x<bi.getWidth();x++)for(int y=0;y<bi.getHeight();y++) {
			if(bi.getRGB(x, y)==Color.decode("#801100").getRGB())bi.setRGB(x, y, Color.decode("#b62203").getRGB());
		}
		return bi;
	}
}
