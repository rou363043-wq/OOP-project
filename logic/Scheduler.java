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

  publicc ArrayList<Job> getQueue(){
    ArrayList<Job> q= new ArrayList<Job>();
    for(int i=0; i<jobs.size(); i++){
      if(jobs.get(i).isQueued())
        q.add(jobs.get(i));
    }

    for(int i=0; i<q.size(); i++){
      for(int j=i+1; j<q.size(); j++){
        Job a=q.get(i);
        Job b=q.get(j);
        int ta=a.getSubmssionTime().getTotal();
        int tb=b.getSubmissionTime().getTotal();
        boolean swap=false;
        if(tb<ta)
          swap=true;
        if(tb==ta && b.getJobNumber() <a.getJobNumber())
          swap=true;
        if(swap){
          q.set(i, b);
          q.set(j, a);
        }
      }
    }
    return q;
  }

  public Job submit(User who, String title, String app, String desc, String size){
    if(!who.getRole().equals("Student")){
      message="Only approved students can submut jobs.";
      return null;
    }
    if(!clock.isSet()){
      message="The clock is not set yet. Jobs cannot be submitted.";
      return null;
    }
    if(title.trim().length()==0 || app.trim().length()==0 || desc.trim().length()==0){
      message="Enter the titke, the application name and the description.";
      return null;
    }
    int duration= TaskSize.unitsFor(size);
    if(duration ==0){
      message= "Select a task size.";
      return null;
    }

    Student st= (Student) who;
    if(st.getAllowance().reserve(duration)==false){
      message= "This job needs "+ duration+" min, but you have only "+ st.getAllowance().getAvailable()
        +" min available.";
      return null;
    }

    title=title.trim().replace(";",",").replace("\n"," ");
    app= app.trim().replace(";", ",").replace("\n"," ");
    desc= desc.trim().replace(";",",").replace("\n", " ");

    Job job= new Job(nextJobNumber, st.getUniversityId(), title, app, desc, size, clock.getNow());
    nextJobNumber++;
    jobs.add(job);
    startWaitingJobs();
    message="Job " + job.getJobId() + " accepted.";
    return job;
  }

  private void startWaitingJobs(){
    ArrayList<Job> q= getQueue();
    int k=0;
    for(int g=0; g<4; g++){
      if(gpus[g].isFree() && k<q.size()){
        Job job= q.get(k);
        job.start(gpus[g].getId(), clock.getNow());
        gpus[g].assign(job);
        k++;
      }
    }
  }






  
}
