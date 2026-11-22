package logic;
import model.SimTime;

public class SimulationClock{
  private SimTime startTime;
  private SimTime now;
  
  public boolean isSet(){
    return now!=null;
  }

  public boolean setStart(SimTime start){
    if(now !=null)
      return false;
    startTime=start;
    now=start;
    return true;
  }
  
  public void advance(){
    if(now!=null)
      now=now.plusMinutes(1);
  }

  public void restore(SimTime savedStart, SimTime savedNow){
    startTime=savedStart;
    now=savedNow;
  }

  public SimTime getNow(){
    return now;
  }

  public SimTime getStartTime(){
    return startTime;
  }

  public int getElapsedMinutes(){
    if(now==null)
      return 0;
    return startTime.minutesUntil(now);
  }  
}
