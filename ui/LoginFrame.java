package ui;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

import logic.AccountService;
import logic.Scheduler;
import logic.SimulationClock;
import storage.DataStore;
import model.SimTime;
import model.Student;
import model.User;

public class LoginFrame extends JFrame{

  private JTextField userField = new JTxtField(15);
  private JPasswordField passField = new JPasswordField(15);
  private JButton signInButton = new JButton("Sign in");
  private JButton registerButton = new Jbutton("New student? Request an account");
  private JLabel messageLabel = new JLabel(" ");

  public LoginFrame(){
    
