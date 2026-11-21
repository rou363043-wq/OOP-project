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
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signInButton) {
            String u = userField.getText();
            String pw = new String(passField.getPassword());

            User user = accounts.login(u, pw);
            if (user == null) {
                messageLabel.setText(accounts.getMessage());
                return;
            }
            if (user.getRole().equals("Administrator")) {
                if (!clock.isSet()) {
                    boolean ok = askClock();
                    if (ok == false)
                        return;
                    if (!store.save())
                        JOptionPane.showMessageDialog(this, store.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
                }
                AdminFrame f = new AdminFrame(accounts, scheduler, clock, store, user);
                f.setVisible(true);
                dispose();
            }
            else {
                StudentFrame f = new StudentFrame(accounts, scheduler, clock, store, (Student) user);
                f.setVisible(true);
                dispose();
            }
        }
        else if (e.getSource() == registerButton) {
            RegisterFrame f = new RegisterFrame(accounts, scheduler, clock, store);
            f.setVisible(true);
            dispose();
        }
    }
    private boolean askClock() {
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));

        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        p.add(new JLabel("Date"));
        p.add(dateSpinner);
        p.add(new JLabel("Time (24 hour)"));
        p.add(timeSpinner);

        int answer = JOptionPane.showConfirmDialog(this, p, "Set the starting date and time",
                JOptionPane.OK_CANCEL_OPTION);
        if (answer != JOptionPane.OK_OPTION)
            return false;
        
        SimpleDateFormat f1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat f2 = new SimpleDateFormat("HH:mm");
        String d = f1.format((Date) dateSpinner.getValue());
        String t = f2.format((Date) timeSpinner.getValue());

        String[] a = d.split("/");
        String[] b = t.split(":");
        int day = Integer.parseInt(a[0]);
        int month = Integer.parseInt(a[1]);
        int year=Integer.parseInt(a[2]);
        int hour = Integer.parseInt(b[0]);
        int min = Integer.parseInt(b[1]);

        if (year < 2000 || year > 2100) {
            JOptionPane.showMessageDialog(this, "Choose a year between 2000 and 2100.");
            return false;
        }
        clock.setStart(new SimTime(day, month, year, hour, min));
        return true;
    }
}
        
                
    
