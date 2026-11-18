package ui;

import java.awt.*;
import javax.swing.*;

public class LoginFrame extends JFrame{

  private JTextField userField = new JTxtField(15);
  private JPasswordField passField = new JPasswordField(15);
  private JButton signInButton = new JButton("Sign in");
  private JButton registerButton = new Jbutton("New student? Request an account");
  private JLabel messageLabel = new JLabel(" ");

  public LoginFrame(){
    
