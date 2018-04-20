import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddQueueFrame extends JFrame implements ActionListener, Validate {

	private JRadioButton[] schedulingType = new JRadioButton[4];
	private JRadioButton[] preemptiness = new JRadioButton[2];
	private JButton createQueueButton, cancel;
	private JTextField quantumTime;
	private JLabel errorLabel;
	private Frame frame;

	private String schedulingAlgo;
	private boolean isPreemptive;
	private int noOfQueues = 0;

	public AddQueueFrame(Frame frame) {
		this.frame = frame;
		this.addWindowListener(new Window(this));
		setLayout(new BorderLayout());
		//setUndecorated(true);
		setSize(300,150);
		setResizable(false);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel config = new JPanel();
		JPanel buttons = new JPanel();
		JPanel mainPanel = new JPanel();

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(config, BorderLayout.CENTER);
		mainPanel.add(buttons, BorderLayout.SOUTH);
		config.setLayout(new FlowLayout(FlowLayout.CENTER));

		JLabel quantumLabel = new JLabel("Quantum Time: ");
		quantumTime = new JTextField(2);

		String[] radio = {"FCFS", "SRTF", "Priority", "Round Robin"};
		ButtonGroup bg = new ButtonGroup();
		for(int x = 0; x < schedulingType.length; x++) {
			bg.add(schedulingType[x] = new JRadioButton(radio[x]));
			config.add(schedulingType[x]);
		}

		String[] preRadio = {"Preemptive", "Non-Preemptive"};
		ButtonGroup bg2 = new ButtonGroup();
		for(int a = 0; a < preemptiness.length; a++) {
			bg2.add(preemptiness[a] = new JRadioButton(preRadio[a]));
			config.add(preemptiness[a]);
		}

		errorLabel = new JLabel();
		errorLabel.setVisible(false);

		createQueueButton = new JButton("Create Queue");
		createQueueButton.addActionListener(this);

		cancel = new JButton("Cancel");
		cancel.addActionListener(this);

		config.add(quantumLabel);
		config.add(quantumTime);

		buttons.add(errorLabel);
		buttons.add(createQueueButton);
		buttons.add(cancel);

		add(mainPanel, BorderLayout.CENTER);
	}

	public boolean isComplete() {
		if(quantumTime.getText().equals("") || quantumTime.getText() == null) {
			errorLabel.setText("Indicate quantum time.");
			return false;
		}
		try {
			int check = Integer.parseInt(quantumTime.getText());
			if(check <= 0) {
				errorLabel.setText("QTime must be > 0.");
				return false;
			}
		} catch(Exception e) {
			errorLabel.setText("Invalid time.");
			return false;
		}
		for(int x = 0; x < schedulingType.length; x++) {
			if(schedulingType[x].isSelected()) {
				schedulingAlgo = schedulingType[x].getText();
			}
		}
		for(int x = 0; x < preemptiness.length; x++) {
			if(preemptiness[x].isSelected()) {
				isPreemptive = preemptiness[x].getText().equals("Preemptive") ? true : false;
				return true;
			}
		}
		errorLabel.setText("Select algorithm.");
		return false;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == createQueueButton) {
			if(isComplete()) {
				String[] queueInfo = {
					String.valueOf(++noOfQueues),
					quantumTime.getText(),
					schedulingAlgo
				};
				frame.addQueues(queueInfo, isPreemptive);
				frame.setFocusable(true);
				frame.setEnabled(true);
				this.dispose();
			} else {
				errorLabel.setVisible(true);
			}
		}
		else if(e.getSource() == cancel) {
			frame.setFocusable(true);
			frame.setEnabled(true);
			this.dispose();
		}
	}

	private class Window extends WindowAdapter {
		AddQueueFrame windowFrame;
		public Window(AddQueueFrame frame) {
			windowFrame = frame;
		}
		public void windowClosed(WindowEvent w) {
			frame.setFocusable(true);
			frame.setEnabled(true);
			windowFrame.dispose();
		}
		public void windowClosing(WindowEvent w) {
			frame.setFocusable(true);
			frame.setEnabled(true);
			windowFrame.dispose();
		}
	}

}
