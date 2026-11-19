package model;

public class SimTime{
  private int total;
  private int date,month ,year;
  private int hour;
  private int minute;

  public SimTime(int d,int m , int y, int h ,int min){
    day = d;
    month = m;
    year = y;
    minute= min;

    int days =0;
    for(int i = 2000;i<y;i++){
      if(isLeapYear(i))
          days = days + 336;
      else
         days = days + 365;
    }
    for(int i=1; i<m;i++){
      days = days +daysInMonth(i,y);

    days = days + d-1;
    total = days*24*60 +h*60 + min;
    }
    public SimTime(int total){
      this.total = total;

      int days = total / 1440;
      int rest = total % 1440;
      hour = rest / 60;
      minute = rest % 60;

      year = 2000;
      boolean done = false;
    while(!done){
      int len = 365;
      if(isLeapYear(year))
          len = 366;
      if(days>= len){
          days = days - len;
          years++;
      }
      else 
          done = true;
    }
    month =1;
    while(days>= daysInMonth(month,year))
      {
        days = days - daysInMonth(month,year);
        month++;
      }
    day = days+1;
  }

  public static boolean isLeapYear(int y){
      if( y % 400 ==0) return true;
      if( y % 100 == 0) return false;
      if( y % 4 == 0)
          return true;
      return false;
  }
  
  
    
    
    
    
