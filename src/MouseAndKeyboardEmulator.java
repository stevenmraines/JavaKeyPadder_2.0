import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.lwjgl.input.Controller;


/**
 * 
 * MouseAndKeyboardEmulator handles gamepad inputs and translates it
 * into the appropriate mouse and keyboard actions.
 * 
 * @author Steven Raines
 *
 */
public class MouseAndKeyboardEmulator {
	// GUI components
	private JFrame frame = new JFrame();
	private JPanel contentPane = new Keyboard();
	
	// Lowercase keyboard images
	private Image lowercaseNull =
			Toolkit.getDefaultToolkit().getImage("images/lowercase/lowercase_NULL.png");
	private Image lowercaseNorth =
			Toolkit.getDefaultToolkit().getImage("images/lowercase/lowercase_NORTH.png");
	private Image lowercaseSouth =
			Toolkit.getDefaultToolkit().getImage("images/lowercase/lowercase_SOUTH.png");
	private Image lowercaseEast =
			Toolkit.getDefaultToolkit().getImage("images/lowercase/lowercase_EAST.png");
	private Image lowercaseWest =
			Toolkit.getDefaultToolkit().getImage("images/lowercase/lowercase_WEST.png");
	private Image lowercaseNorthEast =
			Toolkit.getDefaultToolkit().getImage("images/lowercase/lowercase_NORTH_EAST.png");
	private Image lowercaseSouthEast =
			Toolkit.getDefaultToolkit().getImage("images/lowercase/lowercase_SOUTH_EAST.png");
	private Image lowercaseSouthWest =
			Toolkit.getDefaultToolkit().getImage("images/lowercase/lowercase_SOUTH_WEST.png");
	private Image lowercaseNorthWest =
			Toolkit.getDefaultToolkit().getImage("images/lowercase/lowercase_NORTH_WEST.png");
	
	// Uppercase keyboard images
	private Image uppercaseNull =
			Toolkit.getDefaultToolkit().getImage("images/uppercase/uppercase_NULL.png");
	private Image uppercaseNorth =
			Toolkit.getDefaultToolkit().getImage("images/uppercase/uppercase_NORTH.png");
	private Image uppercaseSouth =
			Toolkit.getDefaultToolkit().getImage("images/uppercase/uppercase_SOUTH.png");
	private Image uppercaseEast =
			Toolkit.getDefaultToolkit().getImage("images/uppercase/uppercase_EAST.png");
	private Image uppercaseWest =
			Toolkit.getDefaultToolkit().getImage("images/uppercase/uppercase_WEST.png");
	private Image uppercaseNorthEast =
			Toolkit.getDefaultToolkit().getImage("images/uppercase/uppercase_NORTH_EAST.png");
	private Image uppercaseSouthEast =
			Toolkit.getDefaultToolkit().getImage("images/uppercase/uppercase_SOUTH_EAST.png");
	private Image uppercaseSouthWest =
			Toolkit.getDefaultToolkit().getImage("images/uppercase/uppercase_SOUTH_WEST.png");
	private Image uppercaseNorthWest =
			Toolkit.getDefaultToolkit().getImage("images/uppercase/uppercase_NORTH_WEST.png");
	
	// Symbols keyboard images
	private Image symbolsNull =
			Toolkit.getDefaultToolkit().getImage("images/symbols/symbols_NULL.png");
	private Image symbolsNorth =
			Toolkit.getDefaultToolkit().getImage("images/symbols/symbols_NORTH.png");
	private Image symbolsSouth =
			Toolkit.getDefaultToolkit().getImage("images/symbols/symbols_SOUTH.png");
	private Image symbolsEast =
			Toolkit.getDefaultToolkit().getImage("images/symbols/symbols_EAST.png");
	private Image symbolsWest =
			Toolkit.getDefaultToolkit().getImage("images/symbols/symbols_WEST.png");
	private Image symbolsNorthEast =
			Toolkit.getDefaultToolkit().getImage("images/symbols/symbols_NORTH_EAST.png");
	private Image symbolsSouthEast =
			Toolkit.getDefaultToolkit().getImage("images/symbols/symbols_SOUTH_EAST.png");
	private Image symbolsSouthWest =
			Toolkit.getDefaultToolkit().getImage("images/symbols/symbols_SOUTH_WEST.png");
	private Image symbolsNorthWest =
			Toolkit.getDefaultToolkit().getImage("images/symbols/symbols_NORTH_WEST.png");
	
	// Store all keyboard images in an array for use in the flicker method
	private Image[] images = new Image[27];
	
	// Button indices
	private int a;
	private int b;
	private int x;
	private int y;
	private int lb;
	private int rb;
	private int back;
	private int start;
	private int ls;
	private int rs;
	
	// Dpad values
	private float dPadRight;
	private float dPadLeft;
	private float dPadDown;
	private float dPadUp;
	
	// Analogue indices
	private int leftYAxis;
	private int leftXAxis;
	private int rightYAxis;
	private int rightXAxis;
	private int triggerAxis;
	
	// Analogue values
	private float leftY;
	private float leftX;
	private float rightY;
	private float rightX;
	private float leftTrigger;
	private float rightTrigger;
	
	// Initialize user settings variables to defaults
	private float leftDeadZone = 0.2f;
	private float rightDeadZone = 0.2f;
	private float triggerDeadZone = 0.2f;
	private int sensitivity = 5;
	
	// Initialize polling rates
	private final int BUTTON_RATE = 1;
	private final int STICK_BUTTON_RATE = 200;
	private final int D_PAD_RATE = 100;
	private final int ANALOGUE_RATE = 1;
	private final int TRIGGER_RATE = 100;
	
	// Other instance variables
	private Robot emulator;
	private Controller gamepad;
	private PointerInfo cursor;
	private Point cursorLocation;
	private Timer masterTimer = new Timer(3000, new MasterTimerActionListener());
	private PauseTimer buttonPauser =
			new PauseTimer(150, new PauseTimerActionListener(), PauseTimer.BUTTON_TYPE);
	private Timer buttonTimer = new Timer(BUTTON_RATE, new ButtonTimerActionListener());
	private PauseTimer stickPauser =
			new PauseTimer(150, new PauseTimerActionListener(), PauseTimer.STICK_BUTTON_TYPE);
	private Timer stickButtonTimer = new Timer(STICK_BUTTON_RATE, 
			new StickButtonTimerActionListener());
	private PauseTimer dPadPauser = new PauseTimer(150, new PauseTimerActionListener(), PauseTimer.D_PAD_TYPE);
	private Timer dPadTimer = new Timer(D_PAD_RATE, new DPadTimerActionListener());
	private Timer analogueTimer = new Timer(ANALOGUE_RATE, new AnalogueTimerActionListener());
	private PauseTimer triggerPauser =
			new PauseTimer(150, new PauseTimerActionListener(), PauseTimer.TRIGGER_TYPE);
	private Timer triggerTimer = new Timer(TRIGGER_RATE, new TriggerTimerActionListener());
	private ArrayList<String> whitelist = new ArrayList<String>();
	private ArrayList<String> programs;
	private Process p;
	private BufferedReader pr;
	private boolean isLowercaseActive = true;  // default when keyboard is initially shown
	private boolean isUppercaseActive = false;
	private boolean isSymbolsActive = false;
	private final int LOWERCASE_BOOL = 0;
	private final int UPPERCASE_BOOL = 1;
	private final int SYMBOLS_BOOL = 2;
	private Map<String, KeyboardValue> lowercaseDictionary = new HashMap<>();
	private Map<String, KeyboardValue> uppercaseDictionary = new HashMap<>();
	private Map<String, KeyboardValue> symbolsDictionary = new HashMap<>();
	
	
	public MouseAndKeyboardEmulator(Map<String, Integer> buttons, Map<String, Float> dPad,
			Map<String, String> axes, Controller c) {
		// Initialize the Robot instance
		try {
			emulator = new Robot();
		} catch(AWTException e) {
			e.printStackTrace();
		}
		
		// Initialize instance variables
		a = buttons.get("A");
		b = buttons.get("B");
		x = buttons.get("X");
		y = buttons.get("Y");
		lb = buttons.get("LB");
		rb = buttons.get("RB");
		back = buttons.get("Back");
		start = buttons.get("Start");
		ls = buttons.get("LS");
		rs = buttons.get("RS");
		dPadRight = dPad.get("DX");
		dPadLeft = -dPadRight;
		dPadDown = dPad.get("DY");
		dPadUp = -dPadDown;
		leftYAxis = Integer.parseInt(axes.get("LY").split(":")[0]);
		leftXAxis = Integer.parseInt(axes.get("LX").split(":")[0]);
		rightYAxis = Integer.parseInt(axes.get("RY").split(":")[0]);
		rightXAxis = Integer.parseInt(axes.get("RX").split(":")[0]);
		triggerAxis = Integer.parseInt(axes.get("LT").split(":")[0]);
		leftY = Float.parseFloat(axes.get("LY").split(":")[1]);
		leftX = Float.parseFloat(axes.get("LX").split(":")[1]);
		rightY = Float.parseFloat(axes.get("RY").split(":")[1]);
		rightX = Float.parseFloat(axes.get("RX").split(":")[1]);
		leftTrigger = Float.parseFloat(axes.get("LT").split(":")[1]);
		rightTrigger = -leftTrigger;
		gamepad = c;
		
		// Initialize lowercase keyboard dictionary
		lowercaseDictionary.put("y:N", new KeyboardValue(KeyEvent.VK_A, false));
		lowercaseDictionary.put("b:N", new KeyboardValue(KeyEvent.VK_B, false));
		lowercaseDictionary.put("a:N", new KeyboardValue(KeyEvent.VK_C, false));
		lowercaseDictionary.put("x:N", new KeyboardValue(KeyEvent.VK_D, false));
		lowercaseDictionary.put("y:NE", new KeyboardValue(KeyEvent.VK_E, false));
		lowercaseDictionary.put("b:NE", new KeyboardValue(KeyEvent.VK_F, false));
		lowercaseDictionary.put("a:NE", new KeyboardValue(KeyEvent.VK_G, false));
		lowercaseDictionary.put("x:NE", new KeyboardValue(KeyEvent.VK_H, false));
		lowercaseDictionary.put("y:E", new KeyboardValue(KeyEvent.VK_I, false));
		lowercaseDictionary.put("b:E", new KeyboardValue(KeyEvent.VK_J, false));
		lowercaseDictionary.put("a:E", new KeyboardValue(KeyEvent.VK_K, false));
		lowercaseDictionary.put("x:E", new KeyboardValue(KeyEvent.VK_L, false));
		lowercaseDictionary.put("y:SE", new KeyboardValue(KeyEvent.VK_M, false));
		lowercaseDictionary.put("b:SE", new KeyboardValue(KeyEvent.VK_N, false));
		lowercaseDictionary.put("a:SE", new KeyboardValue(KeyEvent.VK_O, false));
		lowercaseDictionary.put("x:SE", new KeyboardValue(KeyEvent.VK_P, false));
		lowercaseDictionary.put("y:S", new KeyboardValue(KeyEvent.VK_Q, false));
		lowercaseDictionary.put("b:S", new KeyboardValue(KeyEvent.VK_R, false));
		lowercaseDictionary.put("a:S", new KeyboardValue(KeyEvent.VK_S, false));
		lowercaseDictionary.put("x:S", new KeyboardValue(KeyEvent.VK_T, false));
		lowercaseDictionary.put("y:SW", new KeyboardValue(KeyEvent.VK_U, false));
		lowercaseDictionary.put("b:SW", new KeyboardValue(KeyEvent.VK_V, false));
		lowercaseDictionary.put("a:SW", new KeyboardValue(KeyEvent.VK_W, false));
		lowercaseDictionary.put("x:SW", new KeyboardValue(KeyEvent.VK_X, false));
		lowercaseDictionary.put("y:W", new KeyboardValue(KeyEvent.VK_Y, false));
		lowercaseDictionary.put("b:W", new KeyboardValue(KeyEvent.VK_Z, false));
		lowercaseDictionary.put("a:W", new KeyboardValue(KeyEvent.VK_PERIOD, false));
		lowercaseDictionary.put("x:W", new KeyboardValue(KeyEvent.VK_SLASH, true));
		lowercaseDictionary.put("y:NW", new KeyboardValue(KeyEvent.VK_1, true));
		lowercaseDictionary.put("b:NW", new KeyboardValue(KeyEvent.VK_COMMA, false));
		lowercaseDictionary.put("a:NW", new KeyboardValue(KeyEvent.VK_SLASH, false));
		lowercaseDictionary.put("x:NW", new KeyboardValue(KeyEvent.VK_2, true));
		
		// Initialize uppercase keyboard dictionary
		uppercaseDictionary.put("y:N", new KeyboardValue(KeyEvent.VK_A, true));
		uppercaseDictionary.put("b:N", new KeyboardValue(KeyEvent.VK_B, true));
		uppercaseDictionary.put("a:N", new KeyboardValue(KeyEvent.VK_C, true));
		uppercaseDictionary.put("x:N", new KeyboardValue(KeyEvent.VK_D, true));
		uppercaseDictionary.put("y:NE", new KeyboardValue(KeyEvent.VK_E, true));
		uppercaseDictionary.put("b:NE", new KeyboardValue(KeyEvent.VK_F, true));
		uppercaseDictionary.put("a:NE", new KeyboardValue(KeyEvent.VK_G, true));
		uppercaseDictionary.put("x:NE", new KeyboardValue(KeyEvent.VK_H, true));
		uppercaseDictionary.put("y:E", new KeyboardValue(KeyEvent.VK_I, true));
		uppercaseDictionary.put("b:E", new KeyboardValue(KeyEvent.VK_J, true));
		uppercaseDictionary.put("a:E", new KeyboardValue(KeyEvent.VK_K, true));
		uppercaseDictionary.put("x:E", new KeyboardValue(KeyEvent.VK_L, true));
		uppercaseDictionary.put("y:SE", new KeyboardValue(KeyEvent.VK_M, true));
		uppercaseDictionary.put("b:SE", new KeyboardValue(KeyEvent.VK_N, true));
		uppercaseDictionary.put("a:SE", new KeyboardValue(KeyEvent.VK_O, true));
		uppercaseDictionary.put("x:SE", new KeyboardValue(KeyEvent.VK_P, true));
		uppercaseDictionary.put("y:S", new KeyboardValue(KeyEvent.VK_Q, true));
		uppercaseDictionary.put("b:S", new KeyboardValue(KeyEvent.VK_R, true));
		uppercaseDictionary.put("a:S", new KeyboardValue(KeyEvent.VK_S, true));
		uppercaseDictionary.put("x:S", new KeyboardValue(KeyEvent.VK_T, true));
		uppercaseDictionary.put("y:SW", new KeyboardValue(KeyEvent.VK_U, true));
		uppercaseDictionary.put("b:SW", new KeyboardValue(KeyEvent.VK_V, true));
		uppercaseDictionary.put("a:SW", new KeyboardValue(KeyEvent.VK_W, true));
		uppercaseDictionary.put("x:SW", new KeyboardValue(KeyEvent.VK_X, true));
		uppercaseDictionary.put("y:W", new KeyboardValue(KeyEvent.VK_Y, true));
		uppercaseDictionary.put("b:W", new KeyboardValue(KeyEvent.VK_Z, true));
		uppercaseDictionary.put("a:W", new KeyboardValue(KeyEvent.VK_PERIOD, false));
		uppercaseDictionary.put("x:W", new KeyboardValue(KeyEvent.VK_SLASH, true));
		uppercaseDictionary.put("y:NW", new KeyboardValue(KeyEvent.VK_1, true));
		uppercaseDictionary.put("b:NW", new KeyboardValue(KeyEvent.VK_COMMA, false));
		uppercaseDictionary.put("a:NW", new KeyboardValue(KeyEvent.VK_SLASH, false));
		uppercaseDictionary.put("x:NW", new KeyboardValue(KeyEvent.VK_2, true));
		
		// Initialize symbols keyboard dictionary
		symbolsDictionary.put("y:N", new KeyboardValue(KeyEvent.VK_1, false));
		symbolsDictionary.put("b:N", new KeyboardValue(KeyEvent.VK_2, false));
		symbolsDictionary.put("a:N", new KeyboardValue(KeyEvent.VK_3, false));
		symbolsDictionary.put("x:N", new KeyboardValue(KeyEvent.VK_4, false));
		symbolsDictionary.put("y:NE", new KeyboardValue(KeyEvent.VK_5, false));
		symbolsDictionary.put("b:NE", new KeyboardValue(KeyEvent.VK_6, false));
		symbolsDictionary.put("a:NE", new KeyboardValue(KeyEvent.VK_7, false));
		symbolsDictionary.put("x:NE", new KeyboardValue(KeyEvent.VK_8, false));
		symbolsDictionary.put("y:E", new KeyboardValue(KeyEvent.VK_9, false));
		symbolsDictionary.put("b:E", new KeyboardValue(KeyEvent.VK_0, false));
		symbolsDictionary.put("a:E", new KeyboardValue(KeyEvent.VK_EQUALS, true));
		symbolsDictionary.put("x:E", new KeyboardValue(KeyEvent.VK_MINUS, false));
		symbolsDictionary.put("y:SE", new KeyboardValue(KeyEvent.VK_8, true));
		symbolsDictionary.put("b:SE", new KeyboardValue(KeyEvent.VK_SLASH, false));
		symbolsDictionary.put("a:SE", new KeyboardValue(KeyEvent.VK_EQUALS, false));
		symbolsDictionary.put("x:SE", new KeyboardValue(KeyEvent.VK_1, true));
		symbolsDictionary.put("y:S", new KeyboardValue(KeyEvent.VK_2, true));
		symbolsDictionary.put("b:S", new KeyboardValue(KeyEvent.VK_3, true));
		symbolsDictionary.put("a:S", new KeyboardValue(KeyEvent.VK_4, true));
		symbolsDictionary.put("x:S", new KeyboardValue(KeyEvent.VK_5, true));
		symbolsDictionary.put("y:SW", new KeyboardValue(KeyEvent.VK_6, true));
		symbolsDictionary.put("b:SW", new KeyboardValue(KeyEvent.VK_7, true));
		symbolsDictionary.put("a:SW", new KeyboardValue(KeyEvent.VK_9, true));
		symbolsDictionary.put("x:SW", new KeyboardValue(KeyEvent.VK_0, true));
		symbolsDictionary.put("y:W", new KeyboardValue(KeyEvent.VK_MINUS, true));
		symbolsDictionary.put("b:W", new KeyboardValue(KeyEvent.VK_BACK_QUOTE, true));  // This is actually tilde
		symbolsDictionary.put("a:W", new KeyboardValue(KeyEvent.VK_BACK_SLASH, false));
		symbolsDictionary.put("x:W", new KeyboardValue(KeyEvent.VK_QUOTE, true));
		symbolsDictionary.put("y:NW", new KeyboardValue(KeyEvent.VK_SEMICOLON, false));
		symbolsDictionary.put("b:NW", new KeyboardValue(KeyEvent.VK_SEMICOLON, true));
		symbolsDictionary.put("a:NW", new KeyboardValue(KeyEvent.VK_PERIOD, true));
		symbolsDictionary.put("x:NW", new KeyboardValue(KeyEvent.VK_COMMA, true));
		
		// Initialize GUI elements
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setContentPane(contentPane);
		frame.setUndecorated(true);
		frame.pack();
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setAlwaysOnTop(true);
		frame.setFocusableWindowState(false);
		
		// Add keyboard images to a list
		images[0] = lowercaseNull;
		images[1] = lowercaseNorth;
		images[2] = lowercaseSouth;
		images[3] = lowercaseEast;
		images[4] = lowercaseWest;
		images[5] = lowercaseNorthEast;
		images[6] = lowercaseSouthEast;
		images[7] = lowercaseSouthWest;
		images[8] = lowercaseNorthWest;
		
		images[9] = uppercaseNull;
		images[10] = uppercaseNorth;
		images[11] = uppercaseSouth;
		images[12] = uppercaseEast;
		images[13] = uppercaseWest;
		images[14] = uppercaseNorthEast;
		images[15] = uppercaseSouthEast;
		images[16] = uppercaseSouthWest;
		images[17] = uppercaseNorthWest;
		
		images[18] = symbolsNull;
		images[19] = symbolsNorth;
		images[20] = symbolsSouth;
		images[21] = symbolsEast;
		images[22] = symbolsWest;
		images[23] = symbolsNorthEast;
		images[24] = symbolsSouthEast;
		images[25] = symbolsSouthWest;
		images[26] = symbolsNorthWest;
		
		// Set the pause timers so that they only ever go off once
		buttonPauser.setRepeats(false);
		stickPauser.setRepeats(false);
		dPadPauser.setRepeats(false);
		triggerPauser.setRepeats(false);
		
		// Start master timer
		masterTimer.start();
	}
	
	
	private void getWhitelist() {
		try {
			FileReader fr = new FileReader("whitelist.txt");
			BufferedReader br = new BufferedReader(fr);
			
			String line = br.readLine();
			
			while(line != null) {
				whitelist.add(line);
				line = br.readLine();
			}
			
			br.close();
			
		} catch (IOException e) {
			if(e instanceof FileNotFoundException) {
				JOptionPane.showMessageDialog(null, "Whitelist.txt not found.");
			}
			else {
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * The start method starts all of the timer objects in this class.
	 */
	public void start() {
		buttonTimer.start();
		stickButtonTimer.start();
		dPadTimer.start();
		analogueTimer.start();
		triggerTimer.start();
		
		if(whitelist.size() == 0) {
			// Get program whitelist
			getWhitelist();
		}
	}
	
	
	/**
	 * The stop method stops all of the timer objects in this class.
	 */
	public void stop() {
		buttonTimer.stop();
		stickButtonTimer.stop();
		dPadTimer.stop();
		analogueTimer.stop();
		triggerTimer.stop();
	}
	
	
	/**
	 * The isRunning method returns a boolean representing whether or not
	 * the input timers are currently running.
	 * 
	 * @return True if timers are running, false otherwise.
	 */
	public boolean isRunning() {
		return buttonTimer.isRunning();
	}
	
	
	/**
	 * The updateSettings method updates the user preferences
	 * with the values that are passed when it is called.
	 * 
	 * @param settings A list of user settings.
	 */
	public void updateSettings(ArrayList<Object> settings) {
		leftDeadZone = (float) settings.get(0);
		rightDeadZone = (float) settings.get(1);
		triggerDeadZone = (float) settings.get(2);
		sensitivity = (int) settings.get(3);
	}
	
	
	/**
	 * The diff method returns a float representing the difference
	 * between two float values.
	 * 
	 * @param x The first float value.
	 * @param y The second float value.
	 * @return The difference between the two arguments.
	 */
	private float diff(float x, float y) {
		return Math.abs(Math.abs(x) - Math.abs(y));
	}
	
	
	/**
	 * The toggleBools method is used to toggle the group of booleans
	 * used to determine the current state of the keyboard. Only one
	 * of the three booleans, isLowercaseActive, isUppercaseActive,
	 * and isSymbolsActive can be true at any given time, so this method
	 * is used to change their states.
	 * 
	 * @param bool Specifies which boolean to set to true. Pass one of the
	 * three final ints, LOWERCASE_BOOL, UPPERCASE_BOOL, or SYMBOLS_BOOL
	 * for this value.
	 */
	private void toggleBools(int bool) {
		if(bool == 0) {
			isLowercaseActive = true;
			isUppercaseActive = false;
			isSymbolsActive = false;
		}
		else if(bool == 1) {
			isLowercaseActive = false;
			isUppercaseActive = true;
			isSymbolsActive = false;
		}
		else {
			isLowercaseActive = false;
			isUppercaseActive = false;
			isSymbolsActive = true;
		}
	}
	
	
	/**
	 * The getDirection method returns a string value
	 * representing the direction in which the chosen stick
	 * is pointing, relative to the directions of a compass.
	 * Returns "NULL" if the stick is not being pointed
	 * in any direction.
	 * 
	 * @param axisX The X axis of the chosen stick.
	 * @param axisY the Y axis of the chosen stick.
	 * @return The compass direction, i.e. "N", or "SE".
	 */
	public String getDirection(int axisX, int axisY) {
		// Store analogue stick x and y values in some variables
		float x = gamepad.getAxisValue(axisX);
		float y = gamepad.getAxisValue(axisY);
		
		// Diagonals
		if(diff(x, y) <= 0.3 && Math.abs(x) > 0.1 && Math.abs(y) > 0.1) {	
			if(x >= 0.0 && y <= 0.0) {
				return "NE";
			}
			else if(x >= 0.0 && y >= 0.0) {
				return "SE";
			}
			else if(x <= 0.0 && y >= 0.0) {
				return "SW";
			}
			else if(x <= 0.0 && y <= 0.0) {
				return "NW";
			}
		}
		else {
			if(Math.abs(y) <= 0.1 && Math.abs(x) <= 0.1) {
				return "NULL";
			}
			else if(y <= -0.9) {
				return "N";
			}
			else if(y >= 0.9) {
				return "S";
			}
			else if(x >= 0.9) {
				return "E";
			}
			else if(x <= -0.9) {
				return "W";
			}
		}
		
		return "";
	}
	
	
	/**
	 * 
	 * The MasterTimerActionListener is responsible for checking for user
	 * defined programs in the task manager. If one of these programs is
	 * found running, this action listener pauses all of the other timers
	 * so that gamepad control can be taken over by that program. When the
	 * program is no longer running, the timers are resumed and gamepad
	 * control is returned to this program.
	 * 
	 * @author Steven Raines
	 *
	 */
	private class MasterTimerActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean programFound = false;
			programs = new ArrayList<String>();
			
			// Get currently running processes from Windows task list	
			try {
				p =Runtime.getRuntime().exec(System.getenv("windir") +
						"\\system32\\" + "tasklist.exe");
				pr = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
				String line = null;
				
				while((line = pr.readLine()) != null) {
					programs.add(line);
				}
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// Check if one of the whitelisted programs is running
			for(String p: programs) {
				for(String s: whitelist) {
					if(p.contains(s)) {
						if(isRunning()) {
							stop();
						}
							
						programFound = true;
					}
				}
			}
			
			if(!programFound && !isRunning()) {
				start();
			}
		}
	}
	
	
	private class PauseTimerActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			PauseTimer pt = (PauseTimer) e.getSource();
			
			if(pt.getType().equals(PauseTimer.BUTTON_TYPE)) {
				buttonTimer.start();
			}
			else if(pt.getType().equals(PauseTimer.STICK_BUTTON_TYPE)) {
				stickButtonTimer.start();
			}
			else if(pt.getType().equals(PauseTimer.D_PAD_TYPE)) {
				dPadTimer.start();
			}
			else if(pt.getType().equals(PauseTimer.TRIGGER_TYPE)) {
				triggerTimer.start();
			}
		}
	}
	
	
	// TODO Most of this input translating code for the 5 action listeners was
	// copied over from a previous version of the program that didn't bother to
	// verify button and axes indices or axis orientations. So we must add checks
	// using the variables we got from the mapping GUI. Instead of using an if
	// statement like if(gamepad.getAxisValue(leftXAxis) > 0.1), we must make use
	// of the orientation variables like leftY.
	/**
	 * 
	 * ButtonTimerActionListener handles events generated by the buttonTimer. 
	 * 
	 * @author Steven Raines
	 *
	 */
	private class ButtonTimerActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(gamepad.isButtonPressed(start)) {
				emulator.keyPress(KeyEvent.VK_ENTER);
				emulator.keyRelease(KeyEvent.VK_ENTER);
				buttonTimer.stop();
				buttonPauser.start();
			}
			
			if(frame.isVisible()) {
				if(!getDirection(leftXAxis, leftYAxis).equals("NULL")
						&& !getDirection(leftXAxis, leftYAxis).equals("")) {
					// Keyboard buttons
					String button = "";
					
					if(gamepad.isButtonPressed(a)) {
						button = "a";
					}
					else if(gamepad.isButtonPressed(b)) {
						button = "b";
					}
					else if(gamepad.isButtonPressed(x)) {
						button = "x";
					}
					else if(gamepad.isButtonPressed(y)) {
						button = "y";
					}
					
					if(!button.equals("")) {
						KeyboardValue kv;
						
						if(isLowercaseActive) {
							kv = lowercaseDictionary.get(button + ":" +
							getDirection(leftXAxis, leftYAxis));
						}
						else if(isUppercaseActive) {
							kv = uppercaseDictionary.get(button + ":" +
									getDirection(leftXAxis, leftYAxis));
						}
						else {
							kv = symbolsDictionary.get(button + ":" +
									getDirection(leftXAxis, leftYAxis));
						}
						
						if(kv.needsShift()) {
							emulator.keyPress(KeyEvent.VK_SHIFT);
							emulator.keyPress(kv.getKeyCode());
							emulator.keyRelease(kv.getKeyCode());
							emulator.keyRelease(KeyEvent.VK_SHIFT);
							buttonTimer.stop();
							buttonPauser.start();
						}
						else {
							emulator.keyPress(kv.getKeyCode());
							emulator.keyRelease(kv.getKeyCode());
							buttonTimer.stop();
							buttonPauser.start();
						}
					}
				}
				
				// Other buttons
				if(gamepad.isButtonPressed(lb)) {
					emulator.keyPress(KeyEvent.VK_BACK_SPACE);
					emulator.keyRelease(KeyEvent.VK_BACK_SPACE);
					buttonTimer.stop();
					buttonPauser.start();
				}
				else if(gamepad.isButtonPressed(rb)) {
					emulator.keyPress(KeyEvent.VK_SPACE);
					emulator.keyRelease(KeyEvent.VK_SPACE);
					buttonTimer.stop();
					buttonPauser.start();
				}
				else if(gamepad.isButtonPressed(back)) {
					toggleBools(SYMBOLS_BOOL);
					buttonTimer.stop();
					buttonPauser.start();
				}
			}
			else {
				if(gamepad.isButtonPressed(a)) {
					emulator.keyPress(KeyEvent.VK_TAB);
					emulator.keyRelease(KeyEvent.VK_TAB);
					buttonTimer.stop();
					buttonPauser.start();
				}
				else if(gamepad.isButtonPressed(b)) {
					emulator.keyPress(KeyEvent.VK_CONTROL);
					emulator.keyPress(KeyEvent.VK_W);
					emulator.keyRelease(KeyEvent.VK_CONTROL);
					emulator.keyRelease(KeyEvent.VK_W);
					buttonTimer.stop();
					buttonPauser.start();
				}
				else if(gamepad.isButtonPressed(x)) {
					emulator.keyPress(KeyEvent.VK_CONTROL);
					emulator.keyPress(KeyEvent.VK_T);
					emulator.keyRelease(KeyEvent.VK_CONTROL);
					emulator.keyRelease(KeyEvent.VK_T);
					buttonTimer.stop();
					buttonPauser.start();
				}
				else if(gamepad.isButtonPressed(y)) {
					emulator.keyPress(KeyEvent.VK_CONTROL);
					emulator.keyPress(KeyEvent.VK_TAB);
					emulator.keyRelease(KeyEvent.VK_CONTROL);
					emulator.keyRelease(KeyEvent.VK_TAB);
					buttonTimer.stop();
					buttonPauser.start();
				}
				else if(gamepad.isButtonPressed(lb)) {
					emulator.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					emulator.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					buttonTimer.stop();
					buttonPauser.start();
				}
				else if(gamepad.isButtonPressed(rb)) {
					emulator.mousePress(InputEvent.BUTTON3_DOWN_MASK);
					emulator.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
					buttonTimer.stop();
					buttonPauser.start();
				}
				else if(gamepad.isButtonPressed(back)) {
					emulator.keyPress(KeyEvent.VK_ALT);
					emulator.keyPress(KeyEvent.VK_TAB);
					emulator.keyRelease(KeyEvent.VK_ALT);
					emulator.keyRelease(KeyEvent.VK_TAB);
					buttonTimer.stop();
					buttonPauser.start();
				}
			}
		}
	}
	
	
	/**
	 * 
	 * StickButtonTimerActionListener handles events generated by the stickButtonTimer. 
	 * 
	 * @author Steven Raines
	 *
	 */
	private class StickButtonTimerActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(gamepad.isButtonPressed(ls)) {
				// Reset lowercase keyboard to true and swap frame visibility
				toggleBools(LOWERCASE_BOOL);
				frame.setVisible(!frame.isVisible());
				stickButtonTimer.stop();
				stickPauser.start();
			}
			else if(gamepad.isButtonPressed(rs)) {
				emulator.keyPress(KeyEvent.VK_TAB);
				emulator.keyRelease(KeyEvent.VK_TAB);
				stickButtonTimer.stop();
				stickPauser.start();
			}
		}
	}
	
	
	/**
	 * 
	 * DPadTimerActionListener handles events generated by the dPadTimer.
	 * 
	 * @author Steven Raines
	 *
	 */
	private class DPadTimerActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(gamepad.getPovX() == 1.0) {
				emulator.keyPress(KeyEvent.VK_RIGHT);
				emulator.keyRelease(KeyEvent.VK_RIGHT);
				dPadTimer.stop();
				dPadPauser.start();
			}
			else if(gamepad.getPovX() == -1.0) {
				emulator.keyPress(KeyEvent.VK_LEFT);
				emulator.keyRelease(KeyEvent.VK_LEFT);
				dPadTimer.stop();
				dPadPauser.start();
			}
			else if(gamepad.getPovY() == 1.0) {
				emulator.keyPress(KeyEvent.VK_DOWN);
				emulator.keyRelease(KeyEvent.VK_DOWN);
				dPadTimer.stop();
				dPadPauser.start();
			}
			else if(gamepad.getPovY() == -1.0) {
				emulator.keyPress(KeyEvent.VK_UP);
				emulator.keyRelease(KeyEvent.VK_UP);
				dPadTimer.stop();
				dPadPauser.start();
			}
		}
	}
	
	
	/**
	 * 
	 * AnalogueTimerActionListener handles events generated by the analogueTimer.
	 * 
	 * @author Steven Raines
	 *
	 */
	private class AnalogueTimerActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Only poll the gamepad for input in this timer,
			// since it will always be the fastest one
			gamepad.poll();
			
			if(frame.isVisible()) {
				// Get the frame's content pane
				contentPane = (JPanel) frame.getContentPane();
				
				
				// TODO replace all this keyboard direction code with
				// new getDirection method
				
				
				// Store left analogue stick x and y values in some variables
				float lx = gamepad.getAxisValue(leftXAxis);
				float ly = gamepad.getAxisValue(leftYAxis);
				
				// TODO handle dead zones
				// Handle left analogue stick input
				// Diagonals
				if(diff(lx, ly) <= 0.3 && Math.abs(lx) > 0.1 && Math.abs(ly) > 0.1) {					
					// NE
					if(lx >= 0.0 && ly <= 0.0) {
						contentPane.removeAll();
						
						if(isLowercaseActive) {
							contentPane.add(new Keyboard(lowercaseNorthEast));
						}
						else if(isUppercaseActive) {
							contentPane.add(new Keyboard(uppercaseNorthEast));
						}
						else {
							contentPane.add(new Keyboard(symbolsNorthEast));
						}
						
						contentPane.revalidate();
						contentPane.repaint();
						frame.pack();
					}
					// SE
					else if(lx >= 0.0 && ly >= 0.0) {
						contentPane.removeAll();

						if(isLowercaseActive) {
							contentPane.add(new Keyboard(lowercaseSouthEast));
						}
						else if(isUppercaseActive) {
							contentPane.add(new Keyboard(uppercaseSouthEast));
						}
						else {
							contentPane.add(new Keyboard(symbolsSouthEast));
						}
						
						contentPane.revalidate();
						contentPane.repaint();
						frame.pack();
					}
					// SW
					else if(lx <= 0.0 && ly >= 0.0) {
						contentPane.removeAll();

						if(isLowercaseActive) {
							contentPane.add(new Keyboard(lowercaseSouthWest));
						}
						else if(isUppercaseActive) {
							contentPane.add(new Keyboard(uppercaseSouthWest));
						}
						else {
							contentPane.add(new Keyboard(symbolsSouthWest));
						}
						
						contentPane.revalidate();
						contentPane.repaint();
						frame.pack();
					}
					// NW
					else if(lx <= 0.0 && ly <= 0.0) {
						contentPane.removeAll();

						if(isLowercaseActive) {
							contentPane.add(new Keyboard(lowercaseNorthWest));
						}
						else if(isUppercaseActive) {
							contentPane.add(new Keyboard(uppercaseNorthWest));
						}
						else {
							contentPane.add(new Keyboard(symbolsNorthWest));
						}
						
						contentPane.revalidate();
						contentPane.repaint();
						frame.pack();
					}
				}
				else {
					// NULL
					if(Math.abs(ly) <= 0.1 && Math.abs(lx) <= 0.1) {
						contentPane.removeAll();

						if(isLowercaseActive) {
							contentPane.add(new Keyboard(lowercaseNull));
						}
						else if(isUppercaseActive) {
							contentPane.add(new Keyboard(uppercaseNull));
						}
						else {
							contentPane.add(new Keyboard(symbolsNull));
						}
						
						contentPane.revalidate();
						contentPane.repaint();
						frame.pack();
					}
					// N
					else if(ly <= -0.9) {
						contentPane.removeAll();

						if(isLowercaseActive) {
							contentPane.add(new Keyboard(lowercaseNorth));
						}
						else if(isUppercaseActive) {
							contentPane.add(new Keyboard(uppercaseNorth));
						}
						else {
							contentPane.add(new Keyboard(symbolsNorth));
						}
						
						contentPane.revalidate();
						contentPane.repaint();
						frame.pack();
					}
					// S
					else if(ly >= 0.9) {
						contentPane.removeAll();

						if(isLowercaseActive) {
							contentPane.add(new Keyboard(lowercaseSouth));
						}
						else if(isUppercaseActive) {
							contentPane.add(new Keyboard(uppercaseSouth));
						}
						else {
							contentPane.add(new Keyboard(symbolsSouth));
						}
						
						contentPane.revalidate();
						contentPane.repaint();
						frame.pack();
					}
					// E
					else if(lx >= 0.9) {
						contentPane.removeAll();

						if(isLowercaseActive) {
							contentPane.add(new Keyboard(lowercaseEast));
						}
						else if(isUppercaseActive) {
							contentPane.add(new Keyboard(uppercaseEast));
						}
						else {
							contentPane.add(new Keyboard(symbolsEast));
						}
						
						contentPane.revalidate();
						contentPane.repaint();
						frame.pack();
					}
					// W
					else if(lx <= -0.9) {
						contentPane.removeAll();

						if(isLowercaseActive) {
							contentPane.add(new Keyboard(lowercaseWest));
						}
						else if(isUppercaseActive) {
							contentPane.add(new Keyboard(uppercaseWest));
						}
						else {
							contentPane.add(new Keyboard(symbolsWest));
						}
						
						contentPane.revalidate();
						contentPane.repaint();
						frame.pack();
					}
				}
				
				// Handle right analogue stick input
				// Store right X and Y analogue values in some variables
				// TODO handle dead zones
				float rx = gamepad.getAxisValue(rightXAxis);
				float ry = gamepad.getAxisValue(rightYAxis);
				int rxInt = (int) rx * 10;
				int ryInt = (int) ry * 10;
				
				if(Math.abs(rxInt) >= rightDeadZone || Math.abs(ryInt) >= rightDeadZone) {
					frame.setLocation(frame.getLocation().x + rxInt,
							frame.getLocation().y + ryInt);
				}
			}
			else {
				// Left analogue stick X axis
				if(gamepad.getAxisValue(leftXAxis) >= leftDeadZone) {
					emulator.keyPress(KeyEvent.VK_RIGHT);
					emulator.keyRelease(KeyEvent.VK_RIGHT);
				}
				else if(gamepad.getAxisValue(leftXAxis) <= -leftDeadZone) {
					emulator.keyPress(KeyEvent.VK_LEFT);
					emulator.keyRelease(KeyEvent.VK_LEFT);
				}
				
				// Left analogue stick Y axis
				if(gamepad.getAxisValue(leftYAxis) >= leftDeadZone) {
					emulator.mouseWheel(1);
				}
				else if(gamepad.getAxisValue(leftYAxis) <= -leftDeadZone) {
					emulator.mouseWheel(-1);
				}
				
				// Check if the right analogue stick is being moved
				if(Math.abs(gamepad.getAxisValue(rightXAxis)) >= rightDeadZone
						|| Math.abs(gamepad.getAxisValue(rightYAxis)) >= rightDeadZone) {
					// Update cursor info
					cursor = MouseInfo.getPointerInfo();
					cursorLocation = cursor.getLocation();
					
					// Calculate mouse movement				
					emulator.mouseMove((int)cursorLocation.getX()
							+ (int)(gamepad.getAxisValue(rightXAxis) 
						* Math.abs(gamepad.getAxisValue(rightXAxis)) * sensitivity),
							(int)cursorLocation.getY()
							+ (int)(gamepad.getAxisValue(rightYAxis) 
								* Math.abs(gamepad.getAxisValue(rightYAxis)) * sensitivity));
				}
			}
		}
	}
	
	
	/**
	 * 
	 * TriggerTimerActionListener handles events generated by the triggerTimer. 
	 * 
	 * @author Steven Raines
	 *
	 */
	private class TriggerTimerActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// If keyboard is currently visible
			if(Math.abs(gamepad.getAxisValue(triggerAxis)) >= triggerDeadZone
					&& frame.isVisible()) {
				// LT
				if(gamepad.getAxisValue(triggerAxis) >= triggerDeadZone
						&& gamepad.getAxisValue(triggerAxis) <= leftTrigger) {
					toggleBools(LOWERCASE_BOOL);
					triggerTimer.stop();
					triggerPauser.start();
				}
				// RT
				else {
					toggleBools(UPPERCASE_BOOL);
					triggerTimer.stop();
					triggerPauser.start();
				}
			}
			// If keyboard is not visible
			else if(Math.abs(gamepad.getAxisValue(triggerAxis)) >= triggerDeadZone) {
				// LT
				if(gamepad.getAxisValue(triggerAxis) >= triggerDeadZone
						&& gamepad.getAxisValue(triggerAxis) <= leftTrigger) {
					emulator.keyPress(KeyEvent.VK_ALT);
					emulator.keyPress(KeyEvent.VK_LEFT);
					emulator.keyRelease(KeyEvent.VK_ALT);
					emulator.keyRelease(KeyEvent.VK_LEFT);
					triggerTimer.stop();
					triggerPauser.start();
				}
				// RT
				else {
					emulator.keyPress(KeyEvent.VK_ALT);
					emulator.keyPress(KeyEvent.VK_RIGHT);
					emulator.keyRelease(KeyEvent.VK_ALT);
					emulator.keyRelease(KeyEvent.VK_RIGHT);
					triggerTimer.stop();
					triggerPauser.start();
				}
			}
		}
	}
}
