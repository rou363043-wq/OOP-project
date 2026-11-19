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

public int getAllocated(){ return allocated;}
public int getUsed(){
    return used;
}
public int getReserved(){return reserved;}

public int getAvailable(){
  int a = allocated - used - reserved;
  return a;
}
public boolean reserve(int minutes){
  reserved = reserved - minutes;
  if( reserved < 0)
      reserved = 0;
}
public void charge(int minutes){
  if(minutes < 0)
      return;
  used = used + minutes;
  if( used > allocated - reserved)
      used = allocated - reserved;
}

public void addAllcated(int minutes){
  allocated = allocated + minutes;
}
  
  
