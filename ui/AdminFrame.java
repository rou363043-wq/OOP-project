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


  
