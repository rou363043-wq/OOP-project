package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import logic.AccountService;
import logic.Scheduler;
import logic.SimulationClock;
import storage.DataStore;

public class RegisterFrame extends JFrame implements ActionListener {

    private AccountService accounts;
    private Scheduler scheduler;
    private SimulationClock clock;
    private DataStore store;

    private JTextField idField = new JTextField(15);
    private JTextField nameField = new JTextField(15);
    private JTextField emailField = new JTextField(15);
    private JTextField phoneField = new JTextField(15);
    private JPasswordField passField = new JPasswordField(15);
    private JButton submitButton = new JButton("Submit request");
    private JButton backButton = new JButton("Back to login");
    private JLabel messageLabel = new JLabel(" ");

    public RegisterFrame(AccountService accounts, Scheduler scheduler, SimulationClock clock, DataStore store) {
        this.accounts = accounts;
        this.scheduler = scheduler;
        this.clock=clock;
        this.store = store;

        setTitle("GPU Utilization Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("Request a student account");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 8));
        form.add(new JLabel("University ID"));
        form.add(idField);
        form.add(new JLabel("Full name"));
        form.add(nameField);
        form.add(new JLabel("Email"));
        form.add(emailField);
        form.add(new JLabel("Phone number"));
        form.add(phoneField);
        form.add(new JLabel("Password"));
        form.add(passField);
      
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(submitButton);
        buttons.add(backButton);
        messageLabel.setForeground(Color.RED);

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.add(buttons);
        bottom.add(messageLabel);

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(title);
        top.add(new JLabel("An administrator must approve your request before you can sign in."));

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        p.add(top, BorderLayout.NORTH);
        p.add(form, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        add(p);

        submitButton.addActionListener(this);
        backButton.addActionListener(this);

        setSize(520, 380);
        setLocationRelativeTo(null);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String pw = new String(passField.getPassword());
            boolean ok = accounts.register(idField.getText(), nameField.getText(), emailField.getText(),
                    phoneField.getText(), pw);
            if (ok == false) {
                messageLabel.setText(accounts.getMessage());
                  return;
            }
            if (!store.save())
                JOptionPane.showMessageDialog(this, store.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
            JOptionPane.showMessageDialog(this, "Request sent. Status: Pending.\nYou can sign in after the 
                                          administrator approves it.");
            goBack();
        }
        else if (e.getSource() == backButton) {
            goBack();
        }
    }
    private void goBack() {
        LoginFrame login = new LoginFrame(accounts, scheduler, clock, store);
        login.setVisible(true);
        dispose();
    }
}


