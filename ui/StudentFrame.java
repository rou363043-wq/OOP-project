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

    public StudentFrame(AccountService accounts, Scheduler scheduler, SimulationClock clock, DataStore store, Student me) {
        this.accounts = accounts;
        this.scheduler = scheduler;
        this.clock=clock;
        this.store = store;
        this.me = me;

        setTitle("GPU Utilization Management (Student)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel welcome = new JLabel("  Welcome, " + me.getFullName() + "   (Student " + me.getUniversityId() + ")");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 16));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        right.add(timeLabel);
        right.add(logoutButton);
        JPanel header = new JPanel(new BorderLayout());
        header.add(welcome, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel tiles = new JPanel(new GridLayout(1, 4, 8, 8));
        addTile(tiles, allocatedLabel, "Allocated");
        addTile(tiles, usedLabel, "Used");
        addTile(tiles, reservedLabel, "Reserved");
        addTile(tiles, availableLabel, "Available");

        sizeBox.addItem(TaskSize.SMALL);
        sizeBox.addItem(TaskSize.MEDIUM);
        sizeBox.addItem(TaskSize.LARGE);
        sizeBox.addItem(TaskSize.VERY_LARGE);

        JPanel line1 = new JPanel(new GridLayout(2, 2, 8, 2));
        line1.add(new JLabel("Job title"));
        line1.add(new JLabel("Application name"));
        line1.add(titleField);
        line1.add(appField);

        JPanel line2 = new JPanel(new BorderLayout());
        line2.add(new JLabel("Description"), BorderLayout.NORTH);
        descArea.setLineWrap(true);
        line2.add(new JScrollPane(descArea), BorderLayout.CENTER);

        JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line3.add(new JLabel("Task size"));
        line3.add(sizeBox);
        line3.add(durationLabel);
        line3.add(submitButton);

        JPanel form = new JPanel(new BorderLayout(8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Submit a new job"));
        form.add(line1, BorderLayout.NORTH);
        form.add(line2, BorderLayout.CENTER);
        form.add(line3, BorderLayout.SOUTH);

        JPanel home = new JPanel(new BorderLayout(8, 8));
        home.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        home.add(tiles, BorderLayout.NORTH);
        home.add(form, BorderLayout.CENTER);

        statusBox.addItem("All statuses");
        statusBox.addItem("Queued");
        statusBox.addItem("Running");
        statusBox.addItem("Completed");
        statusBox.addItem("Cancelled");

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(new JLabel("Search by job ID or title"));
        bar.add(searchField);
        bar.add(statusBox);
        bar.add(searchButton);
        bar.add(cancelButton);

        JobRows.setWidths(jobTable);
        JPanel myJobs = new JPanel(new BorderLayout(8, 8));
        myJobs.add(bar, BorderLayout.NORTH);
        myJobs.add(new JScrollPane(jobTable), BorderLayout.CENTER);
        myJobs.add(new JLabel("  * predicted time. Showing only your own jobs."), BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Home", home);
        tabs.addTab("My jobs", myJobs);
        add(tabs, BorderLayout.CENTER);

        logoutButton.addActionListener(this);
        sizeBox.addActionListener(this);
        submitButton.addActionListener(this);
        searchButton.addActionListener(this);
        statusBox.addActionListener(this);
        cancelButton.addActionListener(this);

        setSize(1250, 560);
        setLocationRelativeTo(null);
        refresh();
    }
