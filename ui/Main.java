package ui;

import javax.swing.JOptionPane;
import logic.AccountService;
import logic.Scheduler;
import logic.SimulationClock;
import storage.DataStore;

public class Main {

    public static void main(String[] args) {
        AccountService accounts = new AccountService();
        SimulationClock clock = new SimulationClock();
        Scheduler scheduler = new Scheduler(clock, accounts);
        DataStore store = new DataStore(accounts, scheduler, clock);
