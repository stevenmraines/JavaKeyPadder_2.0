import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.Timer;
import javax.swing.JFileChooser;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;


/**
 * @author Steven Raines
 * @version 2.0
 * @date 9/6/2016
 * 
 * Java Key Padder allows you to control the mouse and keyboard with
 * a USB gamepad. Upon starting the program, the user is prompted to select
 * a controller from a list of all currently plugged in gamepads. Then the
 * user is required to map out all of the buttons and axes of the controller.
 * Once controls are mapped, a third window allows the user to set preferences
 * such as dead zones and mouse movement sensitivity. After clicking the run
 * button, the user is finally able to control the mouse and keyboard with
 * the USB gamepad.
 * 
 * This program uses the Lightweight Java Game Libraries to get input from
 * a USB gamepad. The Robot class is used to emulate mouse and keyboard actions.
 * 
 */
public class JavaKeyPadder {
	// GUI components
	// Controller selection window
	private JFrame selectFrame = new JFrame("Java Key Padder 2.0");
	private JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
	private JLabel selectLabel = new JLabel("Select a controller:");
	private JComboBox<String> selectComboBox = new JComboBox<String>();
	private JButton selectButton = new JButton("Select");
	
	// Button mapping window
	private GridBagConstraints gbc = new GridBagConstraints();
	private JFrame mappingFrame = new JFrame("Java Key Padder 2.0");
	private JPanel mappingPanel = new JPanel(new GridBagLayout());
	private JPanel buttonInputPanel = new JPanel(new GridBagLayout());
	private JPanel dPadInputPanel = new JPanel(new GridBagLayout());
	private JPanel axesInputPanel = new JPanel(new GridBagLayout());
	private JPanel buttonPanel = new JPanel(new GridBagLayout());
	private JPanel axesPanel = new JPanel(new GridBagLayout());
	private JPanel triggerPanel = new JPanel(new GridBagLayout());
	private JPanel mapButtonPanel = new JPanel(new GridBagLayout());
	private JPanel buttonResultsPanel = new JPanel(new GridBagLayout());
	private JPanel dPadResultsPanel = new JPanel(new GridBagLayout());
	private JPanel axesResultsPanel = new JPanel(new GridBagLayout());
	private JTextField[] buttonInputFields = new JTextField[10];
	private JLabel[] buttonInputLabels = new JLabel[10];
	private JLabel dPadXInputLabel = new JLabel("Dpad X:");
	private JLabel dPadYInputLabel = new JLabel("Dpad Y:");
	private JTextField dPadXInputField = new JTextField();
	private JTextField dPadYInputField = new JTextField();
	private JLabel[] axesInputLabels = new JLabel[5];
	private JTextField[] axesInputFields = new JTextField[5];
	private JTextField[] buttonResultsFields = new JTextField[10];
	private JLabel[] buttonResultsLabels = new JLabel[10];
	private JLabel dPadXResultsLabel = new JLabel("Dpad X:");
	private JLabel dPadYResultsLabel = new JLabel("Dpad Y:");
	private JTextField dPadXResultsField = new JTextField();
	private JTextField dPadYResultsField = new JTextField();
	private JLabel[] axesResultsLabels = new JLabel[5];
	private JTextField[] axesResultsFields = new JTextField[5];
	private JButton[] mappingButtons = new JButton[18];
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem saveMenuItem = new JMenuItem("Save controller profile");
	private JMenuItem loadMenuItem = new JMenuItem("Load controller profile");
	private JMenuItem howToMenuItem = new JMenuItem("What's this?");
	private JFileChooser fileChooser = new JFileChooser("profiles/");
	private JFrame promptFrame = new JFrame();
	private JPanel promptPanel = new JPanel();
	private JLabel promptLabel = new JLabel("", SwingConstants.CENTER);
	
	// User settings window
	private JFrame settingsFrame = new JFrame("Java Key Padder 2.0");
	private JPanel settingsPanel = new JPanel(new GridBagLayout());
	private JPanel topSettingsPanel = new JPanel(new GridBagLayout());
	private JPanel middleSettingsPanel = new JPanel(new GridBagLayout());
	private JPanel bottomSettingsPanel = new JPanel(new GridBagLayout());
	private JPanel deadZonePanel = new JPanel(new GridBagLayout());
	private JPanel mouseSensitivityPanel = new JPanel(new GridBagLayout());
	private JLabel[] settingsLabels = new JLabel[4];
	private JSlider[] settingsSliders = new JSlider[4];
	private JTextField[] settingsFields = new JTextField[4];
	private JButton settingsConfirmButton = new JButton("Run");
	
	// Other instance variables
	private Dimension screenSize;
	private Controller gamepad;
	private ArrayList<Controller> controllers = new ArrayList<Controller>();
	private String[] controllerNames;
	private String selectedControllerName;
	private Timer inputTimer;
	private Timer textFieldTimer = new Timer(100, new TextFieldTimerListener());
	private Timer calibrateTimer = new Timer(100, new CalibrateTimerListener());
	private Timer promptTimer;
	private Map<String, Integer> buttonInputs = new HashMap<>();
	private Map<String, Float> dPadInputs = new HashMap<>();
	private Map<String, String> axesInputs = new HashMap<>();
	private ArrayList<String> unmappedInputs;
	private DecimalFormat df = new DecimalFormat("#.#");
	private Map<Integer, String> initialAxesValues = new HashMap<>();
	private ArrayList<Object> userSettings;
	private MouseAndKeyboardEmulator emulator;
	
	
	/**
	 * The no-arg constructor calls the initSelectGUI method
	 * to start the program with a prompt asking the user
	 * to select a controller.
	 */
	public JavaKeyPadder() {
		initSelectGUI();
	}
	
	
	/**
	 * The main method calls the initSelectGUI method to
	 * create the GUI which prompts the user to select a controller.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		new JavaKeyPadder();
	}
	
	
	/**
	 * A convenience method used to center a component, such as
	 * a JFrame, in the middle of the screen, both horizontally
	 * and vertically.
	 * 
	 * @param c The component to be centered.
	 * @return The centered coordinates for the component.
	 */
	private Point center(Component c) {
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point p = new Point(screenSize.width / 2 - c.getWidth() / 2,
				screenSize.height / 2 - c.getHeight() / 2);
		return p;
	}
	
	
	/**
	 * The checkControllers method is used to determine whether or not
	 * any valid controller devices have been detected.
	 * 
	 * @return A boolean value representing whether there are any
	 * available controllers.
	 */
	private boolean checkControllers() {
		if(Controllers.getControllerCount() == 0) {
			return false;
		}
		else {
			// Make sure the list only contains controllers
			for(int i = 0; i < Controllers.getControllerCount(); i++) {
				if(Controllers.getController(i).getName().contains("(Controller)")) {
					controllers.add(Controllers.getController(i));
				}
			}
			
			if(controllers.size() == 0) {
				return false;
			}
			else {
				return true;
			}
		}
	}
	
	
	/**
	* initSelectGUI initializes the GUI that allows the user
	* to select from a list of available controllers.
	*/
	private void initSelectGUI() {
		// Setup select GUI components
		selectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		selectFrame.setResizable(false);
		selectFrame.add(selectPanel);
		selectPanel.add(selectLabel, BorderLayout.NORTH);
		selectPanel.add(selectComboBox, BorderLayout.CENTER);
		selectPanel.add(selectButton, BorderLayout.SOUTH);
		
		// Add action listeners
		selectButton.addActionListener(new SelectButtonActionListener());
		
		// Get all available controllers
		try {
			Controllers.create();
		} catch(LWJGLException e) {
			e.printStackTrace();
		}
		
		// Check that there are any controllers available
		// If not, close the program
		if(!checkControllers()) {
			JOptionPane.showMessageDialog(null, "No available controllers detected."
					+ "\nPlease plug in a controller and restart the program.");
			System.exit(0);
		}
		
		// Add available controllers to selectComboBox
		controllerNames = new String[controllers.size()];
			
		for(int i = 0; i < controllers.size(); i++) {
			controllerNames[i] = controllers.get(i).getName();
			selectComboBox.addItem(controllerNames[i]);
		}
		
		for(int i = 0; i < controllers.size(); i++) {
			selectComboBox.setName(controllers.get(i).getName());
		}
		
		// Center selectFrame in the middle of the screen and make visible
		selectFrame.setSize(Math.max(250,
				selectComboBox.getPreferredSize().width + 20), 135);
		selectFrame.setLocation(center(selectFrame));
		selectFrame.setVisible(true);
	}
	
	
	/**
	 * The promptWait method pauses for 2 seconds before hiding
	 * a message and re-enabling the mapping GUI.
	 */
	private void promptWait(JFrame frame) {
		promptTimer = new Timer(2000, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				promptFrame.setVisible(false);
				frame.setEnabled(true);
				frame.requestFocus();
				promptTimer.stop();
			}
		});
		
		promptTimer.start();
	}
	
	
	/**
	 * initMappingGUI initializes the GUI which allows the user
	 * to map out all of the buttons and axes of the chosen controller.
	 */
	private void initMappingGUI() {		
		// Create a couple of hashmaps to store each button
		// and axis value with a String key representing
		// it's actual name.
		buttonInputs.put("A", null);
		buttonInputs.put("B", null);
		buttonInputs.put("X", null);
		buttonInputs.put("Y", null);
		buttonInputs.put("LB", null);
		buttonInputs.put("RB", null);
		buttonInputs.put("Back", null);
		buttonInputs.put("Start", null);
		buttonInputs.put("LS", null);
		buttonInputs.put("RS", null);
		dPadInputs.put("DX", null);
		dPadInputs.put("DY", null);
		axesInputs.put("LY", null);  // Values stored in axesInputs as "axisNumber:axisValue"
		axesInputs.put("LX", null);
		axesInputs.put("RY", null);
		axesInputs.put("RX", null);
		axesInputs.put("LT", null);
		
		// Dispose of the controller selection GUI
		selectFrame.dispose();
		
		// Add new components
		mappingFrame.setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		fileMenu.add(saveMenuItem);
		fileMenu.add(loadMenuItem);
		helpMenu.add(howToMenuItem);
		mappingFrame.add(mappingPanel);
		
		TitledBorder tb1 = new TitledBorder(new LineBorder(Color.gray),
				"Button live inputs");
		tb1.setTitleColor(Color.gray);
		TitledBorder tb2 = new TitledBorder(new LineBorder(Color.gray),
				"Dpad live inputs");
		tb2.setTitleColor(Color.gray);
		TitledBorder tb3 = new TitledBorder(new LineBorder(Color.gray),
				"Axes live inputs");
		tb3.setTitleColor(Color.gray);
		TitledBorder tb4 = new TitledBorder(new LineBorder(Color.gray), "Buttons");
		tb4.setTitleColor(Color.gray);
		TitledBorder tb5 = new TitledBorder(new LineBorder(Color.gray), "Axes");
		tb5.setTitleColor(Color.gray);
		TitledBorder tb6 = new TitledBorder(new LineBorder(Color.gray), "Triggers");
		tb6.setTitleColor(Color.gray);
		TitledBorder tb7 = new TitledBorder(new LineBorder(Color.gray),
				"Button mapping results");
		tb7.setTitleColor(Color.gray);
		TitledBorder tb8 = new TitledBorder(new LineBorder(Color.gray),
				"Dpad mapping results");
		tb8.setTitleColor(Color.gray);
		TitledBorder tb9 = new TitledBorder(new LineBorder(Color.gray),
				"Axes mapping results");
		tb9.setTitleColor(Color.gray);
		
		buttonInputPanel.setBorder(tb1);
		dPadInputPanel.setBorder(tb2);
		axesInputPanel.setBorder(tb3);
		buttonPanel.setBorder(tb4);
		axesPanel.setBorder(tb5);
		triggerPanel.setBorder(tb6);
		buttonResultsPanel.setBorder(tb7);
		dPadResultsPanel.setBorder(tb8);
		axesResultsPanel.setBorder(tb9);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 0;
		mappingPanel.add(buttonInputPanel, gbc);
		gbc.gridy = 1;
		mappingPanel.add(dPadInputPanel, gbc);
		gbc.gridy = 2;
		mappingPanel.add(axesInputPanel, gbc);
		gbc.gridx = 2;
		gbc.gridy = 0;
		mappingPanel.add(buttonResultsPanel, gbc);
		gbc.gridy = 1;
		mappingPanel.add(dPadResultsPanel, gbc);
		gbc.gridy = 2;
		mappingPanel.add(axesResultsPanel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 0;
		mappingPanel.add(buttonPanel, gbc);
		gbc.gridy = 1;
		mappingPanel.add(axesPanel, gbc);
		gbc.gridy = 2;
		mappingPanel.add(triggerPanel, gbc);
		gbc.gridy = 3;
		mappingPanel.add(mapButtonPanel, gbc);
		
		// Initialize mapping buttons
		mappingButtons[0] = new JButton("A");
		mappingButtons[1] = new JButton("B");
		mappingButtons[2] = new JButton("X");
		mappingButtons[3] = new JButton("Y");
		mappingButtons[4] = new JButton("LB");
		mappingButtons[5] = new JButton("RB");
		mappingButtons[6] = new JButton("Back");
		mappingButtons[7] = new JButton("Start");
		mappingButtons[8] = new JButton("LS");
		mappingButtons[9] = new JButton("RS");
		mappingButtons[10] = new JButton("Dpad X");
		mappingButtons[11] = new JButton("Dpad Y");
		mappingButtons[12] = new JButton("Left Y");
		mappingButtons[13] = new JButton("Left X");
		mappingButtons[14] = new JButton("Right Y");
		mappingButtons[15] = new JButton("Right X");
		mappingButtons[16] = new JButton("Triggers");
		mappingButtons[17] = new JButton("Confirm");
		
		// Add components to the live input panels
		gbc.insets = new Insets(5, 5, 5, 5);  // top, left, bottom, right
		gbc.gridx = 0;
		
		for(int i = 0; i < buttonInputLabels.length; i++) {
			gbc.gridy = i;
			buttonInputLabels[i] = new JLabel(i + ":");
			buttonInputPanel.add(buttonInputLabels[i], gbc);
		}
		
		gbc.gridx = 1;
		gbc.ipadx = 100;
		gbc.weightx = 1.0;
		
		for(int i = 0; i < buttonInputFields.length; i++) {
			gbc.gridy = i;
			buttonInputFields[i] = new JTextField();
			buttonInputPanel.add(buttonInputFields[i], gbc);
			buttonInputFields[i].setEditable(false);
		}
		
		gbc.ipadx = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		dPadInputPanel.add(dPadXInputLabel, gbc);
		gbc.gridy = 1;
		dPadInputPanel.add(dPadYInputLabel, gbc);
		gbc.ipadx = 100;
		gbc.weightx = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		dPadInputPanel.add(dPadXInputField, gbc);
		dPadXInputField.setEditable(false);
		gbc.gridy = 1;
		dPadInputPanel.add(dPadYInputField, gbc);
		dPadYInputField.setEditable(false);
		gbc.ipadx = 0;
		gbc.gridx = 0;
		gbc.weightx = 0.0;
		
		for(int i = 0; i < axesInputLabels.length; i++) {
			gbc.gridy = i;
			axesInputLabels[i] = new JLabel("Axis " + i + ":");
			axesInputPanel.add(axesInputLabels[i], gbc);
		}
		
		gbc.gridx = 1;
		gbc.ipadx = 100;
		gbc.weightx = 1.0;
		
		for(int i = 0; i < axesInputFields.length; i++) {
			gbc.gridy = i;
			axesInputFields[i] = new JTextField();
			axesInputPanel.add(axesInputFields[i], gbc);
			axesInputFields[i].setEditable(false);
		}
		
		// Add components to the results panels
		gbc.ipadx = 0;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		buttonResultsLabels[0] = new JLabel("A:");
		buttonResultsPanel.add(buttonResultsLabels[0], gbc);
		gbc.gridy = 1;
		buttonResultsLabels[1] = new JLabel("B:");
		buttonResultsPanel.add(buttonResultsLabels[1], gbc);
		gbc.gridy = 2;
		buttonResultsLabels[2] = new JLabel("X:");
		buttonResultsPanel.add(buttonResultsLabels[2], gbc);
		gbc.gridy = 3;
		buttonResultsLabels[3] = new JLabel("Y:");
		buttonResultsPanel.add(buttonResultsLabels[3], gbc);
		gbc.gridy = 4;
		buttonResultsLabels[4] = new JLabel("LB:");
		buttonResultsPanel.add(buttonResultsLabels[4], gbc);
		gbc.gridy = 5;
		buttonResultsLabels[5] = new JLabel("RB:");
		buttonResultsPanel.add(buttonResultsLabels[5], gbc);
		gbc.gridy = 6;
		buttonResultsLabels[6] = new JLabel("Back:");
		buttonResultsPanel.add(buttonResultsLabels[6], gbc);
		gbc.gridy = 7;
		buttonResultsLabels[7] = new JLabel("Start:");
		buttonResultsPanel.add(buttonResultsLabels[7], gbc);
		gbc.gridy = 8;
		buttonResultsLabels[8] = new JLabel("LS:");
		buttonResultsPanel.add(buttonResultsLabels[8], gbc);
		gbc.gridy = 9;
		buttonResultsLabels[9] = new JLabel("RS:");
		buttonResultsPanel.add(buttonResultsLabels[9], gbc);
		
		gbc.gridx = 1;
		gbc.ipadx = 70;
		gbc.weightx = 1.0;
		
		for(int i = 0; i < buttonResultsFields.length; i++) {
			gbc.gridy = i;
			buttonResultsFields[i] = new JTextField();
			buttonResultsPanel.add(buttonResultsFields[i], gbc);
			buttonResultsFields[i].setEditable(false);
		}
		
		gbc.ipadx = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		dPadResultsPanel.add(dPadXResultsLabel, gbc);
		gbc.gridy = 1;
		dPadResultsPanel.add(dPadYResultsLabel, gbc);
		gbc.ipadx = 100;
		gbc.weightx = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		dPadResultsPanel.add(dPadXResultsField, gbc);
		dPadXResultsField.setEditable(false);
		gbc.gridy = 1;
		dPadResultsPanel.add(dPadYResultsField, gbc);
		dPadYResultsField.setEditable(false);
		gbc.ipadx = 0;
		gbc.gridx = 0;
		gbc.weightx = 0.0;
		
		gbc.gridy = 0;
		axesResultsLabels[0] = new JLabel("LY axis:");
		axesResultsPanel.add(axesResultsLabels[0], gbc);
		gbc.gridy = 1;
		axesResultsLabels[1] = new JLabel("LX axis:");
		axesResultsPanel.add(axesResultsLabels[1], gbc);
		gbc.gridy = 2;
		axesResultsLabels[2] = new JLabel("RY axis:");
		axesResultsPanel.add(axesResultsLabels[2], gbc);
		gbc.gridy = 3;
		axesResultsLabels[3] = new JLabel("RX axis:");
		axesResultsPanel.add(axesResultsLabels[3], gbc);
		gbc.gridy = 4;
		axesResultsLabels[4] = new JLabel("Trigger axis:");
		axesResultsPanel.add(axesResultsLabels[4], gbc);
		
		gbc.gridx = 1;
		gbc.ipadx = 100;
		gbc.weightx = 1.0;
		
		for(int i = 0; i < axesResultsFields.length; i++) {
			gbc.gridy = i;
			axesResultsFields[i] = new JTextField();
			axesResultsPanel.add(axesResultsFields[i], gbc);
			axesResultsFields[i].setEditable(false);
		}
		
		// Start text field timer
		textFieldTimer.start();
		
		// Add buttons to the button panel
		// Add items in left to right, top to bottom order
		gbc.weightx = 0.0;
		gbc.ipadx = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		buttonPanel.add(mappingButtons[4], gbc);  // LB
		gbc.gridx = 5;
		buttonPanel.add(mappingButtons[5], gbc);  // RB
		gbc.gridx = 5;
		gbc.gridy = 1;
		buttonPanel.add(mappingButtons[3], gbc);  // Y
		gbc.gridx = 1;
		gbc.gridy = 2;
		buttonPanel.add(mappingButtons[10], gbc);  // DX
		gbc.gridx = 2;
		buttonPanel.add(mappingButtons[6], gbc);  // Back
		gbc.gridx = 3;
		buttonPanel.add(mappingButtons[7], gbc);  // Start
		gbc.gridx = 4;
		buttonPanel.add(mappingButtons[2], gbc);  // X
		gbc.gridx = 6;
		buttonPanel.add(mappingButtons[1], gbc);  // B
		gbc.gridx = 0;
		gbc.gridy = 3;
		buttonPanel.add(mappingButtons[11], gbc);  // DY
		gbc.gridx = 5;
		buttonPanel.add(mappingButtons[0], gbc);  // A
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.ipady = 20;
		buttonPanel.add(mappingButtons[8], gbc);  // LS
		gbc.gridx = 3;
		buttonPanel.add(mappingButtons[9], gbc);  // RS
		
		// Add buttons to the axes panel
		gbc.insets = new Insets(5, 0, 5, 50);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipady = 0;
		axesPanel.add(mappingButtons[13], gbc);  // LX
		gbc.insets = new Insets(5, 0, 5, 50);
		gbc.gridx = 0;
		gbc.gridy = 1;
		axesPanel.add(mappingButtons[12], gbc);  // LY
		gbc.insets = new Insets(5, 50, 5, 0);
		gbc.gridx = 1;
		gbc.gridy = 0;
		axesPanel.add(mappingButtons[15], gbc);  // RX
		gbc.insets = new Insets(5, 50, 5, 0);
		gbc.gridx = 1;
		gbc.gridy = 1;
		axesPanel.add(mappingButtons[14], gbc);  // RY
		
		// Add buttons to the trigger panel
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 20;
		gbc.ipady = 20;
		triggerPanel.add(mappingButtons[16], gbc);
		
		// Add buttons to the map button panel
		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.ipadx = 0;
		gbc.ipady = 20;
		mapButtonPanel.add(mappingButtons[17], gbc);
		
		// Add action listeners
		for(JButton b: mappingButtons) {
			b.addActionListener(new MappingButtonActionListener());
		}
		saveMenuItem.addActionListener(new MappingButtonActionListener());
		loadMenuItem.addActionListener(new MappingButtonActionListener());
		howToMenuItem.addActionListener(new MappingButtonActionListener());
		
		// Center JFrame in the middle of the screen
		mappingFrame.pack();
		mappingFrame.setLocation(center(mappingFrame));
		mappingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mappingFrame.setVisible(true);
		
		// Initialize the prompt window
		promptFrame.add(promptPanel);
		promptPanel.add(promptLabel);
		promptFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		promptFrame.setUndecorated(true);
		promptLabel.setFont(new Font(promptLabel.getFont().getFontName(), Font.BOLD, 20));
		promptLabel.setForeground(Color.lightGray);
		promptPanel.setOpaque(false);
		promptFrame.getContentPane().setBackground(Color.darkGray);		
		promptLabel.setText("<html>Please rotate both analogue sticks in a "
				+ "full circle, <br>and pull either the left or right trigger to "
				+ "<br>calibrate the axes of your controller.</html>");
		promptFrame.pack();
		promptFrame.setLocation(center(promptFrame));
		promptFrame.setVisible(true);
		promptFrame.setAlwaysOnTop(true);
		mappingFrame.setEnabled(false);
		
		// Grab initial axes values and start calibration
		gamepad.poll();
		
		for(int i = 0; i < gamepad.getAxisCount(); i++) {
			initialAxesValues.put(i, gamepad.getAxisValue(i) + ":NULL");
		}
		
		calibrateTimer.start();
	}
	
	
	/**
	 * initSettingsGUI initializes the GUI which allows the user
	 * to set their preferences for things like dead zones and mouse sensitivity.
	 */
	// TODO figure out settings GUI
	private void initSettingsGUI() {
		// Dispose of the mapping GUI
		mappingFrame.dispose();
		
		// Initialize emulator object
		emulator = new MouseAndKeyboardEmulator(buttonInputs, dPadInputs,
				axesInputs, gamepad);
		
		// Initialize settings GUI components
		settingsFrame.add(settingsPanel);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(6, 10, 10, 10);
		settingsPanel.add(topSettingsPanel, gbc);
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 10, 0, 10);
		settingsPanel.add(middleSettingsPanel, gbc);
		gbc.gridy = 2;
		settingsPanel.add(bottomSettingsPanel, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		topSettingsPanel.add(deadZonePanel, gbc);
		gbc.gridy = 1;
		middleSettingsPanel.add(mouseSensitivityPanel, gbc);
		
		settingsConfirmButton.addActionListener(new SettingsButtonActionListener());
		
		TitledBorder tb1 = new TitledBorder(new LineBorder(Color.gray), "Dead zones");
		tb1.setTitleColor(Color.gray);
		TitledBorder tb2 = new TitledBorder(new LineBorder(Color.gray), "Mouse sensitivity");
		tb2.setTitleColor(Color.gray);
		
		deadZonePanel.setBorder(tb1);
		mouseSensitivityPanel.setBorder(tb2);
		
		for(int i = 0; i < settingsLabels.length; i++) {
			settingsLabels[i] = new JLabel();
		}
		
		settingsLabels[0].setText("Left stick:");
		settingsLabels[1].setText("Right stick:");
		settingsLabels[2].setText("Triggers:");
		settingsLabels[3].setText("Sensitivity:");
		
		settingsSliders[0] = new JSlider(SwingConstants.HORIZONTAL, 1, 9, 1);
		settingsSliders[0].setValue(2);
		settingsSliders[1] = new JSlider(SwingConstants.HORIZONTAL, 1, 9, 1);
		settingsSliders[1].setValue(2);
		settingsSliders[2] = new JSlider(SwingConstants.HORIZONTAL, 1, 9, 1);
		settingsSliders[2].setValue(2);
		settingsSliders[3] = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 1);
		settingsSliders[3].setValue(5);
		
		for(int i = 0; i < settingsSliders.length; i++) {
			settingsSliders[i].setSnapToTicks(true);
		}

		for(int i = 0; i < settingsFields.length; i++) {
			settingsFields[i] = new JTextField();
			settingsFields[i].setEditable(false);
		}
		
		gbc.gridx = 0;
		for(int i = 0; i < 3; i++) {
			gbc.gridy = i;
			deadZonePanel.add(settingsLabels[i], gbc);
			gbc.gridx = 1;
			deadZonePanel.add(settingsSliders[i], gbc);
			gbc.gridx = 2;
			gbc.ipadx = 20;
			deadZonePanel.add(settingsFields[i], gbc);
			gbc.gridx = 0;
			gbc.ipadx = 0;
		}
		
		gbc.gridy = 0;
		mouseSensitivityPanel.add(settingsLabels[3], gbc);
		gbc.gridx = 1;
		mouseSensitivityPanel.add(settingsSliders[3], gbc);
		gbc.gridx = 2;
		gbc.ipadx = 20;
		mouseSensitivityPanel.add(settingsFields[3], gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 10;
		bottomSettingsPanel.add(settingsConfirmButton, gbc);
		
		settingsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		settingsFrame.pack();
		settingsFrame.setLocation(center(settingsFrame));
		settingsFrame.setVisible(true);
	}
	
	
	/**
	 * The run method hides the settings GUI and activates
	 * gamepad control of the mouse and keyboard.
	 */

	private void run() {
		// Get rid of the settings frame
		settingsFrame.dispose();
		
		// Set user preferences
		userSettings = new ArrayList<Object>();
		userSettings.add((float) settingsSliders[0].getValue() / 10);  // LS dead zone
		userSettings.add((float) settingsSliders[1].getValue() / 10);  // RS dead zone
		userSettings.add((float) settingsSliders[2].getValue() / 10);  // Trigger dead zone
		userSettings.add(settingsSliders[3].getValue());  // Mouse sensitivity
		emulator.updateSettings(userSettings);
		
		// Give control of mouse and keyboard to the gamepad
		// if that hasn't been done already
		if(!emulator.isRunning()) {
			emulator.start();
		}
	}
	
	
	/**
	 * @author Steven Raines
	 * 
	 * The SelectButtonActionListener class handles events generated
	 * by the selectButton object. Clicking this button removes all
	 * components from the JFrame and replaces them with components
	 * for the controller mapping window.
	 *
	 */
	private class SelectButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Set the gamepad object to the chosen controller
			selectedControllerName = (String)selectComboBox.getSelectedItem();
			
			for(Controller c: controllers) {
				if(c.getName().equalsIgnoreCase(selectedControllerName)) {
					gamepad = c;
				}
			}
			
			if(gamepad != null) {
				// Dispose of controller selection GUI components and
				// go to the button mapping GUI
				initMappingGUI();
			}
			else {
				JOptionPane.showMessageDialog(null, "No controller selected.");
			}
		}
	}
	
	
	/**
	 * @author Steven Raines
	 * 
	 * The CalibrateTimerListener displays a message to the user to
	 * calibrate the axes of their controller by moving both analogue
	 * sticks and pulling either the left or right trigger. The mapping
	 * interface will not be enabled until this has been done, because
	 * mapping the axes before calibration could throw off the results. 
	 *
	 */
	private class CalibrateTimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			gamepad.poll();
			boolean isCalibrated = true;
			
			for(int i = 0; i < gamepad.getAxisCount(); i++) {
				String[] value = initialAxesValues.get(i).split(":");
				if(value[1].equalsIgnoreCase("null")
						&& gamepad.getAxisValue(i) != Float.parseFloat(value[0])) {
					initialAxesValues.replace(i, value[0] + ":"
						+ gamepad.getAxisValue(i));
				}
			}
			
			for(int i = 0; i < initialAxesValues.size(); i++) {
				String[] value = initialAxesValues.get(i).split(":");
				if(value[1].equalsIgnoreCase("null")) {
					isCalibrated = false;
				}
			}
			
			if(isCalibrated) {
				calibrateTimer.stop();
				promptLabel.setText("Calibration complete!");
				promptFrame.pack();
				promptFrame.setLocation(center(promptFrame));
				promptWait(mappingFrame);
			}
		}
	}
	
	
	/**
	 * @author Steven Raines
	 * 
	 * TextFieldTimerListener updates the text fields used to display
	 * live input data from the controller in the mapping GUI.
	 *
	 */
	private class TextFieldTimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			gamepad.poll();
			
			// Set live input text fields
			// Button inputs
			for(int i = 0; i < buttonInputFields.length; i++) {
				if(gamepad.isButtonPressed(i)) {
					buttonInputFields[i].setText("Button pressed");
				}
				else {
					buttonInputFields[i].setText("");
				}
			}
			
			// Dpad
			dPadXInputField.setText("" + gamepad.getPovX());
			dPadYInputField.setText("" + gamepad.getPovY());
			
			// Axes
			for(int i = 0; i < axesInputFields.length; i++) {
				axesInputFields[i].setText("" + gamepad.getAxisValue(i));
			}
		}
	}
	
	
	/**
	 * @author Steven Raines
	 * 
	 * The MappingButtonActionListener class is an action listener for
	 * the mapping GUI. It handles events for all buttons and menu items.
	 *
	 */
	private class MappingButtonActionListener implements ActionListener {
		
		/**
		 * promptForInput is used to display the prompts and subsequent success
		 * messages that the user will see after clicking on one of the JButtons
		 * on the mapping GUI. It is also responsible for setting the newly mapped
		 * values to their appropriate text fields on the results panel.
		 * 
		 * @param promptMessage The message prompting the user for input.
		 * @param successMessage The message shown when input has been registered.
		 * @param inputKey Denotes the specific button or axis being mapped.
		 * @param inputType Specifies whether the input being mapped is a button,
		 * dPad button, or axis.
		 */
		private void promptForInput(String promptMessage, String successMessage,
				String inputKey, String inputType) {
			promptLabel.setText(promptMessage);
			promptFrame.pack();
			promptFrame.setLocation(center(promptFrame));
			promptFrame.setVisible(true);
			promptFrame.setAlwaysOnTop(true);
			mappingFrame.setEnabled(false);
			
			inputTimer = new Timer(10, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					gamepad.poll();
					
					// Handle button inputs
					if(inputType.equalsIgnoreCase("button")) {
						for(int i = 0; i < gamepad.getButtonCount(); i++) {
							if(gamepad.isButtonPressed(i)) {
								buttonInputs.replace(inputKey, i);
								
								if(inputKey.equals("A")) {
									buttonResultsFields[0].setText("Button " + i);
								}
								if(inputKey.equals("B")) {
									buttonResultsFields[1].setText("Button " + i);
								}
								if(inputKey.equals("X")) {
									buttonResultsFields[2].setText("Button " + i);
								}
								if(inputKey.equals("Y")) {
									buttonResultsFields[3].setText("Button " + i);
								}
								if(inputKey.equals("LB")) {
									buttonResultsFields[4].setText("Button " + i);
								}
								if(inputKey.equals("RB")) {
									buttonResultsFields[5].setText("Button " + i);
								}
								if(inputKey.equals("Back")) {
									buttonResultsFields[6].setText("Button " + i);
								}
								if(inputKey.equals("Start")) {
									buttonResultsFields[7].setText("Button " + i);
								}
								if(inputKey.equals("LS")) {
									buttonResultsFields[8].setText("Button " + i);
								}
								if(inputKey.equals("RS")) {
									buttonResultsFields[9].setText("Button " + i);
								}
								
								swapMessage();
								break;
							}
						}
					}
					
					// Handle Dpad inputs
					else if(inputType.equalsIgnoreCase("dpad")) {
						// Dpad X
						if(Math.abs(gamepad.getPovX()) == 1.0) {
							dPadInputs.replace(inputKey, gamepad.getPovX());
							dPadXResultsField.setText("" + gamepad.getPovX());
							swapMessage();
						}
						
						// Dpad Y
						else if(Math.abs(gamepad.getPovY()) == 1.0) {
							dPadInputs.replace(inputKey, gamepad.getPovY());
							dPadYResultsField.setText("" + gamepad.getPovY());
							swapMessage();
						}
					}
					
					// Handle axes inputs
					else {
						for(int i = 0; i < gamepad.getAxisCount(); i++) {
							if(Math.abs(gamepad.getAxisValue(i)) >= 0.9) {
								float formattedValue = Float.parseFloat(
										df.format((double)gamepad.getAxisValue(i)));
								float roundedValue = Math.round(formattedValue);
								axesInputs.replace(inputKey, i + ":" + roundedValue);
								axesResultsFields[i].setText("" + roundedValue);
								swapMessage();
								break;
							}
						}
					}
				}
				
				// Ends timer and replaces promptMessage with successMessage
				private void swapMessage() {
					inputTimer.stop();
					promptLabel.setText(successMessage);
					promptFrame.pack();
					promptFrame.setLocation(center(promptFrame));
					promptWait(mappingFrame);
				}
			});
			
			inputTimer.start();
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// Figure out the index of the JButton that generated the event
			// Initialize index to -1 so that the switch statements are not
			// triggered when the menu buttons are clicked
			int index = -1;
			
			for(int i = 0; i < mappingButtons.length; i++) {
				if(mappingButtons[i].equals(e.getSource())) {
					index = i;
				}
			}
			
			// When a button on the GUI is clicked, a message will display
			// telling the user to press the button or move the stick
			// The message will not go away until the user gives some input
			// The mapping interface will also be locked until the user gives
			// some controller input
			switch(index) {
				case 0:
					promptForInput("Please press the A button",
							"A button successfully mapped!", "A", "button");
					break;
				case 1:
					promptForInput("Please press the B button.",
							"B button successfully mapped!", "B", "button");
					break;
				case 2:
					promptForInput("Please press the X button.",
							"X button successfully mapped!", "X", "button");
					break;
				case 3:
					promptForInput("Please press the Y button.",
							"Y button successfully mapped!", "Y", "button");
					break;
				case 4:
					promptForInput("Please press the left bumper button.",
							"Left bumper button successfully mapped!", "LB", "button");
					break;
				case 5:
					promptForInput("Please press the right bumper button.",
							"Right bumper button successfully mapped!", "RB", "button");
					break;
				case 6:
					promptForInput("Please press the back button.",
							"Back button successfully mapped!", "Back", "button");
					break;
				case 7:
					promptForInput("Please press the start button.",
							"Start button successfully mapped!", "Start", "button");
					break;
				case 8:
					promptForInput("Please press the left stick button.",
							"Left stick button successfully mapped!", "LS", "button");
					break;
				case 9:
					promptForInput("Please press the right stick button.",
							"Right stick button successfully mapped!", "RS", "button");
					break;
				case 10:
					promptForInput("Please press the right Dpad button.",
							"Dpad X axis successfully mapped!", "DX", "dpad");
					break;
				case 11:
					promptForInput("Please press the down Dpad button.",
							"Dpad Y axis successfully mapped!", "DY", "dpad");
					break;
				case 12:
					promptForInput("Please push the left stick all the way down",
							"Left Y axis successfully mapped!", "LY", "axis");
					break;
				case 13:
					promptForInput("Please push the left stick all the way to the right",
							"Left X axis successfully mapped!", "LX", "axis");
					break;
				case 14:
					promptForInput("Please push the right stick all the way down",
							"Right Y axis successfully mapped!", "RY", "axis");
					break;
				case 15:
					promptForInput("Please push the right stick all the way to the right",
							"Right X axis successfully mapped!", "RX", "axis");
					break;
				case 16:
					promptForInput("Please pull the left trigger.",
							"Triggers successfully mapped!", "LT", "axis");
					break;
				case 17:
					ArrayList<String> mapInputCheck = checkInputs();
					
					if(mapInputCheck.size() == 0) {
						initSettingsGUI();
					}
					else if(mapInputCheck.size() == 17) {
						JOptionPane.showMessageDialog(null, "You have not mapped any inputs.");
					}
					else {
						String temp = "";
						
						for(String s: unmappedInputs) {
							temp += "\n" + s;
						}
						
						JOptionPane.showMessageDialog(null, "The following controls"
								+ " have not yet been mapped:\n".concat(temp));
					}
					
					break;
			}
			
			if(e.getSource().equals(saveMenuItem)) {
				ArrayList<String> saveInputCheck = checkInputs();
				
				if(saveInputCheck.size() == 0) {
					// Show the file saving menu
					int choice = fileChooser.showSaveDialog(mappingFrame);
					
					if(choice == JFileChooser.APPROVE_OPTION) {
						try {
							// Write map details to a file
							String fileName = fileChooser.getSelectedFile().getName();
							
							// Check file extension
							if(!fileName.contains(".jkp")) {
								fileName += ".jkp";
							}
							
							PrintWriter writer = new PrintWriter("profiles/"
									+ fileName, "UTF-8");
							writer.println(selectedControllerName);
							writer.println(buttonInputs.get("A"));
							writer.println(buttonInputs.get("B"));
							writer.println(buttonInputs.get("X"));
							writer.println(buttonInputs.get("Y"));
							writer.println(buttonInputs.get("LB"));
							writer.println(buttonInputs.get("RB"));
							writer.println(buttonInputs.get("Back"));
							writer.println(buttonInputs.get("Start"));
							writer.println(buttonInputs.get("LS"));
							writer.println(buttonInputs.get("RS"));
							writer.println(dPadInputs.get("DX"));
							writer.println(dPadInputs.get("DY"));
							writer.println(axesInputs.get("LY"));
							writer.println(axesInputs.get("LX"));
							writer.println(axesInputs.get("RY"));
							writer.println(axesInputs.get("RX"));
							writer.println(axesInputs.get("LT"));
							writer.close();
						} catch (FileNotFoundException e2) {
							e2.printStackTrace();
						} catch (UnsupportedEncodingException e2) {
							e2.printStackTrace();
						}
					}
					else if(choice == JFileChooser.ERROR_OPTION) {
						JOptionPane.showMessageDialog(null, "File not saved properly.");
					}
				}
				else if(saveInputCheck.size() == 20) {
					JOptionPane.showMessageDialog(null, "You have not mapped any inputs.");
				}
				else {
					String temp = "";
					
					for(String s: unmappedInputs) {
						temp += "\n" + s;
					}
					
					JOptionPane.showMessageDialog(null, "The following controls"
							+ " have not yet been mapped:\n".concat(temp));
				}
			}
			
			if(e.getSource().equals(loadMenuItem)) {
				fileChooser.showOpenDialog(mappingFrame);
				Path path = FileSystems.getDefault().getPath("profiles\\",
						fileChooser.getSelectedFile().getName());
				List<String> contents;
				
				try {
					// Get file contents
					contents = Files.readAllLines(path, Charset.defaultCharset());
					
					if(!selectedControllerName.equalsIgnoreCase(contents.get(0))) {
						JOptionPane.showMessageDialog(null, "Warning: it's possible "
								+ "this profile was created using a different "
								+ "controller from the one currently chosen.");
					}
					
					// Set inputs
					buttonInputs.replace("A", Integer.parseInt(contents.get(1)));
					buttonInputs.replace("B", Integer.parseInt(contents.get(2)));
					buttonInputs.replace("X", Integer.parseInt(contents.get(3)));
					buttonInputs.replace("Y", Integer.parseInt(contents.get(4)));
					buttonInputs.replace("LB", Integer.parseInt(contents.get(5)));
					buttonInputs.replace("RB", Integer.parseInt(contents.get(6)));
					buttonInputs.replace("Back", Integer.parseInt(contents.get(7)));
					buttonInputs.replace("Start", Integer.parseInt(contents.get(8)));
					buttonInputs.replace("LS", Integer.parseInt(contents.get(9)));
					buttonInputs.replace("RS", Integer.parseInt(contents.get(10)));
					dPadInputs.replace("DX", Float.parseFloat(contents.get(11)));
					dPadInputs.replace("DY", Float.parseFloat(contents.get(12)));
					axesInputs.replace("LY", contents.get(13));
					axesInputs.replace("LX", contents.get(14));
					axesInputs.replace("RY", contents.get(15));
					axesInputs.replace("RX", contents.get(16));
					axesInputs.replace("LT", contents.get(17));
					
					// Set results fields
					buttonResultsFields[0].setText("" + contents.get(1));
					buttonResultsFields[1].setText("" + contents.get(2));
					buttonResultsFields[2].setText("" + contents.get(3));
					buttonResultsFields[3].setText("" + contents.get(4));
					buttonResultsFields[4].setText("" + contents.get(5));
					buttonResultsFields[5].setText("" + contents.get(6));
					buttonResultsFields[6].setText("" + contents.get(7));
					buttonResultsFields[7].setText("" + contents.get(8));
					buttonResultsFields[8].setText("" + contents.get(9));
					buttonResultsFields[9].setText("" + contents.get(10));
					dPadXResultsField.setText("" + contents.get(11));
					dPadYResultsField.setText("" + contents.get(12));
					axesResultsFields[0].setText("" + contents.get(13).split(":")[1]);
					axesResultsFields[1].setText("" + contents.get(14).split(":")[1]);
					axesResultsFields[2].setText("" + contents.get(15).split(":")[1]);
					axesResultsFields[3].setText("" + contents.get(16).split(":")[1]);
					axesResultsFields[4].setText("" + contents.get(17).split(":")[1]);
					
					JOptionPane.showMessageDialog(null, "Controller inputs mapped.");
				} catch (IOException e3) {
					e3.printStackTrace();
				}
			}
			
			if(e.getSource() == howToMenuItem) {
				JOptionPane.showMessageDialog(null, "USB gamepads often "
						+ "give nondescript names to controller buttons and axes, such"
						+ " as 'Button 0.' \n\nThis interface is necessary to determine the"
						+ " actual identity of each button, as well as axes orientations."
						+ "\n\nTo map a button or axis click on the appropriate button"
						+ " on the interface, then follow the instructions in the prompt"
						+ " that appears on the screen. \n\nOnce you have mapped all of the"
						+ " inputs, you may save them using the 'File' menu at the top of"
						+ " the interface. \n\nNext time you start the program with that"
						+ " controller, you need only load the appropriate profile.");
			}
		}
		
		/**
		 * The checkInputs method returns a list of all inputs that have yet
		 * to be mapped by the user. If there are no unmapped inputs, an
		 * empty list will be returned.
		 * 
		 * @return A list containing all unmapped inputs.
		 */
		private ArrayList<String> checkInputs() {
			// Make sure all inputs have been mapped
			unmappedInputs = new ArrayList<String>();

			if(buttonInputs.get("A") == null) { unmappedInputs.add("A button"); }
			if(buttonInputs.get("B") == null) { unmappedInputs.add("B button"); }
			if(buttonInputs.get("X") == null) { unmappedInputs.add("X button"); }
			if(buttonInputs.get("Y") == null) { unmappedInputs.add("Y button"); }
			if(buttonInputs.get("LB") == null) { unmappedInputs.add("Left bumper"); }
			if(buttonInputs.get("RB") == null) { unmappedInputs.add("Right bumper"); }
			if(buttonInputs.get("Back") == null) { unmappedInputs.add("Back"); }
			if(buttonInputs.get("Start") == null) { unmappedInputs.add("Start"); }
			if(buttonInputs.get("LS") == null) { unmappedInputs.add("Left stick button"); }
			if(buttonInputs.get("RS") == null) { unmappedInputs.add("Right stick button"); }
			if(dPadInputs.get("DX") == null) { unmappedInputs.add("Dpad X axis"); }
			if(dPadInputs.get("DY") == null) { unmappedInputs.add("Dpad Y axis"); }
			if(axesInputs.get("LY") == null) { unmappedInputs.add("Left Y axis"); }
			if(axesInputs.get("LX") == null) { unmappedInputs.add("Left X axis"); }
			if(axesInputs.get("RY") == null) { unmappedInputs.add("Right Y axis"); }
			if(axesInputs.get("RX") == null) { unmappedInputs.add("Right X axis"); }
			if(axesInputs.get("LT") == null) { unmappedInputs.add("Triggers"); }
			
			return unmappedInputs;
		}
	}
	
	
	/**
	 * @author Steven Raines
	 * 
	 * The SettingsButtonActionListener handles events generated by the user
	 * settings GUI components.
	 *
	 */
	private class SettingsButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == settingsConfirmButton) {
				run();
			}
		}
	}
}
