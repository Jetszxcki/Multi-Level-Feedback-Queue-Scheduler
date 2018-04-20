//Usage PCB(priority_num, burst_time, remaining_time, arrival_time);

public class PCB{
	String name = "";
	int burst_time = 0;
	int remaining_time = 0;
	int priority = 0;
	int arrival_time = 0;
	
	int current_level = 0;
	int response_time = -1;
	int waiting_time = -1;
	int turnaround_time = -1;
	public PCB(String n, int prio, int bt, int rt, int at){
		this(prio, bt, rt, at);
		this.name = n;
	}
	
	public PCB(int prio, int bt, int rt, int at){
		this.burst_time = bt;
		this.remaining_time = rt;
		this.priority = prio;
		this.arrival_time = at;
	}
	
	public static class PCBLink{
		PCB value = null;
		PCBLink next = null;
		
		public PCBLink(PCB v){
			this.value = v;
		}
		
		public PCBLink(PCB v, PCBLink n){
			this.value = v;
			this.next = n;
		}
	}
}