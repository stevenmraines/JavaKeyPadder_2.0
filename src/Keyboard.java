import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

	/**
	 * 
	 * The Keyboard class extends JPanel and displays the keyboard
	 * GUI images.
	 * 
	 * @author Steven Raines
	 *
	 */
	public class Keyboard extends JPanel {
		private Image img;
		
		// All of the keyboard images are 500x500 pixels
		private final int WIDTH = 500;
		private final int HEIGHT = 500;
		
		
		public Keyboard() {
			super();
			img = Toolkit.getDefaultToolkit().getImage("images/lowercase/lowercase_NULL");
		}
		
		
		public Keyboard(Image img) {
			super();
			this.img = img;
		}
		
		
		/**
		 * Overriden getPreferredSize method will always return the dimensions
		 * of the keyboard images, 500x500 pixels.
		 */
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(WIDTH, HEIGHT);
		}
		
		
		/**
		 * Overriden paintComponent method simply draws an image.
		 */
		@Override
		public void paintComponent(Graphics g) {
			g.drawImage(img, 0, 0, WIDTH, HEIGHT, this);
		}
	}
