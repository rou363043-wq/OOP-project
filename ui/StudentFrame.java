package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import logic.AccountService;
import logic.Scheduler;
import logic.SimulationClock;
import model.Job;
import model.Student;
import model.TaskSize;
import storage.DataStore;

public class StudentFrame extends JFrame implements ActionListener {

    private AccountService accounts;
    private Scheduler scheduler;
    private SimulationClock clock;
    private DataStore store;
    private Student me;

    private JLabel timeLabel = new JLabel();
    private JButton logoutButton = new JButton("Log out");

    private JLabel allocatedLabel = new JLabel("0", JLabel.CENTER);
    private JLabel usedLabel = new JLabel("0", JLabel.CENTER);
    private JLabel reservedLabel = new JLabel("0", JLabel.CENTER);
    private JLabel availableLabel = new JLabel("0", JLabel.CENTER);

    private JTextField titleField = new JTextField(20);
    private JTextField appField = new JTextField(20);
    private JTextArea descArea = new JTextArea(4, 40);
    private JComboBox<String> sizeBox = new JComboBox<String>();
    private JLabel durationLabel = new JLabel();
    private JButton submitButton = new JButton("Submit job");

    private JTextField searchField = new JTextField(18);
    private JComboBox<String> statusBox = new JComboBox<String>();
    private JButton searchButton = new JButton("Search");
    private JButton cancelButton = new JButton("Cancel selected job");
    private ReadOnlyTableModel jobModel = new ReadOnlyTableModel(JobRows.columns);
    private JTable jobTable = new JTable(jobModel);
