import java.awt.event.ActionListener;

import javax.swing.Timer;


/**
 * 
 * PauseTimer extends the javax.swing.Timer class. It adds a String variable to
 * denote the type of input this timer will be used to regulate, along with a
 * series of static, final String variables that are used to set the type variable.
 * 
 * When using a regular swing timer to get input from a gamepad and translate that
 * into mouse and keyboard input, what was meant to only be a single input can become
 * multiple inputs, i.e., pressing a button once to print the character 'a' may sometimes
 * print 'aa', and this is not as easy to address as simply changing the timer delay.
 * 
 * This class is used to temporarily "turn off" an input timer for a few ms after it is
 * pressed to ensure that what was meant to be a single input doesn't get doubled.
 * 
 * @author Steven Raines
 *
 */
public class PauseTimer extends Timer {
	public static final String BUTTON_TYPE = "button";
	public static final String STICK_BUTTON_TYPE = "stick";
	public static final String D_PAD_TYPE = "dPad";
	public static final String TRIGGER_TYPE = "trigger";
	private String type;
	

	/**
	 * The default swing timer super constructor.
	 * 
	 * @param delay The delay interval for the timer.
	 * @param listener The ActionListener that will watch for events generated by this timer.
	 */
	public PauseTimer(int delay, ActionListener listener) {
		super(delay, listener);
	}
	
	
	/**
	 * Specifies a delay, an action listener, and the type of input this timer will regulate.
	 * 
	 * @param delay The delay interval for the timer.
	 * @param listener The ActionListener that will watch for events generated by this timer.
	 * @param type The type of input this timer will regulate.
	 */
	public PauseTimer(int delay, ActionListener listener, String type) {
		super(delay, listener);
		this.type = type;
	}
	
	
	/**
	 * Mutator for the String type variable.
	 * 
	 * @param type The value to specify the type of input this timer will regulate.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
	/**
	 * Accessor for the String type variable.
	 * 
	 * @return The type of input this timer will regulate.
	 */
	public String getType() {
		return type;
	}
}
