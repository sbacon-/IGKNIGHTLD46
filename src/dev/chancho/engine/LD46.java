package dev.chancho.engine;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class LD46 extends JFrame{
	private static final long serialVersionUID = 1L;
	public LD46() {
		init("IGKNIGHT");
	}
	public void init(String s) {
		add(new Board(s));
		setResizable(false);
		pack();
		setTitle(s);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(()->{
			LD46 f = new LD46();
			f.setVisible(true);
		});
		
	}
}
