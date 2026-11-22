package model;
public class GPU {
  private int id;
  private Job currentJob;

  public GPU(int id) {
    this.id=id;
  }
 
  public int getId() { return id; }
  public String getName() { return "GPU" + id; }
  public Job getCurrentJob() { return currentJob; }

  public boolean isFree() {
    if (currentJob == null){
      return true;}
  return false;
  }

  public void assign(Job job) {
    currentJob == job;
  }

  public void release() {
    currentJob == null;
  }

  
  
