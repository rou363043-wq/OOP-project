package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import logic.AccountService;
import logic.Prediction;
import logic.Scheduler;
import logic.SimulationClock;
import model.AccessRequest;
import model.GPU;
import model.Job;
import model.SimTime;
import model.Student;
import model.TimeAllowance;
import model.User;
import storage.DataStore;

public class AdminFrame extends JFrame implements ActionListener {

    private AccountService accounts;
    private Scheduler scheduler;
    private SimulationClock clock;
    private DataStore store;
    private User admin;

    private JTabbedPane tabs = new JTabbedPane();
    private JLabel timeLabel = new JLabel();
    private JButton plusButton = new JButton("+1 minute");
    private JButton logoutButton = new JButton("Log out");

    private JLabel runningLabel = new JLabel("0", JLabel.CENTER);
    private JLabel queuedLabel = new JLabel("0", JLabel.CENTER);
    private JLabel completedLabel = new JLabel("0", JLabel.CENTER);
    private JLabel cancelledLabel = new JLabel("0", JLabel.CENTER);
    private JLabel busyLabel = new JLabel("0 / 4", JLabel.CENTER);
    private JLabel utilLabel = new JLabel("N/A", JLabel.CENTER);
    private ReadOnlyTableModel gpuModel;
    private ReadOnlyTableModel jobModel;
    private ReadOnlyTableModel doneModel;
    private ReadOnlyTableModel balanceModel;

    private ReadOnlyTableModel requestModel;
    private JTable requestTable;
    private JTextField minutesField = new JTextField("" + TimeAllowance.DEFAULT_MINUTES, 6);
    private JButton approveButton = new JButton("Approve");
    private JButton rejectButton = new JButton("Reject");

    private JTextField idField = new JTextField(12);
    private JButton findButton = new JButton("Search");
    private JLabel studentLabel = new JLabel("Type a university ID and press Search.");
    private String shownId = "";
    private JLabel sAllocated = new JLabel("", JLabel.CENTER);
    private JLabel sUsed = new JLabel("", JLabel.CENTER);
    private JLabel sReserved = new JLabel("", JLabel.CENTER);
    private JLabel sAvailable = new JLabel("", JLabel.CENTER);
    private JTextField grantField = new JTextField(5);
    private JTextField reasonField = new JTextField(30);
    private JButton grantButton = new JButton("Grant");
    private ReadOnlyTableModel studentJobModel = new ReadOnlyTableModel(JobRows.columns);
    private JTable studentJobTable = new JTable(studentJobModel);
    private JButton cancelStudentJobButton = new JButton("Cancel selected job");

    private JTextField searchField = new JTextField(18);
    private JComboBox<String> statusBox = new JComboBox<String>();
    private JButton searchButton = new JButton("Search");
    private JButton cancelJobButton = new JButton("Cancel selected job");
    private ReadOnlyTableModel allModel = new ReadOnlyTableModel(JobRows.columns);
    private JTable allTable = new JTable(allModel);

    public AdminFrame(AccountService accounts, Scheduler scheduler, SimulationClock clock, DataStore store, User admin) {
        this.accounts = accounts;
        this.scheduler = scheduler;
        this.clock=clock;
        this.store = store;
        this.admin = admin;

        setTitle("GPU Utilization Management (Administrator)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("  GPU Utilization Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        right.add(timeLabel);
        right.add(plusButton);
        right.add(logoutButton);
        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        tabs.addTab("Dashboard", makeDashboardTab());
        tabs.addTab("Requests", makeRequestsTab());
        tabs.addTab("Students", makeStudentsTab());
        tabs.addTab("All jobs", makeAllJobsTab());
        add(tabs, BorderLayout.CENTER);

        plusButton.addActionListener(this);
        logoutButton.addActionListener(this);
        approveButton.addActionListener(this);
        rejectButton.addActionListener(this);
        findButton.addActionListener(this);
        grantButton.addActionListener(this);
        cancelStudentJobButton.addActionListener(this);
        searchButton.addActionListener(this);
        statusBox.addActionListener(this);
        cancelJobButton.addActionListener(this);

        setSize(1300, 760);
        setLocationRelativeTo(null);
        refresh();
    }
    private JPanel makeDashboardTab() {
        JPanel summary = new JPanel(new GridLayout(1, 6, 8, 8));
        addTile(summary, runningLabel, "Running");
        addTile(summary, queuedLabel, "Queued");
        addTile(summary, completedLabel, "Completed");
        addTile(summary, cancelledLabel, "Cancelled");
        addTile(summary, busyLabel, "Busy GPUs");
        addTile(summary, utilLabel, "Utilization");

        String[] c1 = {"GPU", "Availability", "Current job", "Remaining", "Expected finish"};
        gpuModel = new ReadOnlyTableModel(c1);
        String[] c2 = {"Job", "Student", "Application", "Status", "Position", "Start", "Finish", "Wait"};
        jobModel = new ReadOnlyTableModel(c2);
        String[] c3 = {"Job", "Student", "GPU", "Submitted", "Start", "Finish", "Runtime", "Queue wait", "Charged"};
        doneModel = new ReadOnlyTableModel(c3);
        String[] c4 = {"Student", "Name", "Allocated", "Used", "Reserved", "Available"};
        balanceModel=new ReadOnlyTableModel(c4);

        JPanel firstRow = new JPanel(new GridLayout(1, 2, 8, 8));
        firstRow.add(makeTable(gpuModel, "GPU status"));
        firstRow.add(makeTable(balanceModel, "Student allowances"));
        JPanel center = new JPanel(new GridLayout(3, 1, 8, 8));
        center.add(firstRow);
        center.add(makeTable(jobModel, "Running and waiting jobs (start and finish of waiting jobs are predicted)"));
        center.add(makeTable(doneModel, "Completed jobs"));

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.add(summary, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JPanel makeRequestsTab() {
        String[] c = {"University ID", "Name", "Email", "Phone", "Password", "Status"};
        requestModel = new ReadOnlyTableModel(c);
        requestTable = new JTable(requestModel);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("Initial GPU minutes for the selected request:"));
        bottom.add(minutesField);
        bottom.add(approveButton);
        bottom.add(rejectButton);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.add(new JScrollPane(requestTable), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }
    private JPanel makeStudentsTab() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(new JLabel("University ID"));
        bar.add(idField);
        bar.add(findButton);
        bar.add(studentLabel);

        JPanel tiles = new JPanel(new GridLayout(1, 4, 8, 8));
        addTile(tiles, sAllocated, "Allocated");
        addTile(tiles, sUsed, "Used");
        addTile(tiles, sReserved, "Reserved");
        addTile(tiles, sAvailable, "Available");

        JPanel grant = new JPanel(new FlowLayout(FlowLayout.LEFT));
        grant.setBorder(BorderFactory.createTitledBorder("Grant extra time"));
        grant.add(new JLabel("Minutes"));
        grant.add(grantField);
        grant.add(new JLabel("Reason (required)"));
        grant.add(reasonField);
        grant.add(grantButton);

        JPanel top = new JPanel(new GridLayout(3, 1, 4, 4));
        top.add(bar);
        top.add(tiles);
        top.add(grant);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(cancelStudentJobButton);
        bottom.add(new JLabel("  * predicted time"));

        JobRows.setWidths(studentJobTable);
        JScrollPane sp = new JScrollPane(studentJobTable);
        sp.setBorder(BorderFactory.createTitledBorder("Jobs of this student"));

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.add(top, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }
        private JPanel makeAllJobsTab() {
        statusBox.addItem("All statuses");
        statusBox.addItem("Queued");
        statusBox.addItem("Running");
        statusBox.addItem("Completed");
        statusBox.addItem("Cancelled");

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(new JLabel("Search by job ID, student ID or title"));
        bar.add(searchField);
        bar.add(statusBox);
        bar.add(searchButton);
        bar.add(cancelJobButton);

        JobRows.setWidths(allTable);
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.add(bar, BorderLayout.NORTH);
        p.add(new JScrollPane(allTable), BorderLayout.CENTER);
        p.add(new JLabel("  * predicted time"), BorderLayout.SOUTH);
        return p;
    }
     private JPanel makeAllJobsTab() {
        statusBox.addItem("All statuses");
        statusBox.addItem("Queued");
        statusBox.addItem("Running");
        statusBox.addItem("Completed");
        statusBox.addItem("Cancelled");

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(new JLabel("Search by job ID, student ID or title"));
        bar.add(searchField);
        bar.add(statusBox);
        bar.add(searchButton);
        bar.add(cancelJobButton);

        JobRows.setWidths(allTable);
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.add(bar, BorderLayout.NORTH);
        p.add(new JScrollPane(allTable), BorderLayout.CENTER);
        p.add(new JLabel("  * predicted time"), BorderLayout.SOUTH);
        return p;
    }
    private void addTile(JPanel panel, JLabel label, String name) {
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setBorder(BorderFactory.createTitledBorder(name));
        panel.add(label);
    }

    private JScrollPane makeTable(ReadOnlyTableModel model, String name) {
        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder(name));
        return sp;
    }
     private int toNumber(String text) {
        text = text.trim();
        if (text.length() == 0 || text.length() > 6)
            return -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) < '0' || text.charAt(i) > '9')
                return -1;
        }
        return Integer.parseInt(text);
    }
    private void saveNow() {
        if (store.save() == false)
            JOptionPane.showMessageDialog(this, store.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        
    


  
