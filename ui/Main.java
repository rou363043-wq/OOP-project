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

        if (store.load() == false) {
            JOptionPane.showMessageDialog(null, store.getMessage()
                    + "\nThe file was not loaded. Fix or remove it, then restart.",
                    "File error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        LoginFrame login = new LoginFrame(accounts, scheduler, clock, store);
        login.setVisible(true);
    }
}
