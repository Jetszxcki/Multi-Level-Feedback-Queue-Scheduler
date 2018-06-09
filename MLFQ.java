/*
 Usage:
 	Constructor parameter: 2D array of ints. 
 	{{queueType, [quantum]}, {queueType}, {queueType}}
 	ex :
 	 	int [][] processTypes = {{ProcessQueue.RR, 5}, {ProcessQueue.RR, 10}, {ProcessQueue.STR}};
 		MLFQ mlfq = new MLFQ(processTypes);
*/
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import java.awt.Color;

public class MLFQ implements ActionListener{
	Timer timer = new Timer(1, this);
	PCB[] processes = null;
	
	ProcessQueue[] queue_level = null;
	ProcessQueue arrival_queue = new ProcessQueue(ProcessQueue.FCFS);
	PCB in_CPU = null;
	int current_time;
	//int overhead_time;
	int cpu_time;
	
	boolean isPreemptive = true;
	boolean running = false;
	boolean is_schedulling = false;
	int cpu_quantum = 0;

	float ave_turnaround = 0;
	float ave_waiting = 0;
	float ave_response = 0;
	
	public MLFQ(int[][] queueTypes){
		queue_level = new ProcessQueue[queueTypes.length];
		for(int i = 0; i < queueTypes.length; i++){
			if(queueTypes[i][0] == ProcessQueue.RR){
				try{
					queue_level[i] = new ProcessQueue(queueTypes[i][0], queueTypes[i][1]);
				}catch(ArrayIndexOutOfBoundsException ex){
					queue_level[i] = new ProcessQueue(queueTypes[i][0]);
				}
			}else{
				queue_level[i] = new ProcessQueue(queueTypes[i][0]);
			}
		}
	}
	
	public void loadProcesses(PCB[] pcb){
		processes = pcb.clone();
		//sorting part below can be changed (kay bubble sort la ini)

		for(int x = 0; x < processes.length; x++) {
			for(int y = x+1; y < processes.length; y++) {
				if(processes[x].arrival_time > processes[y].arrival_time) {
					PCB tmp = processes[x];
					processes[x] = processes[y];
					processes[y] = tmp;
				} else if(processes[x].arrival_time == processes[y].arrival_time) {
					if(processes[x].priority > processes[y].priority) {
						PCB tmp = processes[x];
						processes[x] = processes[y];
						processes[y] = tmp;
					}
				}
			}
		}
		for(int i = 0; i < processes.length; i++){
	//		System.out.println(processes[i].name + ": " + processes[i].arrival_time + ", " + processes[i].priority + ", " + processes[i].remaining_time + "/" + processes[i].burst_time);
			arrival_queue.enqueue(processes[i]);
		}
	}
	
	
	public boolean start_scheduling(){
		if(processes == null || queue_level == null){
			return false;
		}else{
			running = true;
			current_time = 0;
			//overhead_time = 0;
			cpu_time = 0;
			timer.start();
		}
		return true;
	}
	
	public void actionPerformed(ActionEvent ev){
		//long s = System.nanoTime();
		try { Thread.sleep(1000); }catch(InterruptedException e) {}
		if(is_schedulling){return;}
		is_schedulling = true;
		System.out.println("(Time: " + current_time + ")"); 

		while(!arrival_queue.isEmpty() && arrival_queue.peek().arrival_time == current_time){
			PCB buffer = arrival_queue.dequeue();
			//Frame.processesLabel[0][0].setText(buffer.name);
			queue_level[buffer.current_level].enqueue(buffer);
			System.out.println("\t" + buffer.name + " is queued");
			//System.out.println("Queue Level: " + buffer.current_level);
		}
		isPreemptive = true;
		if(in_CPU == null || isPreemptive){//comment this part to change to pre emptive & uncomment to change to non pre emptive
			boolean queuesEmpty = true;
			for(int i = 0; i < queue_level.length; i++){
				if(!(queue_level[i].isEmpty())){
					//comment this part below to change to non pre emptive & uncomment to change to pre emptive 
					if(isPreemptive) {
						queuesEmpty = false;
						if(in_CPU!=null){
							//System.out.println("queue level is preemptive " + queue_level[i].isPreemptive + ";" + in_CPU.remaining_time + ";" + queue_level[i].peek().remaining_time+";"+(queue_level[i].queueType == ProcessQueue.STR));
							if(in_CPU.current_level > i){
								System.out.println("Higher ups");
								System.out.print("\tMoved " + in_CPU.name + " from level " + (in_CPU.current_level+1));
								queue_level[in_CPU.current_level].enqueue(in_CPU);
								System.out.println(" to level " + (in_CPU.current_level+1));
							}else if((queue_level[i].isPreemptive && queue_level[i].queueType == ProcessQueue.PRIO) && in_CPU.priority > queue_level[i].peek().priority){
								System.out.println("Higher priority preempt");
								System.out.print("\tMoved " + in_CPU.name + " from level " + (in_CPU.current_level+1));
								queue_level[(in_CPU.current_level==queue_level.length-1?in_CPU.current_level:++in_CPU.current_level)].enqueue(in_CPU);
								System.out.println(" to level " + (in_CPU.current_level+1));
							}else if((queue_level[i].isPreemptive && queue_level[i].queueType == ProcessQueue.STR) && in_CPU.remaining_time > queue_level[i].peek().remaining_time){
								System.out.println("Shortest time preempt");
								System.out.print("\tMoved " + in_CPU.name + " from level " + (in_CPU.current_level+1));
								queue_level[(in_CPU.current_level==queue_level.length-1?in_CPU.current_level:++in_CPU.current_level)].enqueue(in_CPU);
								System.out.println(" to level " + (in_CPU.current_level+1));
							}else{
								continue;
							}
						}else if(in_CPU == null){
							//System.out.println("NULL");
						}else{
							continue;
						}
					}
					//comment this part above to change to non pre emptive & uncomment to change to pre emptive
					in_CPU = queue_level[i].dequeue();
					if(in_CPU.response_time == -1){
						in_CPU.response_time = current_time - in_CPU.arrival_time;
					}
					if(queue_level[i].queueType == ProcessQueue.RR){
						cpu_quantum = queue_level[i].quantum; 
					}else{
						cpu_quantum = in_CPU.remaining_time;
					}
					queuesEmpty = false;
					break;
				}
			}
			if(queuesEmpty && arrival_queue.isEmpty() && in_CPU == null){
				timer.stop();
				postResult();
				JOptionPane.showMessageDialog(null, "        Scheduling Finished.", "Done!", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		} //comment this part to change to pre emptive & uncomment to change to non pre emptive
		update_GANTT_CHART();
		update_GANTT_CHART_2();

		if(!(in_CPU == null)){
			System.out.println("\t" + in_CPU.name + " in CPU: " + in_CPU.remaining_time + "/" + in_CPU.burst_time);
			in_CPU.remaining_time--;
			cpu_quantum--;
			if(cpu_quantum == 0 || in_CPU.remaining_time == 0){
				if(in_CPU.remaining_time > 0){
					System.out.print("\tMoved " + in_CPU.name + " from level " + (in_CPU.current_level+1));
					queue_level[(in_CPU.current_level==queue_level.length-1?in_CPU.current_level:++in_CPU.current_level)].enqueue(in_CPU);
					System.out.println(" to level " + (in_CPU.current_level+1));
				}else{
					in_CPU.turnaround_time = current_time-in_CPU.arrival_time+1;
					in_CPU.waiting_time = in_CPU.turnaround_time-in_CPU.burst_time;
					System.out.println("\t" + in_CPU.name + " is done");
				}
				cpu_quantum = 0;
				in_CPU = null;
			}
			cpu_time += 1;
		}

		current_time += 1;
		is_schedulling = false;
		updateMLFQ();
	}

	private void updateMLFQ() {
		for(int a = 0; a < queue_level.length; a++) {
			queue_level[a].toFrame(a);
		}
	}

	private void update_GANTT_CHART_2() {
		JLabel label = new JLabel(String.valueOf(current_time));
		F.timeLabels.add(label);
		F.timePanel.setLayout(new GridLayout(1,F.timeLabels.size()));

		for(JLabel timeLabel : F.timeLabels) {
			F.timePanel.add(timeLabel);
			F.timePanel.revalidate();
		}

		label = new JLabel(in_CPU != null ? in_CPU.name : "  ", javax.swing.SwingConstants.CENTER);
		label.setOpaque(true);
		label.setForeground(java.awt.Color.WHITE);
		label.setFont(new java.awt.Font("Calibri", java.awt.Font.BOLD, 20));
		label.setBackground(in_CPU != null ? in_CPU.color : Color.WHITE);

		for(int x = 0; x < F.queuePanels.length; x++) {
			if(in_CPU != null) {
				if(in_CPU.current_level == x) {
					F.queueLabels.get(x).add(label);
				}else{
					JLabel lbl = new JLabel(" ");
					lbl.setOpaque(true);
					lbl.setFont(new java.awt.Font("Calibri", java.awt.Font.BOLD, 20));
					lbl.setBackground(Color.WHITE);
					F.queueLabels.get(x).add(lbl);
				}
			}else{
				JLabel lbl = new JLabel(" ");
				lbl.setOpaque(true);
				lbl.setFont(new java.awt.Font("Calibri", java.awt.Font.BOLD, 20));
				lbl.setBackground(Color.WHITE);
				F.queueLabels.get(x).add(lbl);
			}
			F.queuePanels[x].setLayout(new GridLayout(1,F.queueLabels.get(x).size()));
			for(JLabel ll : F.queueLabels.get(x)) {
				F.queuePanels[x].add(ll);
				F.queuePanels[x].revalidate();
			}
		}

	}

	private void update_GANTT_CHART() {
		JLabel label = null;

		//  gantt chart timePanel
		Frame.GANTT_TIME.add(label = new JLabel(String.valueOf(current_time)));
		Frame.timePanel.setLayout(new GridLayout(1,Frame.GANTT_TIME.size()));

		for(JLabel timeLabel : Frame.GANTT_TIME) {
			Frame.timePanel.add(timeLabel);
			Frame.timePanel.revalidate();
		}

		// gantt chart gcPanel
		Frame.GANTT_CHART_LABELS.add(label = new JLabel(in_CPU != null ? in_CPU.name : "  ", javax.swing.SwingConstants.CENTER));
		label.setOpaque(true);
		label.setForeground(java.awt.Color.WHITE);
		label.setFont(new java.awt.Font("Calibri", java.awt.Font.BOLD, 20));
		label.setBackground(in_CPU != null ? in_CPU.color : Color.WHITE);

		Frame.gcPanel.setLayout(new GridLayout(1,Frame.GANTT_CHART_LABELS.size()));
		for(JLabel gc : Frame.GANTT_CHART_LABELS) {
			Frame.gcPanel.add(gc);
			Frame.gcPanel.revalidate();
		}

		Frame.bottomPanel[0].add(Frame.timePanel, BorderLayout.NORTH);
		Frame.bottomPanel[0].add(Frame.gcPanel, BorderLayout.CENTER);
		Frame.bottomPanel[0].revalidate();
	}
	
	public void postResult(){
		System.out.println("Schedulling done!\nResult:");
		for(int i = 0; i < processes.length; i++){
			System.out.println(processes[i].name);
			System.out.println("\tArrived at:" + processes[i].arrival_time);
			System.out.println("\tFinished at:" + (processes[i].arrival_time+processes[i].turnaround_time));
			System.out.println("\tBurst time:" + processes[i].burst_time);
			System.out.println("\tResponse time:" + processes[i].response_time);
			System.out.println("\tWaiting time:" + processes[i].waiting_time);
			System.out.println("\tTurnaround time:" + processes[i].turnaround_time);
		}
		
		for(int i = 0 ; i < processes.length; i++){
			ave_turnaround += (float)processes[i].turnaround_time;
			ave_waiting += (float)processes[i].waiting_time;
			ave_response += (float)processes[i].response_time;
		}
		ave_turnaround  = ave_turnaround/(float)processes.length;
		ave_response  = ave_response/(float)processes.length;
		ave_waiting  = ave_waiting/(float)processes.length;
		System.out.println();
		System.out.println("Average response time: " + ave_response);
		System.out.println("Average waiting time: " + ave_waiting);
		System.out.println("Average turnaround time: " + ave_turnaround);
		running = false;
	}
}
