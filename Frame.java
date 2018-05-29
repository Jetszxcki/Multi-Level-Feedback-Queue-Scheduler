import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("serial")

public class Frame extends JFrame implements ActionListener {
	
	public static final Font CALIBRI_PLAIN = new Font("Calibri", Font.PLAIN, 13);
	public static final Font CALIBRI = new Font("Calibri", Font.BOLD, 13);

// config panel components
	private JPanel[] panels = new JPanel[3]; // main panels
	
	private AddProcessFrame addProcessFrame;
	private AddQueueFrame addQueueFrame;
	private JFrame postAnalysisFrame;

	private JButton createProcessButton;
	private JButton postAnalysisButton;
	private JButton loadProcessButton;
	private JButton addProcessButton;
	private JButton loadQueueButton;
	private JButton addQueueButton;
	private JButton startSchedule;
	private JButton pause, resume;
	private JButton newButton;

	private JTable resultsTable;
	private JTable processTable;
	private JTable queuesTable;

	private JScrollPane gantt_scroll;

	private String[] processColumnHeads = {"Process ID", "Priority", "Burst Time", "Arrival Time"};
	private String[] queuesColumnHeads = {"Queue No.", "Quantum Time", "Scheduling Algorithm"};
	private String[] resultsColumnHeads = {"Results", ""};

	private String[][] processes = new String[1][4];
	private String[][] queues = new String[1][3];
	private boolean[] queuebool = new boolean[1];

	public static final int DEFAULT_MAX = 4;
	public static int PROCESS_DIVISIONS = 0;
	private int noOfProcesses = 0;
	private int noOfQueues = 0;

	private boolean processReady;
	private boolean queuesReady;
	//private boolean isPreemptive;

// middle panel (For MLFQ)
	public static JLabel[][] processesLabel;
	public static JPanel[] middlePanels;
	public static JPanel[] queuePanels;

// bottom panel (For GANTT-CHART)
	public static ArrayList<JLabel> GANTT_CHART_LABELS = new ArrayList<JLabel>();
	public static ArrayList<JLabel> GANTT_TIME = new ArrayList<JLabel>();
	public static JPanel[] bottomPanel = new JPanel[2];
	public static JPanel[] GANTT_CHART_PANELS;
	public static JPanel timePanel;
	public static JPanel gcPanel;
// end

	public ArrayList<Color> pcb_colors = new ArrayList<Color>();
	public int[][] queueTypes;
	public PCB[] pcb;
	public MLFQ mlfq;

	
	public Frame(String frameTitle) {

		super(frameTitle);
		setLayout(new BorderLayout());
		setComponents();

	}
	
	public void setComponents() {

		UIManager.put("OptionPane.messageFont", CALIBRI);
		int[][] dimensions = { {320,0}, {100,0}, {0,165}};
		Color[] colors = {Color.WHITE, Color.GRAY, Color.DARK_GRAY};
		
		for(int x = 0; x < panels.length; x++) {
			panels[x] = new JPanel();
			panels[x].setBackground(colors[x]);
			panels[x].setPreferredSize(new Dimension(dimensions[x][0], dimensions[x][1]));
			panels[x].setLayout(new BorderLayout(2,2));
		}
		
		SET_CONFIG_PANEL();
		SET_MLFQ_PANEL(DEFAULT_MAX);
		SET_GANTT_CHART_PANEL();
		
		add(panels[0], BorderLayout.WEST);
		add(panels[2], BorderLayout.SOUTH);

	}
	
	private PCB randomizeProcess() {

		PCB randomized_process = null;
		Random randomizer = new Random();
		String[] pcbTableString = new String[4];

		int rbt = 0;
		int rat = 0;
		int rp = 0;

		int R = randomizer.nextInt(256);
		int G = randomizer.nextInt(256);
		int B = randomizer.nextInt(256);
		Color c = new Color(R,G,B);

		do {
			rbt = randomizer.nextInt(11);
		}while(rbt == 0);
		rat = randomizer.nextInt(11);
		rp = randomizer.nextInt(11);
		randomized_process = new PCB("P"+String.valueOf(noOfProcesses+1), rp, rbt, rbt, rat, c);

		pcbTableString[0] = "P" + String.valueOf(noOfProcesses+1);
		pcbTableString[1] = String.valueOf(rp);
		pcbTableString[2] = String.valueOf(rbt);
		pcbTableString[3] = String.valueOf(rat);

		addProcess(pcbTableString, c);
		//noOfProcesses++;

		return randomized_process;

	}

	public void SET_CONFIG_PANEL() {

		JLabel configProcessLabel = new JLabel("PROCESS CONFIGURATION");
		configProcessLabel.setFont(CALIBRI);
		JPanel[] configPanels = new JPanel[2];
		int[][] dimensions = { {0,50}, {0,190} };

		for(int x = 0; x < configPanels.length; x++) {
			configPanels[x] = new JPanel();
			configPanels[x].setBackground(Color.WHITE);
			configPanels[x].setPreferredSize(new Dimension(dimensions[x][0], dimensions[x][1]));
		}

// ==================== Add Process Panel  ============================

		JPanel processButtonPanel = new JPanel();
		processButtonPanel.setBackground(Color.WHITE);
		addProcessButton = new JButton("Add Process");
		loadProcessButton = new JButton("Load Processes");
		newButton = new JButton("RESET MLFQ");
		addProcessButton.addActionListener(this);
		loadProcessButton.addActionListener(this);
		newButton.addActionListener(this);
		processButtonPanel.add(addProcessButton);
		processButtonPanel.add(loadProcessButton);
		processButtonPanel.add(newButton);

		processTable = new JTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		processTable.setModel(new DefaultTableModel(new String[0][4], processColumnHeads));
		JScrollPane processScroll = new JScrollPane(processTable);
		processScroll.setPreferredSize(new Dimension(300,220));

		TableColumn[] processColumns = { processTable.getColumn("Process ID"),
							  processTable.getColumn("Priority"),
							  processTable.getColumn("Burst Time"),
							  processTable.getColumn("Arrival Time") };
		
		for(int x = 0; x < processColumns.length; x++)	{						  
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
			processColumns[x].setCellRenderer(dtcr);
			processColumns[x].setHeaderRenderer(dtcr);
		}

		configPanels[0].add(configProcessLabel);
		configPanels[0].add(processScroll);
		configPanels[0].add(processButtonPanel);

// ====================== Add Queues Panel ==========================

		JLabel configQueueLabel = new JLabel("QUEUE CONFIGURATION");
		configQueueLabel.setFont(CALIBRI);
		JPanel queueButtonPanel = new JPanel();
		queueButtonPanel.setBackground(Color.WHITE);
		loadQueueButton = new JButton("Load Queue");
		addQueueButton = new JButton("Add Queue");
		loadProcessButton.setEnabled(false);
		loadQueueButton.setEnabled(false);
		loadQueueButton.addActionListener(this);
		addQueueButton.addActionListener(this);
		queueButtonPanel.add(addQueueButton);
		queueButtonPanel.add(loadQueueButton);

		queuesTable = new JTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		queuesTable.setModel(new DefaultTableModel(new String[0][3], queuesColumnHeads));
		JScrollPane queueScroll = new JScrollPane(queuesTable);
		queueScroll.setPreferredSize(new Dimension(300,80));

		TableColumnModel columnModel = queuesTable.getColumnModel();	
		columnModel.getColumn(0).setPreferredWidth(40);

		TableColumn[] queueColumns = { queuesTable.getColumn("Queue No."),
							  		   queuesTable.getColumn("Quantum Time"),
							  		   queuesTable.getColumn("Scheduling Algorithm") };
		
		for(int x = 0; x < queueColumns.length; x++)	{						  
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);
			queueColumns[x].setCellRenderer(dtcr);
			queueColumns[x].setHeaderRenderer(dtcr);
		}

		startSchedule = new JButton("Start Schedule");
		// startSchedule.setEnabled(false);
		startSchedule.addActionListener(this);
		resume = new JButton("Resume");
		pause = new JButton("Pause");
		pause.addActionListener(this);
		resume.addActionListener(this);
		resume.setEnabled(false);
		pause.setEnabled(false);

		configPanels[1].add(configQueueLabel);
		configPanels[1].add(queueScroll);
		configPanels[1].add(queueButtonPanel);
		configPanels[1].add(startSchedule);
		configPanels[1].add(resume);
		configPanels[1].add(pause);


		panels[0].add(configPanels[0], BorderLayout.CENTER);
		panels[0].add(configPanels[1], BorderLayout.SOUTH);

	}

	public void SET_MLFQ_PANEL(int NO_OF_QUEUES) {

		PROCESS_DIVISIONS = noOfProcesses > 15 ? noOfProcesses : 15;

		panels[1] = new JPanel();
		panels[1].setPreferredSize(new Dimension(100,0));
		panels[1].setLayout(new GridLayout(NO_OF_QUEUES,2));

		middlePanels = new JPanel[NO_OF_QUEUES];
		queuePanels = new JPanel[NO_OF_QUEUES];
		processesLabel = new JLabel[NO_OF_QUEUES][PROCESS_DIVISIONS];
		JLabel[] queueLabels = new JLabel[NO_OF_QUEUES];

		for(int x = 0; x < NO_OF_QUEUES; x++) {
			queueLabels[x] = new JLabel("Q" + (x+1), SwingConstants.CENTER);
			queueLabels[x].setForeground(Color.WHITE);
			queueLabels[x].setFont(new Font("Calibri", Font.PLAIN, 20));

			middlePanels[x] = new JPanel();
			middlePanels[x].setLayout(new FlowLayout(FlowLayout.RIGHT));
			middlePanels[x].setBorder(new LineBorder(Color.GRAY,2));
			middlePanels[x].setBackground(Color.BLACK);

			queuePanels[x] = new JPanel();
			queuePanels[x].setBackground(Color.WHITE);
			int width = (int)middlePanels[x].getPreferredSize().getWidth();
			int height = (int)middlePanels[x].getPreferredSize().getHeight();
			queuePanels[x].setPreferredSize(new Dimension(width*45,height*8));
			queuePanels[x].setLayout(new GridLayout(1,PROCESS_DIVISIONS));
			for(int y = 0; y < PROCESS_DIVISIONS; y++) {
				queuePanels[x].add(processesLabel[x][y] = new JLabel("", SwingConstants.CENTER));
				processesLabel[x][y].setBorder(new LineBorder(Color.BLACK,2));
				processesLabel[x][y].setFont(CALIBRI_PLAIN);
				processesLabel[x][y].setOpaque(true);
			}

			middlePanels[x].add(queueLabels[x]);
			middlePanels[x].add(queuePanels[x]);
			panels[1].add(middlePanels[x]);
		}

		this.add(panels[1], BorderLayout.CENTER);
		this.revalidate();

	}

	public void SET_GANTT_CHART_PANEL() {

		for(int x = 0; x < bottomPanel.length; x++) {
			bottomPanel[x] = new JPanel();
			bottomPanel[x].setBackground(Color.WHITE);
		}

		Object[][] resultStrings = {{"Current Time","0"}, {"Process in CPU","NONE"}, 
									{"Average Response Time","0.0"}, {"Average Waiting Time","0.0"}, 
									{"Average Turnaround Time","0.0"}};

		resultsTable = new JTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		resultsTable.setModel(new DefaultTableModel(resultStrings, resultsColumnHeads));
		resultsTable.setTableHeader(null);
		JScrollPane resultsScroll = new JScrollPane(resultsTable);
		resultsScroll.setColumnHeaderView(null);
		resultsScroll.setPreferredSize(new Dimension(200,102));
		TableColumnModel columnModel = resultsTable.getColumnModel();	
		columnModel.getColumn(0).setPreferredWidth(140);
		columnModel.getColumn(1).setPreferredWidth(65);
		resultsTable.setRowHeight(20);
							  
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		resultsTable.getColumn("Results").setCellRenderer(dtcr);
		resultsTable.getColumn("Results").setHeaderRenderer(dtcr);
		resultsTable.getColumn("").setCellRenderer(dtcr);
		resultsTable.getColumn("").setHeaderRenderer(dtcr);

		JLabel analysisLabel = null;

		bottomPanel[1].setPreferredSize(new Dimension(215,0));
		bottomPanel[1].add(analysisLabel = new JLabel("STATUS & ANALYSIS", SwingConstants.CENTER));
		bottomPanel[1].add(resultsScroll);
		bottomPanel[1].add(postAnalysisButton = new JButton("See Full Results"));

		postAnalysisButton.setEnabled(false);
		postAnalysisButton.addActionListener(this);
		analysisLabel.setFont(CALIBRI);

		// GANTT-CHART
		bottomPanel[0].setLayout(new BorderLayout());
		gantt_scroll = new JScrollPane(bottomPanel[0]);
		//gantt_scroll.setPreferredSize(new Dimension(100,100));
		gcPanel = new JPanel();
		gcPanel.setLayout(new GridLayout(1,1));
		gcPanel.setBorder(new LineBorder(Color.BLACK,3));
		//gantt_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		timePanel = new JPanel();
		timePanel.setLayout(new GridLayout(1,GANTT_CHART_LABELS.size()));
		timePanel.setBorder(new LineBorder(Color.BLACK,2));

		bottomPanel[0].add(timePanel, BorderLayout.NORTH);
		bottomPanel[0].add(gcPanel, BorderLayout.CENTER);
		panels[2].add(gantt_scroll, BorderLayout.CENTER);
		panels[2].add(bottomPanel[1], BorderLayout.EAST);
		this.revalidate();

	}

	/*private void SET_GCPANEL() {
		GANTT_CHART_PANELS = new JPanel[noOfQueues];
		gcPanel.setLayout(new GridLayout(noOfQueues, 1));
		for(int x = 0; x < noOfQueues; x++) {
			GANTT_CHART_PANELS[x] = new JPanel();
			GANTT_CHART_PANELS[x].setBorder(new LineBorder(Color.BLACK, 2));
			gcPanel.add(GANTT_CHART_PANELS[x]);
			gcPanel.revalidate();
		}
	}*/

	public void addProcess(String[] processControlBlocks, Color c) {

		boolean noDuplicates = true;
		for(int x = 0; x < processes.length; x++) {
			if(processControlBlocks[0].equals(processes[x][0])) {
				JOptionPane.showMessageDialog(null, "Process ID already exists. Must be unique.", "Duplicate", JOptionPane.ERROR_MESSAGE);
				noDuplicates = false;
			}
		}
		if(noDuplicates) {
			if(noOfProcesses == 0) {
				for(int x = 0; x < processControlBlocks.length; x++) {
					processes[noOfProcesses][x] = processControlBlocks[x];
				}
			} else {
				String[][] temp = processes;
				processes = new String[noOfProcesses+1][4];
				for(int x = 0; x < temp.length; x++) {
					for(int y = 0; y < 4; y++) {
						processes[x][y] = temp[x][y];
					}
				}
				processes[noOfProcesses] = processControlBlocks;
			}
			DefaultTableModel dtm = (DefaultTableModel)processTable.getModel();
			dtm.addRow(processControlBlocks);
			noOfProcesses++;
		}
		pcb_colors.add(c);

		if(noOfProcesses == 2) {
			loadProcessButton.setEnabled(true);
		}

	}

	public void addQueues(String[] qcb, boolean isPreemptive) {
		
		if(noOfQueues == 0) {
			queuebool[noOfQueues] = isPreemptive;
			for(int x = 0; x < qcb.length; x++) {
				queues[noOfQueues][x] = qcb[x];
			}
		} else {
			boolean[] booltmp = queuebool;
			queuebool = new boolean[noOfQueues+1];
			for(int a = 0; a < booltmp.length; a++) {
				queuebool[a] = booltmp[a];
			}
			queuebool[noOfQueues] = isPreemptive;

			String[][] temp = queues;
			queues = new String[noOfQueues+1][3];
			for(int x = 0; x < temp.length; x++) {
				for(int y = 0; y < 3; y++) {
					queues[x][y] = temp[x][y];
				}
			}
			queues[noOfQueues] = qcb;
		}
		DefaultTableModel dtm = (DefaultTableModel)queuesTable.getModel();
		dtm.addRow(qcb);
		noOfQueues++;

		loadQueueButton.setEnabled(true);

	}

	public void loadProcesses() {
		
		addProcessButton.setEnabled(false);
		loadProcessButton.setEnabled(false);
		
		pcb = new PCB[noOfProcesses];
		for(int x = 0; x < noOfProcesses; x++) {
			pcb[x] = new PCB(processes[x][0], Integer.parseInt(processes[x][1]), 
							 Integer.parseInt(processes[x][2]), Integer.parseInt(processes[x][2]),
							 Integer.parseInt(processes[x][3]), pcb_colors.get(x));
		}
		//for(int x = 0; x < pcb.length; x++) {
		//	System.out.println(pcb[x].name+" " + pcb[x].priority+" " + pcb[x].burst_time+" " + pcb[x].arrival_time);
		//}

		processReady = true;
		if(queuesReady) {
			mlfq.loadProcesses(pcb);
			startSchedule.setEnabled(true);
		}

	}

	public void loadProcesses(int[] prio, int[] bt, int[] at, int noOfProcesses) {
		
		addProcessButton.setEnabled(false);
		loadProcessButton.setEnabled(false);
		
		pcb = new PCB[noOfProcesses];
		Random randomizer = new Random();
		for(int x = 0; x < noOfProcesses; x++) {
			int R = randomizer.nextInt(256);
			int G = randomizer.nextInt(256);
			int B = randomizer.nextInt(256);
			Color c = new Color(R,G,B);
			pcb[x] = new PCB("P"+(x+1), prio[x], bt[x],bt[x],at[x], c);
		}

		processReady = true;
		if(queuesReady) {
			mlfq.loadProcesses(pcb);
			startSchedule.setEnabled(true);
		}

	}

	public void loadQueues() {

		addQueueButton.setEnabled(false);
		loadQueueButton.setEnabled(false);
		//System.out.println("No of. Queues: " + noOfQueues);

		int[][] converted = new int[noOfQueues][2];
		for(int x = 0; x < noOfQueues; x++) {
			converted[x][0] = Integer.parseInt(queues[x][1]);
			if(queues[x][2].equals("Priority")) converted[x][1] = ProcessQueue.PRIO;
			else if(queues[x][2].equals("FCFS")) converted[x][1] = ProcessQueue.FCFS;
			else if(queues[x][2].equals("SRTF")) converted[x][1] = ProcessQueue.STR;
			else if(queues[x][2].equals("Round Robin")) converted[x][1] = ProcessQueue.RR;
		}

		queueTypes = new int[noOfQueues][2];
		for(int x = 0; x < noOfQueues; x++) {
			queueTypes[x][0] = converted[x][1];
			queueTypes[x][1] = converted[x][0];
		}
		//for(int x = 0; x < noOfQueues; x++) {
		//	System.out.println(queueTypes[x][0] + " " + queueTypes[x][1]);
		//}
		mlfq = new MLFQ(queueTypes);
		for(int x = 0; x < mlfq.queue_level.length; x++) {
			mlfq.queue_level[x].isPreemptive = queuebool[x];
			System.out.println(mlfq.queue_level[x].isPreemptive);
		}
		//mlfq.isPreemptive = this.isPreemptive;

		queuesReady = true;		
		if(processReady)  {
			mlfq.loadProcesses(pcb);
			startSchedule.setEnabled(true);
		}

	}

	private void updateStatusTable() {

		new Thread() {
			public void run() {
				while(mlfq.running) {
					resultsTable.setValueAt(mlfq.current_time,0,1);
					resultsTable.setValueAt(mlfq.in_CPU == null ? "NONE" : mlfq.in_CPU.name,1,1);
					resultsTable.setValueAt(mlfq.ave_response,2,1);
					resultsTable.setValueAt(mlfq.ave_waiting,3,1);
					resultsTable.setValueAt(mlfq.ave_turnaround,4,1);
				}
				newButton.setEnabled(true);
				postAnalysisButton.setEnabled(true);
				resume.setEnabled(false);
				pause.setEnabled(false);
			}
		}.start();

	}

	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == addProcessButton) {
			// randomizeProcess();
			// the statements below are for manual adding of processes
			this.setEnabled(false);
			addProcessFrame = new AddProcessFrame(this);
			loadProcessButton.setEnabled(true);
		} 
		else if(e.getSource() == addQueueButton) {
			this.setEnabled(false);
			addQueueFrame = new AddQueueFrame(this);
		}
		else if(e.getSource() == loadProcessButton) {
			loadProcesses();
			
			// int[] prio = {12,4,4,18,17,4,20,16,19,16,17,10,3,14,10};
			// int[] bt = {6,17,3,19,7,35,46,27,25,21,26,40,49,48,43};
			// int[] at = {39,37,5,16,32,5,13,38,16,1,16,17,29,33,28};

			// int[] prio = {5,6,4,19,7,20,8,19,16,5,1,17};
			// int[] bt = {7,17,6,17,8,2,24,36,32,41,47,49};
			// int[] at = {27,26,5,29,39,8,24,35,31,21,36,21};

			// int[] prio = {8,16,2,4,10,20,7,10,5,19,1,3};
			// int[] bt = {13,1,14,7,8,6,19,3,41,43,26,29};
			// int[] at = {5,20,6,20,15,4,3,17,1,11,16,6};


		// 	if(prio.length == bt.length && prio.length == at.length) {
		// 		System.out.println("EQUAL");
		// 	}
		// 	loadProcesses(prio,bt,at,prio.length);
		} 
		else if(e.getSource() == loadQueueButton) {
			loadQueues();
		}
		else if(e.getSource() == newButton) {
			resetAll();
		}
		else if(e.getSource() == resume) {
			mlfq.timer.start();
			pause.setEnabled(true);
			resume.setEnabled(false);
		}
		else if(e.getSource() == pause) {
			mlfq.timer.stop();
			pause.setEnabled(false);
			resume.setEnabled(true);
			newButton.setEnabled(true);
		}
		else if(e.getSource() == startSchedule) {
			// SET_GCPANEL();
			pause.setEnabled(true);
			startSchedule.setEnabled(false);
			newButton.setEnabled(false);
			postAnalysisButton.setEnabled(false);
			this.remove(panels[1]);
			new F(this,noOfQueues);
			SET_MLFQ_PANEL(noOfQueues);
			updateStatusTable();
			mlfq.start_scheduling();
		}
		else if(e.getSource() == postAnalysisButton) {
			showResults();
		}

	}

	private void resetAll() {
		DefaultTableModel processTableModel = (DefaultTableModel) processTable.getModel();
		DefaultTableModel queueTableModel = (DefaultTableModel) queuesTable.getModel();
		processTableModel.setRowCount(0);
		queueTableModel.setRowCount(0);

		postAnalysisButton.setEnabled(false);
		addProcessButton.setEnabled(true);
		addQueueButton.setEnabled(true);

		processes = new String[1][4];
		queues = new String[1][3];
		pcb_colors = new ArrayList<Color>();

		startSchedule.setEnabled(false);
		GANTT_TIME = new ArrayList<JLabel>();
		GANTT_CHART_LABELS = new ArrayList<JLabel>();
		PROCESS_DIVISIONS = 0;
		noOfProcesses = 0;
		noOfQueues = 0;
		processReady = false;
		queuesReady = false;
		AddQueueFrame.noOfQueues = 0;

		this.remove(panels[1]);
		SET_MLFQ_PANEL(DEFAULT_MAX);
		panels[2].remove(gantt_scroll);
		panels[2].remove(bottomPanel[1]);
		SET_GANTT_CHART_PANEL();
	}

	private void showResults() {

		JPanel analysisPanel = new JPanel();
		analysisPanel.setLayout(new BoxLayout(analysisPanel, BoxLayout.Y_AXIS));

		this.setEnabled(false);
		postAnalysisFrame = new JFrame("Post Analysis");
		postAnalysisFrame.setSize(350,600);
		postAnalysisFrame.setVisible(true);
		postAnalysisFrame.setResizable(false);
		postAnalysisFrame.setLocationRelativeTo(null);
		postAnalysisFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		postAnalysisFrame.addWindowListener(new Window(this));

		String[] cHeads = {"Time Arrived","Time Finished","Burst Time","Response Time","Waiting Time","Turnaround Time"};
		String[] cols = {"C1","C2"};

		for(int x = 0; x < mlfq.processes.length; x++) {
			JLabel processID = new JLabel("Process ID:  " + mlfq.processes[x].name, SwingConstants.CENTER);
			processID.setFont(CALIBRI);
			analysisPanel.add(processID);

			JTable table = new JTable() {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};

			int[] times = {mlfq.processes[x].arrival_time, mlfq.processes[x].arrival_time+mlfq.processes[x].turnaround_time,
						   mlfq.processes[x].burst_time, mlfq.processes[x].response_time, mlfq.processes[x].waiting_time, mlfq.processes[x].turnaround_time};

			Object[][] postInfo = new Object[6][2];
			for(int a = 0; a < 6; a++) {
				postInfo[a][0] = cHeads[a];
				postInfo[a][1] = times[a];
			}

			table.setTableHeader(null);
			table.setModel(new DefaultTableModel(postInfo, cols));
			JScrollPane scroll = new JScrollPane(table);
			scroll.setColumnHeaderView(null);
			scroll.setPreferredSize(new Dimension(200,98));

			TableColumnModel columnModel = table.getColumnModel();	
			columnModel.getColumn(1).setPreferredWidth(20);

			TableColumn[] columns = { table.getColumn("C1"),
							  		  table.getColumn("C2") };
		
			for(int y = 0; y < columns.length; y++)	{						  
				DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
				dtcr.setHorizontalAlignment(SwingConstants.CENTER);
				columns[y].setCellRenderer(dtcr);
				columns[y].setHeaderRenderer(dtcr);
			}

			analysisPanel.add(scroll);
		}
		JScrollPane panelScroll = new JScrollPane(analysisPanel);
		panelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
		panelScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);  
		postAnalysisFrame.add(panelScroll);

	}

	private class Window extends WindowAdapter {
		private JFrame pFrame;
		public Window(JFrame parentFrame) {
			this.pFrame = parentFrame;
		}
		public void windowClosed(WindowEvent w) {
			pFrame.setFocusable(true);
			pFrame.setEnabled(true);
		}
		public void windowClosing(WindowEvent w) {
			pFrame.setFocusable(true);
			pFrame.setEnabled(true);
		}
	}

	public static void main(String[] args) {

		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			Frame f = new Frame("Multi-Level Feedback Queue Simulator");
			f.setSize(1000,700);
			f.setVisible(true);
			f.setResizable(false);
			f.setLocationRelativeTo(null);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}catch(Exception e){}		

	}

}
