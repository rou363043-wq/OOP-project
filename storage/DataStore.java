package storage;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import logic.AccountService;
import logic.Scheduler;
import logic.SimulationClock;
import model.AccessRequest;
import model.Job;
import model.SimTime;
import model.Storable;
import model.Student;
import model.TimeGrant;

/* saves everything to text files in the data folder and reads it back.
   if save() or load() returns false, getMessage() says which file and why */
public class DataStore {

    private String folder = "data";
    private AccountService accounts;
    private Scheduler scheduler;
    private SimulationClock clock;
    private String message = "";

    public DataStore(AccountService accounts, Scheduler scheduler, SimulationClock clock) {
        this.accounts = accounts;
        this.scheduler=scheduler;
        this.clock = clock;
    }

    public String getMessage() { return message; }

    public boolean save() {
        String name = "";
        try {
            File dir = new File(folder);
            if (!dir.exists())
                dir.mkdir();

            // every list goes through the same method because all items are Storable
            ArrayList<Storable> list = new ArrayList<Storable>();
            for (int i = 0; i < accounts.getStudents().size(); i++)
                list.add(accounts.getStudents().get(i));
            name = folder + "/students.txt";
            writeFile(name, list);

            list = new ArrayList<Storable>();
            for (int i = 0; i < accounts.getRequests().size(); i++)
                list.add(accounts.getRequests().get(i));
            name = folder + "/requests.txt";
            writeFile(name, list);

            list = new ArrayList<Storable>();
            for (int i=0; i<accounts.getGrants().size(); i++)
                list.add(accounts.getGrants().get(i));
            name = folder + "/grants.txt";
            writeFile(name, list);

            list = new ArrayList<Storable>();
            for (int i = 0; i < scheduler.getJobs().size(); i++)
                list.add(scheduler.getJobs().get(i));
            name = folder + "/jobs.txt";
            writeFile(name, list);

            // clock: start time, current time, next job number
            name = folder + "/clock.txt";
            PrintWriter out = new PrintWriter(name);
            out.println(clock.getStartTime());
            out.println(clock.getNow());
            out.print(scheduler.getNextJobNumber());
            out.close();
        }
        catch (Exception e) {
            message = "Could not save " + name + ". Check that the data folder can be written.";
            return false;
        }
        return true;
    }

    private void writeFile(String name, ArrayList<Storable> list) throws Exception {
        PrintWriter out = new PrintWriter(name);
        for (int i = 0; i < list.size(); i++) {
            // the last line gets no line break, so the file has no empty line at the end
            if (i < list.size() - 1)
                out.println(list.get(i).toLine());
            else
                out.print(list.get(i).toLine());
        }
        out.close();
    }

    /* a missing file is fine, it means first run.
       a broken file stops the loading */
    public boolean load() {
        String name = "";
        int lineNo = 0;
        try {
            name = folder + "/clock.txt";
            lineNo = 0;
            int nextNumber = 101;
            if (new File(name).exists()) {
                Scanner in = new Scanner(new FileReader(name));
                lineNo = 1;
                SimTime start = SimTime.fromText(in.nextLine().trim());
                lineNo = 2;
                SimTime now = SimTime.fromText(in.nextLine().trim());
                lineNo=3;
                nextNumber = Integer.parseInt(in.nextLine().trim());
                in.close();
                if (start != null && now != null) {
                    if (start.minutesUntil(now) < 0) {
                        message = "Could not read " + name + " (the current time is before the start time).";
                        return false;
                    }
                    clock.restore(start, now);
                }
            }

            name = folder + "/students.txt";
            lineNo = 0;
            if (new File(name).exists()) {
                Scanner in = new Scanner(new FileReader(name));
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    lineNo++;
                    if (line.trim().length() == 0)
                        continue;
                    Student s = Student.fromLine(line);
                    if (s == null || accounts.findStudent(s.getUniversityId()) != null) {
                        message = "Could not read " + name + " (line " + lineNo + " is not valid).";
                        in.close();
                        return false;
                    }
                    accounts.getStudents().add(s);
                }
                in.close();
            }

            name = folder + "/requests.txt";
            lineNo = 0;
            if (new File(name).exists()) {
                Scanner in = new Scanner(new FileReader(name));
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    lineNo++;
                    if (line.trim().length() == 0)
                        continue;
                    AccessRequest r = AccessRequest.fromLine(line);
                    if (r == null) {
                        message = "Could not read " + name + " (line " + lineNo + " is not valid).";
                        in.close();
                        return false;
                    }
                    accounts.getRequests().add(r);
                }
                in.close();
            }

            name = folder + "/grants.txt";
            lineNo = 0;
            if (new File(name).exists()) {
                Scanner in = new Scanner(new FileReader(name));
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    lineNo++;
                    if (line.trim().length() == 0)
                        continue;
                    TimeGrant g = TimeGrant.fromLine(line);
                    if (g == null) {
                        message = "Could not read " + name + " (line " + lineNo + " is not valid).";
                        in.close();
                        return false;
                    }
                    accounts.getGrants().add(g);
                }
                in.close();
            }

            name = folder + "/jobs.txt";
            lineNo = 0;
            ArrayList<Job> jobs = new ArrayList<Job>();
            if (new File(name).exists()) {
                Scanner in = new Scanner(new FileReader(name));
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    lineNo++;
                    if (line.trim().length() == 0)
                        continue;
                    Job j = Job.fromLine(line);
                    if (j == null || accounts.findStudent(j.getStudentId()) == null) {
                        message = "Could not read " + name + " (line " + lineNo + " is not valid).";
                        in.close();
                        return false;
                    }
                    jobs.add(j);
                }
                in.close();
            }
            if (jobs.size() > 0 && !clock.isSet()) {
                message = "Could not read " + folder + "/clock.txt (there are saved jobs but no saved clock).";
                return false;
            }
            if (scheduler.restore(jobs, nextNumber) == false) {
                message = "Could not read " + name + " (two running jobs use the same GPU).";
                return false;
            }
        }
        catch (Exception e) {
            // wrong number or wrong date in a line ends here
            message = "Could not read " + name + " (line " + lineNo + " is not valid).";
            return false;
        }
        return true;
    }
}
