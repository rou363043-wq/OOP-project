package model;

public class TimeAllowance{
  public static final int DEFAULT_MINUTES = 100;

  private int allocated;
  private int used;
  private int reserved;

public TimeAllowance(int allocated){
  this.allocated = allocated;
  used=0;
  reserved=0;
}

public TimeAllowance(int allocated ,int reserved){
  this.allocated = allocated;
  this.used = used;
  this.reserved = reserved;
}

public int getAllocated(){ return allocated
  
