package logic;

import model.SimTime;
import model.Student;
import model.TaskSize;
import model.User;

public class Scheduler{
  private GPU[] gpus= new GPU[4];
  private ArrayList<Job> jobs= new ArrayList<Job>();
  private SimulationClock clock;
  private AccountService accounts;
  private int nextJobNumber= 101;
  private String message= "";

  public Scheduler(SimulationClock clock, AccountService accounts){
    this.clock= clock;
    this.account=accounts;
    for(int i=0; i<4; i++){
      gpu[i]= new GPU(i+1);
    }
  }

  public String getMessage(){
    return message;
  }

  public GPU[] getGpus(){
    return gpus;
  }

  public ArrayList<Job> getJobs(){
    return jobs;
  }

  public int getNextJobNumber(){
    return nextJobNumber;
  }

  public boolean restore(ArrayList<Job> saved, int SavedNext){
    jobs= saved;
    nextJobNumber= savedNext();
    for(int g=0; g<4; g++){
      gpus[g].release();
    }
    for(int i=0; i<jobs.size(); i++){
      Job j= jobs.get(i);
      if(j.isRunning()){
        int id= j.getAssignedGpuId();
        if(!gpus[id-1].isFree())
          return false;
        gpus[id-1].assign(j);
      }
      if(j.getJobNumber() >= nextJobNumber){
        nextJobNumber= j.getJobNumber()+1;
      }
    }
    return true
  }

  public Job findJob(String jobId){
    for(int i=0; i<jobs.size(); i++){
      Job j= jobs.get(i);
      if(j.getStudentId().equals(studentId))
        list.add(j);
    }
    return list;
  }

  






  
}
