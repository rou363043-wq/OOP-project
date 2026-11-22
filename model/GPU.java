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

  
  
  
