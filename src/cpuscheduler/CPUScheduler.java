/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cpuscheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 *
 * @author fahmy
 */
public class CPUScheduler {

    public double rrQuantum;
    public int    processCounter;
    public ArrayList<Process> processes = new ArrayList<>();
    
    public static void main(String[] args) {
        // TODO code application logic here
        CPUScheduler scheduler = new CPUScheduler();
        scheduler.getInput();
//        scheduler.fcfs();
//        scheduler.sjf();
        scheduler.rr();
    }
    
    
    public void fcfs(){
        Collections.sort(this.processes,new Compare());
        double time = this.processes.get(0).arrivalTime;
        for(int i = 0 ; i < this.processes.size(); i++){
            this.processes.get(i).startTime = time;
            time += this.processes.get(i).burstTime;
            this.processes.get(i).endTime   = time;
            this.processes.get(i).responseTime = 
                    this.processes.get(i).endTime - 
                    this.processes.get(i).arrivalTime;
        }
        print(this.processes);
    }
    
    public void sjf(){
        Collections.sort(this.processes,new Compare());
        ArrayList<Process> queue = new ArrayList<>(this.processes);
        ArrayList<Process> actualOrder = new ArrayList<>();
        double time = queue.get(0).arrivalTime;
        Process running;
        while(queue.size() > 0){
            running = getMinimumProcessInQueue(queue,time);
            if(running == null){
                time = queue.get(0).arrivalTime;
                running = queue.get(0);
            }
            queue.remove(running);
            running.startTime = time;
            running.endTime = time + running.burstTime;
            running.responseTime = running.endTime-running.arrivalTime;
            actualOrder.add(running);
            time = running.endTime;    
        }
        print(actualOrder);
    }
    
    public void rr(){
        Collections.sort(processes,new Compare());
        ArrayList<rrDetail> actualOrder = new ArrayList<>();
        double rem = 1;
        double time = processes.get(0).arrivalTime;
        while(rem > 0){
            rem = 0;
            int idx = 0;
            double x = time;
            for(;idx < processes.size();idx++){
                rem += processes.get(idx).remainingTime;
                if(processes.get(idx).remainingTime>0 &&
                        processes.get(idx).arrivalTime <= time){
                    double q = Math.min(processes.get(idx).remainingTime
                            , this.rrQuantum);
                    time += q;
                    processes.get(idx).remainingTime -= q;
                    
                    rrDetail tmp = new rrDetail();
                    tmp.processName = processes.get(idx).name;
                    tmp.quantum = q;
                    actualOrder.add(tmp);
                }
            }
            if(time == x && rem > 0){
                for(int i = 0 ; i < processes.size(); i++){
                    if(processes.get(i).remainingTime > 0){
                        time = processes.get(i).arrivalTime;
                        break;
                    }
                }
            }
        }
        for(int i = 0; i < actualOrder.size(); i++){
            System.out.println(actualOrder.get(i).processName + " => " +
                    actualOrder.get(i).quantum);
        }
    }
    
    
    public Process getMinimumProcessInQueue(ArrayList<Process> queue,double time){
        ArrayList<Process> temp = new ArrayList<>();
        for(int i = 0 ; i < queue.size(); i++){
            if(queue.get(i).arrivalTime <= time){
                temp.add(queue.get(i));
            }else if(temp.size() == 0){
                return null;
            }
        }
        Collections.sort(temp,new CompareBurst());
        return temp.get(0);
    }
    
    public void print(ArrayList<Process> out){
        double avg = 0;
        for(int i = 0; i < out.size(); i++){
            System.out.println("Name: " + out.get(i).name);
            System.out.println("arrival: " + out.get(i).arrivalTime);
            System.out.println("burst: " + out.get(i).burstTime);
            System.out.println("start: " + out.get(i).startTime);
            System.out.println("end: " + out.get(i).endTime);
            System.out.println("response: " + out.get(i).responseTime);
            System.out.println("");
            avg += out.get(i).responseTime;
        }
        System.out.println("Average : " + (avg/out.size()));
    }
    
    
    public void getInput(){
        Scanner input = new Scanner(System.in);
        
        System.out.println("Number of processes : ");
        int n = input.nextInt();
        this.processCounter = n;
        
        System.out.println("round robin quantum : ");
        double d = input.nextDouble();
        this.rrQuantum = d;
        input.nextLine();//buffer escape \n
        
        System.out.println("Enter processes data");
        for(int i = 0; i < n; i++){
            System.out.println("Process #" + (i+1));
            Process temp = new Process();
            
            System.out.print("Name: ");
            String in = input.nextLine();
            temp.name        = in;
            
            System.out.print("Arrival: ");
            d = input.nextDouble();
            temp.arrivalTime = d;
            
            System.out.print("Burst Time: ");
            d = input.nextDouble();
            temp.burstTime   = d;
            temp.remainingTime = d;
            
            this.processes.add(temp);
        
            input.nextLine();//buffer escape \n
            System.out.println("");
        }
    }
    
}

class Compare implements Comparator<Process>{

    @Override
    public int compare(Process o1, Process o2) {
        if(o1.arrivalTime < o2.arrivalTime)return -1;
        if(o1.arrivalTime > o2.arrivalTime)return  1;
        
        if(o1.burstTime < o2.burstTime)return -1;
        if(o1.burstTime > o2.burstTime)return  1;
        
        return 0;
    }
    
}

class CompareBurst implements Comparator<Process>{

    @Override
    public int compare(Process o1, Process o2) { 
        if(o1.burstTime < o2.burstTime)return -1;
        if(o1.burstTime > o2.burstTime)return  1;
        
        return 0;
    }
    
}

class rrDetail{
    public String processName;
    public double quantum;
}