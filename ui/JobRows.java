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
    else{
        if(j.getStartTime() !=null){
            gpu ="GPU" + j.getAssignedGpuId();
            start = j.getStartTime().toString();
            finish = j.getFinishTime().toString();
            wait = j.getQueueWait() + "min";
        }
        result = "Cancelled by" + j.getCancelledBy() + " at " + j.getCancelTime()
                + ",charged " + j.getCharged() + " min. Reason: " + j.getCancelReason();
    } 
    Object[] row = {j.getJobId(),j.getStudentId(),j.getTitle(), j.getApplicationName(),
                    j.getSubmissionTime().toString(),Start, finish, wait, result};
    return row;
  }
  public static boolean matches(Job j, String text, String status){
    if(!status.equals("All statuses") && !j.getStatus().equals(status))
        return false;
    text = text.trim().toLowerCase();
    if(text.length() == 0)
        return true;
    if(j.getJobId().toLowerCase().contains(text))
        return true;
    if(j.getTitle().toLowerCase().contains(text))
        return true;
    if(j.getStudentId().contains(text))
        return true;
    retrun false;
  }
  public static boolean askAndCancel(Component parent,Scheduler scheduler, String jobId, User who){
      Job j = scheduler.findJob(jobId);
      if( j == null)
          return false;
      if(!j.isQueued() && !j.isRunning()){
          JOptionPane.showMessageDialog(parent,"Only queued or running jobs can be cancelled.\n"
                      + jobId + " is " + j.getStatus().toLowerCase() + ".");
          return false;
      }
    String text;
    if(j.isQueued())
        text = jobId + " is queued. Nothing will be charged.";
    else{
        int ran = scheduler.getCancelCharge(j);
        int back = j.getEstimatedDuration() - ran;
        text = jobId + " is running on GPU" + j.getAssignedGpuId() + " and has run " + ran + " min.\n"
                  + ran + " min will be charged. The other " + back + " min return to the balance.";
    }

    String reason = JOptionPane.showInputDialog(parent, text +"\Reason (Required):",
                "Cancel job " + jobId, JOptionPane.QUESTION_MESSAGE);
    if(reason == null)
          return false;
    if(scheduler.cancel(jobId, who, reason) == false){
        JOptionPane.showMessageDialog(parent, scheduler.getMessage(),"Job not cancelled",JOtionPane.ERROR_MESSAGE);
        return fslse;
    }
    return true;
  }
}

      
    
    
    
