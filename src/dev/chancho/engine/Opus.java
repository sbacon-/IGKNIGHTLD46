package dev.chancho.engine;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Opus {
	public String music,main,explode,boot,gameover,axe,start,branch,hurt;
	public AudioInputStream ais=null;
	public Clip mus=null;
	public AudioInputStream fallback=null;
	public Clip fallbackc=null;
	public boolean mute = false;
	public Opus() {
		hurt="hurt";
		main="main";
		music="frenzy";
		branch="branch";
		start= "start";
		explode = "explode";
		boot = "boot";
		gameover = "gameover";
		axe = "axe";
	}
	public void play(String file) {
		URL music = Opus.class.getResource("res/sound/"+file+".aiff");
		if(!mute) {
			try {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(music);
				if(fallback==null)fallback=audioInput;
				Clip clip = AudioSystem.getClip();
				clip.open(audioInput);
				clip.start();
				if(file=="main"||file=="frenzy") {
					clip.loop(Clip.LOOP_CONTINUOUSLY);
					mus = clip;
					ais = audioInput;
				}
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				mute=true;
				System.out.println("Audio Broke :(");
				stop("AudioBroke");
				try {
					fallback.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				play(this.music);
			}
		}
	}
	public boolean playing() {
		// TODO Auto-generated method stub
		return true;
	}
	public void stop(String music) {
		if(mus!=null)mus.stop();
		if(ais!=null)
			try {
				ais.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
