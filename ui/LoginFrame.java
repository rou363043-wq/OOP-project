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

public class LoginFrame extends JFrame implements ActionListener {

    private AccountService accounts;
    private Scheduler scheduler;
    private SimulationClock clock;
    private DataStore store;

    private JTextField userField = new JTextField(15);
    private JPasswordField passField = new JPasswordField(15);
    private JButton signInButton = new JButton("Sign in");
    private JButton registerButton = new JButton("New student? Request an account");
    private JLabel messageLabel = new JLabel(" ");

    public LoginFrame(AccountService accounts, Scheduler scheduler, SimulationClock clock, DataStore store) {
        this.accounts = accounts;
        this.scheduler=scheduler;
        this.clock = clock;
        this.store=store;
    
