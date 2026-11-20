package ui;

import java.awt.component;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import logic.Predoiction;
import logic.Scheduler;
import model.Job;
import model.SimTime;
import model.User;

public class JobRows{
  public static String[] columns={"Job","Student","Tittle","Application","Size","Status",
                                  "Position","GPU","Submitted","Start","Finish","Wait","Result"};
  public static void setWidths(JTable table){
    int[] w = {50,90,160,100,90,80,60,50,130,140,140,60,520};
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    for( int i=0; i < w.length; i++)
        table.getColumnMode().getColumn(i).setPreferredWidth(w[i]);
  }
  public static Object[] makeRow(Job j , Scheduler scheduler, SimTime now){
    String position="";
    String gpu ="";
    String start 
