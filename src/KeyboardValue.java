/**
 * 
 * The KeyboardValue class simply stores an integer value representing
 * the chosen keyboard character for this object, along with a boolean
 * which dictates whether the shift key must be used to print this
 * character to the screen. Values for the int variable keyCode should
 * be passed using the static final KeyEvent virtual key variables such as
 * KeyEvent.VK_BACK_SLASH.
 * 
 * @author Steven Raines
 *
 */
public class KeyboardValue {
	private int keyCode;
	private boolean needsShift;
	
	
	public KeyboardValue(int keyCode, boolean needsShift) {
		this.keyCode = keyCode;
		this.needsShift = needsShift;
	}

	
	/**
	 * Accessor method for the keyCode instance variable.
	 * 
	 * @return The value of the instance variable keyCode.
	 */
	public int getKeyCode() {
		return keyCode;
	}

	
	/**
	 * Mutator method for the keyCode instance variable.
	 * 
	 * @param keyCode The value to set keyCode to.
	 */
	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}
	
	
	/**
	 * Accessor for the needsShift instance variable.
	 * 
	 * @return The value of the needsShift instance variable.
	 */
	public boolean needsShift() {
		return needsShift;
	}
	
	
	/**
	 * Mutator method for the needsShift instance variable.
	 * 
	 * @param needsShift The value to set needsShift to.
	 */
	public void setNeedsShift(boolean needsShift) {
		this.needsShift = needsShift;
	}
}
