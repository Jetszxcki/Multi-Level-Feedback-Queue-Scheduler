import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddProcessFrame extends JFrame implements ActionListener, Validate {
	
	private JTextField[] processInfo = new JTextField[4];
	private JLabel[] processLabels = new JLabel[processInfo.length];
	private JButton createProcessButton, cancel;
	private JLabel errorLabel;
	private JPanel panel;
	private Frame frame;

	public AddProcessFrame(Frame frame) {
		this.frame = frame;
		this.addWindowListener(new Window(this));
		setLayout(new BorderLayout());
		//setUndecorated(true);
		setSize(150,200);
		setResizable(false);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));

		String[] labels = {"Process ID:   ", "Priority:        ", "Burst Time:   ", "Arrival Time: "};
		for(int x = 0; x < processInfo.length; x++) {
			processInfo[x] = new JTextField(7);
			processLabels[x] = new JLabel(labels[x]);
			panel.add(processLabels[x]);
			panel.add(processInfo[x]);
		}
		
		errorLabel = new JLabel();
		errorLabel.setVisible(false);
		createProcessButton =  new JButton("Create Process");
		createProcessButton.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);

		panel.add(errorLabel);
		panel.add(createProcessButton);
		panel.add(cancel);

		add(panel, BorderLayout.CENTER);
	}

	public boolean isComplete() {
		for(int x = 0; x < processInfo.length; x++) {
			String input = processInfo[x].getText();
			if(input.equals("") || input == null) {
				errorLabel.setText("Fill all fields.");
				return false;
			}
		}
		try {
			int check1 = Integer.parseInt(processInfo[1].getText());	
			int check2 = Integer.parseInt(processInfo[2].getText());	
			int check3 = Integer.parseInt(processInfo[3].getText());	
		} catch(Exception e) {
			errorLabel.setText("Invalid time.");
			errorLabel.setVisible(true);
			return false;
		}
		for(int x = 1; x < processInfo.length; x++) {
			if(Integer.parseInt(processInfo[x].getText()) < 0) {
				errorLabel.setText("Time must be > 0");
				return false;
			}
		}
		return true;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == createProcessButton) {
			if(isComplete()) {
				String[] pcb = { processInfo[0].getText(), processInfo[1].getText(),
							     processInfo[2].getText(), processInfo[3].getText()};

				frame.setFocusable(true);
				frame.setEnabled(true);
				frame.addProcess(pcb);
				this.dispose();
			} else {
				setSize(150,220);
				errorLabel.setVisible(true);
			}
		} else if(e.getSource() == cancel) {
			frame.setFocusable(true);
			frame.setEnabled(true);
			this.dispose();
		}
	}

	private class Window extends WindowAdapter {
		AddProcessFrame windowFrame;
		public Window(AddProcessFrame frame) {
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