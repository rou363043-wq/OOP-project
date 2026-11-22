package logic;

import java.util.ArrayList;

import model.GPU;
import model.Job;
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
    this.accounts=accounts;
    for(int i=0; i<4; i++){
      gpus[i]= new GPU(i+1);
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
    nextJobNumber= SavedNext;
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
    return true;
  }

  public Job findJob(String jobId){
    for(int i=0; i<jobs.size(); i++){
      Job j= jobs.get(i);
      if(j.getJobId().equals(jobId))
        return j;
    }
    return null;
  }
  
  public ArrayList<Job> getJobsOf(String studentId){
    ArrayList<Job> list= new ArrayList<Job>();
    for(int i=0; i<jobs.size(); i++){
      Job j= jobs.get(i);
      if(j.getStudentId().equals(studentId))
        list.add(j);
    }
    return list;
  }

  public ArrayList<Job> getQueue(){
    ArrayList<Job> q= new ArrayList<Job>();
    for(int i=0; i<jobs.size(); i++){
      if(jobs.get(i).isQueued())
        q.add(jobs.get(i));
    }

    for(int i=0; i<q.size(); i++){
      for(int j=i+1; j<q.size(); j++){
        Job a=q.get(i);
        Job b=q.get(j);
        int ta=a.getSubmissionTime().getTotal();
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
      message="Only approved students can submit jobs.";
      return null;
    }
    if(!clock.isSet()){
      message="The clock is not set yet. Jobs cannot be submitted.";
      return null;
    }
    if(title.trim().length()==0 || app.trim().length()==0 || desc.trim().length()==0){
      message="Enter the title, the application name and the description.";
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

  public void advanceMinute(){
    if(!clock.isSet()){
      message="Set the clock first.";
      return;
    }
    clock.advance();
    SimTime now= clock.getNow();
    for(int g=0; g<4; g++){
      Job job= gpus[g].getCurrentJob();
      if(job!=null){
        if(job.getRuntime(now)>= job.getEstimatedDuration()){
          job.complete(now);
          settle(job);
          gpus[g].release();
        }
      }
    }
    startWaitingJobs();
  }

  private void settle(Job job){
    if(job.markSettled()==false)
      return;
    Student owner= accounts.findStudent(job.getStudentId());
    if(owner==null)
      return;
    owner.getAllowance().release(job.getEstimatedDuration());
    int ran= job.getRuntime(clock.getNow());
    owner.getAllowance().charge(ran);
  }

  public boolean cancel(String jobId, User who, String reason){
    Job job= findJob(jobId);
    if(job== null){
      message="No job with ID "+  jobId + ".";
      return false;
    }
    if(who.getRole().equals("Student") && !job.getStudentId().equals(who.getUsername()){
      message="You can cancel only your own jobs.";
      return false;
    }
    if(reason.trim().length()==0){
      message="A reason is required.";
      return false;
    }
    boolean wasRunning=job.isRunning();
    int gpuId=job.getAssignedGpuId();
    reason= reason.trim().replace(";",",").replace("\n", " ");
    if(!job.cancel(who.getUsername(), clock.getNow(), reason)){
      message= jobId+ " is already " +job.getStatus().toLowerCase() + ".";
      return false;
    }
    settle(job);

    if(wasRunning){
      gpus[gpuId-1].release();
      startWaitingJobs();
    }
    message= jobId+" cancelled.";
    return true;    
  }

  public int getCancelCharge(Job job){
    if (job.isRunning())
      return job.getRuntime(clock.getNow());
    return 0;
  }
  public ArrayList<Prediction> predictAll(){
    ArrayList<Prediction> result= new ArrayList<Prediction>();
    if(!clock.isSet())
      return result;
    SimTime now= clock.getNow();
    int[] freeAt= new int[4];
    for(int g=0; g<4; g++){
      if (gpus[g].isFree())
        freeAt[g]= now.getTotal();
      else
        freeAt[g]=now.getTotal()+gpus[g].getRemaining(now);
    }

    ArrayList<Job> q= getQueue();
    for(int i=0; i<q.size(); i++){
      Job job=q.get(i);
      int best=0;
      for(int g=1; g<4; g++){
        if(freeAt[g]< freeAt[best])
          best=g;
      }
      SimTime start= new SimTime(freeAt[best]);
      SimTime finish= start.plusMinutes(job.getEstimatedDuration());
      int wait= now.minutesUntil(start);
      Prediction p= new Prediction(job, i+1, start, finish, wait);
      result.add(p);
      freeAt[best]=finish.getTotal();
    }
    return result;
  }

  public Prediction getPrediction(Job job){
    ArrayList<Prediction> all= predictAll();
    for(int i=0; i<all.size(); i++){
      if (all.get(i).getJob()==job)
        return all.get(i);
    }
    return null;
  }

  public int countByStatus(String status){
    int count=0;
    for(int i=0; i<jobs.size(); i++){
      if(jobs.get(i).getStatus().equals(status))
        count++;
    }
    return count;
  }

  public int countBusyGpus(){
    int busy=0;
    for(int g=0; g<4; g++){
      if(!gpus[g].isFree())
        busy++;
    }
    return busy;
  }

  public int getTotalBusyMinutes(){
    if(!clock.isSet())
      return 0;
    int total=0;
    for(int i=0; i<jobs.size(); i++){
      total= total + jobs.get(i).getRuntime(clock.getNow());
    }
    return total;
  }

  public String getUtilizationText(){
    int elapsed= clock.getElapsedMinutes();
    if(elapsed==0)
      return "N/A";
    double u= getTotalBusyMinutes() * 100.0/(4*elapsed);
    return String.format("%.1f%%", u);
  }
  
}
