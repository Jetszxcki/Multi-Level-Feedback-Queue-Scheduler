public class ProcessQueue{
	public static final int FCFS=1234, STR=1235, PRIO = 1236, RR=1237;
	
	PCB.PCBLink first = new PCB.PCBLink(null);
	int queueType;
	int quantum = 5;
	boolean isPreemptive = true;
	boolean queuing = false;
	
	public ProcessQueue(int queueType){
		this.queueType = queueType;
	}
	
	public ProcessQueue(int queueType, int quantum){ 
		this.queueType = queueType;
		if(queueType == RR){
			this.quantum = quantum;
		}
	}
	
	public boolean enqueue(PCB p){
		queuing = true;
		if(first.value == null){
			first.value = p;
			queuing = false;
			return true;
		}
		
		PCB.PCBLink pl = new PCB.PCBLink(p);
		PCB.PCBLink buff = first;
		if(queueType == FCFS || queueType == RR){ // this is because rr is still fcfs but preemptive(in scheduler point of view)
			while(buff.next != null){
				buff = buff.next;
			}
			buff.next = pl;
		}else if(queueType == STR){ // this already implements FCFS in case of equal remaining time
			if(first.value.remaining_time > pl.value.remaining_time){
				pl.next = first;
				first = pl;
				queuing = false;
				return true;
			}else{
				PCB.PCBLink buff2 = first;
				try{
					while(!(buff.value == null) && buff.value.remaining_time <= pl.value.remaining_time){
						buff2 = buff; 
						buff = buff.next;
						// we are sure at this point that buff2.next is buff
					}
				}catch(NullPointerException ex){}
				buff2.next = pl;
				pl.next = buff;
				queuing = false;
				return true;
			}
		}else if(queueType == PRIO){  // this already implements FCFS in case of equal priority
			if(first.value.priority > pl.value.priority){
				pl.next = first;
				first = pl;
				queuing = false;
				return true;
			}else{
				PCB.PCBLink buff2 = first;
				try{
					while(!(buff.value == null) && buff.value.priority <= pl.value.priority){
						buff2 = buff; 
						buff = buff.next;
						// we are sure at this point that buff2.next is buff
					}
				}catch(NullPointerException ex){}
				buff2.next = pl;
				pl.next = buff;
				queuing = false;
				return true;
			}
		}
		queuing = false;
		return false;
	}
	
	public PCB dequeue(){
		while(queuing){
			System.out.print("");
		}
		PCB.PCBLink ret = first;
		first = first.next;
		if(first == null){
			first = new PCB.PCBLink(null);
		}
		ret.next = null;
		return ret.value;
	}
	
	public PCB peek(){
		return first.value;
	}
	
	public boolean isEmpty(){
		if(first.value == null){
			return true;
		}else{
			return false;
		}
	}
	
	public void toFrame(int queueNo){
		//String ret = "";
		int i = 0;
		PCB.PCBLink buff = first;
		try{
			for(int x = 0; x < Frame.PROCESS_DIVISIONS; x++) {
				Frame.processesLabel[queueNo][x].setBackground(java.awt.Color.WHITE);
				Frame.processesLabel[queueNo][x].setText(null);
			}
			while(!(buff.value == null)){
				//ret += buff.value.name +"|";
				Frame.processesLabel[queueNo][i].setBackground(java.awt.Color.CYAN);
				Frame.processesLabel[queueNo][i].setText(buff.value.name);
				//ret += "\nProcess " + i + ": " + buff.value.name + 
						//"\nBurst time: " + buff.value.burst_time +
						//"\nRemaining time: " + buff.value.remaining_time +
						//"\nPriority: " + buff.value.priority + "\n";
				i++;
				buff = buff.next;
			}
		}catch(NullPointerException ex){}
		//return ret;
	}
	
	public static void main(String[] args){
		//can try checking them here
		ProcessQueue pq = new ProcessQueue(STR);
		//PCB b1 = pq.dequeue();
		int[] prio = {1,6,4,83,03,54,7,9,3,10};
		int[] burst_times = {12,30,42,9,1,69,12,3,57,29};
		for(int i = 0; i < 10; i++){
			pq.enqueue(new PCB(prio[i], burst_times[i], burst_times[i], i));
			//System.out.println("\n---------------" + pq.toString() + "---------------");
		}
		System.out.println("\n---------------" + pq.toString() + "---------------");
		PCB b = pq.dequeue();
		b.remaining_time = 0;
		System.out.println("\n---------------" + pq.toString() + "---------------");
	}
}