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
	public static int noOfQueues = 0;

	public AddQueueFrame(Frame frame) {
		this.frame = frame;
		this.addWindowListener(new Window(this));
		setLayout(new BorderLayout());
		//setUndecorated(true);
		setSize(300,180);
		setResizable(false);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		JPanel algoPanel = new JPanel();
		algoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JPanel preemptPanel = new JPanel();
		preemptPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JPanel quantumPanel = new JPanel();
		quantumPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setPreferredSize(new Dimension(100,40));

		mainPanel.add(algoPanel, BorderLayout.NORTH);
		mainPanel.add(preemptPanel, BorderLayout.CENTER);
		mainPanel.add(quantumPanel, BorderLayout.SOUTH);

		JLabel quantumLabel = new JLabel("Quantum Time: ");
		quantumTime = new JTextField(2);

		String[] radio = {"FCFS", "SRTF", "Priority", "Round Robin"};
		ButtonGroup bg = new ButtonGroup();
		for(int x = 0; x < schedulingType.length; x++) {
			bg.add(schedulingType[x] = new JRadioButton(radio[x]));
			schedulingType[x].addActionListener(this);
			algoPanel.add(schedulingType[x]);
		}

		String[] preRadio = {"Preemptive", "Non-Preemptive"};
		ButtonGroup bg2 = new ButtonGroup();
		for(int a = 0; a < preemptiness.length; a++) {
			bg2.add(preemptiness[a] = new JRadioButton(preRadio[a]));
			preemptPanel.add(preemptiness[a]);
		}

		errorLabel = new JLabel();
		errorLabel.setVisible(false);

		createQueueButton = new JButton("Create Queue");
		createQueueButton.addActionListener(this);

		cancel = new JButton("Cancel");
		cancel.addActionListener(this);

		quantumPanel.add(quantumLabel);
		quantumPanel.add(quantumTime);

		buttonPanel.add(errorLabel);
		buttonPanel.add(createQueueButton);
		buttonPanel.add(cancel);

		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	public boolean isComplete() {
		boolean hasChosen = false;
		for(int x = 0; x < schedulingType.length; x++) {
			if(schedulingType[x].isSelected()) {
				hasChosen = true;
				schedulingAlgo = schedulingType[x].getText();
				break;
			}
		}

		if(!hasChosen) {
			errorLabel.setText("Select an algorithm.");
			return false;
		} else {
			if(schedulingAlgo.equals("FCFS") || schedulingAlgo.equals("Priority")) {
				return true;
			}
			else if(schedulingAlgo.equals("Round Robin")) {
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
					errorLabel.setText("Invalid quantum time.");
					return false;
				}
				return true;
			}
			else if(schedulingAlgo.equals("SRTF")) {
				for(int x = 0; x < preemptiness.length; x++) {
					if(preemptiness[x].isSelected()) {
						schedulingAlgo = (x == 0 ? "SRTF" : "SJF");
						return true;
					}
				}
				errorLabel.setText("Select Queue Type.");
				return false;
			}
		}
		errorLabel.setText("Incomplete Data.");
		return false;
	}

	private void enableRadio(boolean enabled) {
		for(int x = 0; x < 2; x++) {
			preemptiness[x].setEnabled(enabled);
			if(enabled) {
				preemptiness[x].setSelected(false);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == schedulingType[0]) {
			preemptiness[1].setSelected(true);
			enableRadio(false);
			quantumTime.setText("0");
			quantumTime.setEditable(false);
		}
		else if(e.getSource() == schedulingType[1]) {
			enableRadio(true);
			quantumTime.setText("0");
			quantumTime.setEditable(false);
		}
		else if(e.getSource() == schedulingType[2]) {
			preemptiness[0].setSelected(true);
			quantumTime.setEditable(false);
			quantumTime.setText("0");
			enableRadio(false);
		}
		else if(e.getSource() == schedulingType[3]) {
			preemptiness[0].setSelected(true);
			quantumTime.setEditable(true);
			quantumTime.setText("0");
			enableRadio(false);
		}

		if(e.getSource() == createQueueButton) {
			if(isComplete()) {
				String[] queueInfo = {
					String.valueOf(++noOfQueues),
					quantumTime.getText(),
					schedulingAlgo
				};
				for(int x = 0; x < preemptiness.length; x++) {
					if(preemptiness[x].isSelected()) {
						isPreemptive = preemptiness[x].getText().equals("Preemptive") ? true : false;
					}
				}
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
