import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;


public class ShowImage extends JPanel {
	Image image;
	static Timer timer = new Timer(50, new TimerActionListener());
	static Controller gamepad;
	static JFrame frame;

	// Create a constructor method
	public ShowImage(){
		super();
		image = Toolkit.getDefaultToolkit().getImage("images/keyboard_NULL.png");
	}

	public ShowImage(String path) {
		super();
		image = Toolkit.getDefaultToolkit().getImage(path);
	}

	// Override getPreferredSize so that calling pack() on
	// the JFrame this panel is added to actually
	// sets the frame to the correct size
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(500, 500);
	}

	@Override
	public void paintComponent(Graphics g){
		g.drawImage(image,0,0,500,500, this);
	}

	public static void main(String arg[]){
		ShowImage panel = new ShowImage();

		frame = new JFrame("ShowImage");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setContentPane(panel);
		frame.setUndecorated(true);
		frame.pack();
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setVisible(true);

		try {
			Controllers.create();
			gamepad = Controllers.getController(0);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		timer.start();
	}


	private static class TimerActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			gamepad.poll();

			if(gamepad.isButtonPressed(0)) {
				System.exit(0);
			}

			// Store X and Y values in some variables for convenience
			float x = gamepad.getAxisValue(1);
			float y = gamepad.getAxisValue(0);

			// Get the JFrame's content pane
			JPanel contentPane = (JPanel) frame.getContentPane();

			// Diagonals
			if(diff(x, y) <= 0.3 && Math.abs(x) > 0.1 && Math.abs(y) > 0.1) {
				// NE
				if(x >= 0.0 && y <= 0.0) {
					ShowImage img = new ShowImage("images/keyboard_NORTH_EAST.png");
					contentPane.removeAll();
					contentPane.add(img);
					contentPane.revalidate();
					contentPane.repaint();
					frame.pack();
				}
				// SE
				else if(x >= 0.0 && y >= 0.0) {
					ShowImage img = new ShowImage("images/keyboard_SOUTH_EAST.png");
					contentPane.removeAll();
					contentPane.add(img);
					contentPane.revalidate();
					contentPane.repaint();
					frame.pack();
				}
				// SW
				else if(x <= 0.0 && y >= 0.0) {
					ShowImage img = new ShowImage("images/keyboard_SOUTH_WEST.png");
					contentPane.removeAll();
					contentPane.add(img);
					contentPane.revalidate();
					contentPane.repaint();
					frame.pack();
				}
				// NW
				else if(x <= 0.0 && y <= 0.0) {
					ShowImage img = new ShowImage("images/keyboard_NORTH_WEST.png");
					contentPane.removeAll();
					contentPane.add(img);
					contentPane.revalidate();
					contentPane.repaint();
					frame.pack();
				}
			}
			else {
				// NULL
				if(Math.abs(y) <= 0.1 && Math.abs(x) <= 0.1) {
					ShowImage img = new ShowImage();
					contentPane.removeAll();
					contentPane.add(img);
					contentPane.revalidate();
					contentPane.repaint();
					frame.pack();
				}
				// N
				else if(y <= -0.9) {
					ShowImage img = new ShowImage("images/keyboard_NORTH.png");
					contentPane.removeAll();
					contentPane.add(img);
					contentPane.revalidate();
					contentPane.repaint();
					frame.pack();
				}
				// S
				else if(y >= 0.9) {
					ShowImage img = new ShowImage("images/keyboard_SOUTH.png");
					contentPane.removeAll();
					contentPane.add(img);
					contentPane.revalidate();
					contentPane.repaint();
					frame.pack();
				}
				// E
				else if(x >= 0.9) {
					ShowImage img = new ShowImage("images/keyboard_EAST.png");
					contentPane.removeAll();
					contentPane.add(img);
					contentPane.revalidate();
					contentPane.repaint();
					frame.pack();
				}
				// W
				else if(x <= -0.9) {
					ShowImage img = new ShowImage("images/keyboard_WEST.png");
					contentPane.removeAll();
					contentPane.add(img);
					contentPane.revalidate();
					contentPane.repaint();
					frame.pack();
				}
			}
		}

		private float diff(float x, float y) {
			return Math.abs(Math.abs(x) - Math.abs(y));
		}
	}
}
