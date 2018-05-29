import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.ArrayList;

public class F extends JFrame {

	public static ArrayList<ArrayList<JLabel>> queueLabels = new ArrayList<ArrayList<JLabel>>();
	public static ArrayList<JLabel> timeLabels = new ArrayList<JLabel>();

	public static JPanel[] queuePanels;
	public static JPanel timePanel;
	private JPanel leftPanel;
	private JPanel panel;
	
	public static JLabel[] queueTypeLabels;
	public static JFrame frame;
	public static int noOfQueues;


	public F(JFrame frame, int noOfQueues) {
		super("MLFQ Process Queue Distribution");
		this.noOfQueues = noOfQueues;
		this.frame = frame;

		panel = new JPanel();
		leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(noOfQueues,1));
		panel.setLayout(new GridLayout(noOfQueues,1));
		queuePanels = new JPanel[noOfQueues];
		queueTypeLabels = new JLabel[noOfQueues];

		for(int x = 0; x < noOfQueues; x++) {
			queuePanels[x] = new JPanel();
			queuePanels[x].setBorder(new LineBorder(Color.BLACK, 2));
			panel.add(queuePanels[x]);
			queueLabels.add(new ArrayList<JLabel>());
			queueTypeLabels[x] = new JLabel("  Q" + (x+1) + "  ", SwingConstants.CENTER);
			leftPanel.add(queueTypeLabels[x]);
		}

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		timePanel = new JPanel();
		timePanel.setLayout(new GridLayout(1,Frame.GANTT_TIME.size()));
		JScrollPane pane = new JScrollPane(p);

		p.add(timePanel, BorderLayout.NORTH);
		p.add(panel, BorderLayout.CENTER);
		add(leftPanel, BorderLayout.WEST);
		add(pane, BorderLayout.CENTER);
		setSize(700,550);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public static void reset() {

	}

}