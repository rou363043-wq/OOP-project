

public class Prediction{
  private Job job;
  private int position;
  private SimTime startTime;
  private int waitMinutes;

  public Prediction(Job job, int position, SimTime startTime, SimTime finishTime, int waitMinutes){
    this.job=job;
    this.position=position;
    this.startTime=startTime;
    this.finishTime=finishTime;
    this.waitMinutes=waitMinutes; 
  }

  public Job getJob(){
    return job;
  }
  public int getPosition(){
    return position;
  }
  public SimTime getStartTime(){
    return startTime;
  }
  public SimTime getFinishTime(){
    return finishTime;
  }

  public int getWaitMinutes(){
    return waitMinutes;
  }
  
}
