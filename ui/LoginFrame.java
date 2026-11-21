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

        setTitle("GPU Utilization Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("GPU Utilization Management", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel sub = new JLabel("4 x NVIDIA H100 (simulated)", JLabel.CENTER);

        JPanel p = new JPanel(new GridLayout(9, 1, 5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        p.add(title);
        p.add(sub);
        p.add(new JLabel("Username (university ID)"));
        p.add(userField);
        p.add(new JLabel("Password"));
        p.add(passField);
        p.add(signInButton);
        p.add(registerButton);
        messageLabel.setForeground(Color.RED);
        p.add(messageLabel);
        add(p);

        signInButton.addActionListener(this);
        registerButton.addActionListener(this);

        setSize(440, 400);
        setLocationRelativeTo(null);
    }
    
    
