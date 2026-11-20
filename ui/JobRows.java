package ui;

import java.awt.component;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import logic.Predoiction;
import logic.Scheduler;
import model.Job;
import model.SimTime;
import model.User;

public class JobRows{
  public static String[] columns={"Job","Student","Tittle","Application","Size","Status",
                                  "Position","GPU","Submitted","Start","Finish","Wait","Result"};
  public static void setWidths(JTable table){
    int[] w = {50,90,160,100,90,80,60,50,130,140,140,60,520};
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    for( int i=0; i < w.length; i++)
        table.getColumnMode().getColumn(i).setPreferredWidth(w[i]);
  }
  public static Object[] makeRow(Job j , Scheduler scheduler, SimTime now){
    String position="";
    String gpu ="";
    String start ="";
    String finish ="";
    String wait ="";
    String result ="";

    if(j.isQueued()){
        Prediction p = scheduler.getPrediction(j);
        if(p != null){
            position = "" + p.getPosition();
            start = p.getStartTime()+ "*";
            finish = p.getFinishTime()+ "*";
            wait = p.getWaitMinutes() + " min";
        }
    }
    else if (j.isRunning()){
      gpu ="GPU" + j.getAssignedGpuId();
      start = j.getStartTime().toString();
      finish = j.getStartTime().plusMinutes(j.getEstimatedDuration()) + "*";
      wait = j.getQueueWait()+ "min";
      result = "Ran" + j.getRuntime(now) + " of " + j.getEstimatedDuration() + "min";
    }
     else if( j.isRunning()){
         gpu ="GPU" + j.getAssignedGpuId();
         start = j.getStartTime().toString;
         finish = j.getStartTime().plusMinutes(j.getEstimatedDuration() + " min";
         wait = j.getQueueWait() + "min";
         result = "Ran" + j.getRuntime(now) + " of " + j.getEstimatedDuration() + "min";
     }
       
    else if (j.getStatus().equals("Completed")){
        gpu = "GPU" + j.getAssignedGpuId();
        start = j.getStartTime().toString();
        finish = j.getFinishTime().toString();
        wait = j.getQueueWait() + "min";
        result = "Completed,charged" + j.getCharged() + " min";
    }
