package storage

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import model.Job;
import model.TimeAllowance;


public class DataStore{
  public static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
  
  private File folder;
  private File clockFile;
  private File studentsFile;
  private File jobsFile;

  public DataStore(String folderName){
    this.folder= new File(folderName);
    this.clockFile= new File(this.folder, "clock.txt");
    this.studentsFile= new File(this.folder, "students.csv");
    this.jobsFile= new File(this.folder, "jobs.csv");
  }

  public LocalDateTime loadClock() throws Exception {
    if (!clockFile.exists()){
      return null;
    }
    try(Scanner s = new Scanner(clockFile)){
      if(!s.hasNextLine()){
        throw new Exception("Could not read " + clockFile.getPath()+" (line 1: empty clock).");
      }
      String line = s.nextLine().trim();
      if (line.isEmpty()) {
        throw new Exception("Could not read " + clockFile.getPath()+" (line 1: empty clock).");
      }
      return LocalDateTime.parse(line, FORMAT);
    }catch (DateTimeParseException e){
      throw new Exception("Could not read " + clockFile.getPath() + " (line 1: bad date format).");
    }catch (IOException e) {
      throw new Exception("Could not read " + clockFile.getPath() + ".");
    }
  }


  public void saveClock(LocalDateTime time) throws Exception {
    if(time == null){
     return;
    }
    enusreFolderExists();
    try (FileWriter writer = new FileWriter(clockFile)){
      writer.write(time.format(FORMAT) + System.lineSeparator());
    }catch (IOEception e){
      throw new Exception("Could not save " + clockFile.getPath()+".");
    }
  }

  public void saveAllowances(list<String> studentIds, List<TimeAllowance> allowances) throws Exception{
    if (studentIds == null || allowances == null || studentIds.size() != allowances.size()){
      throw new Exception("Could not save " + studentsFile.getPath() + " (mismatched student allowances).");
    }
    enusreFolderExists();
    try(FileWriter writer = new FileWriter(studentsFile){
      for (int i = 0; i<studentIds.size(); i++){
        TimeAllowance a = allowances.get(i);
        String line = studentIds.get(i) + ","+a.getAllocated()+","a.getUsed()+","+a.getReserved();
        writer.write(line + System.lineSeparator());}
      }
      catch (IOException e) {
        throw new Exception("Could not save " + studentsFile.getPath() +".");
      }//save and load requestes &jobs
public void loadAllowances(List<String> loadedIds, List<TimeAllowance> loadedAllowances) throws Exception {
        loadedIds.clear();
        loadedAllowances.clear();
        if (!studentsFile.exists()) {
            return;
        }
        try (Scanner s = new Scanner(studentsFile)) {
            int lineNum = 0;
            while (s.hasNextLine()) {
                lineNum++;
                String line = s.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    throw new Exception("Could not read " + studentsFile.getPath()
                            + " (line " + lineNum + ": expected 4 values).");
                }

                String studentId = parts[0].trim();
                int allocated = parseIntField(parts[1].trim(), lineNum, studentsFile, "allocated");
                int used = parseIntField(parts[2].trim(), lineNum, studentsFile, "used");
                int reserved = parseIntField(parts[3].trim(), lineNum, studentsFile, "reserved");

                if (allocated < 0 || used < 0 || reserved < 0) {
                    throw new Exception("Could not read " + studentsFile.getPath()
                            + " (line " + lineNum + ": negative allowance).");
                }

                loadedIds.add(studentId);
                loadedAllowances.add(new TimeAllowance(allocated, used, reserved));
            }
        } catch (IOException e) {
            throw new Exception("Could not read " + studentsFile.getPath() + ".");
        }
    }

    public void saveJobs(List<Job> jobs) throws Exception {
        if (jobs == null) {
            throw new Exception("Could not save " + jobsFile.getPath() + " (jobs list is null).");
        }
        ensureFolderExists();
        try (FileWriter writer = new FileWriter(jobsFile)) {
            for (Job job : jobs) {
                String line = String.join(",",
                        cleanText(job.getJobId()),
                        cleanText(job.getStudentId()),
                        cleanText(job.getTitle()),
                        cleanText(job.getApplicationName()),
                        cleanText(job.getDescription()),
                        String.valueOf(job.getEstimatedDuration()),
                        formatDateTime(job.getSubmissionTime()),
                        cleanText(job.getStatus()),
                        formatOptionalText(job.getAssignedGpuId()),
                        formatDateTime(job.getStartTime()),
                        formatDateTime(job.getFinishTime()),
                        String.valueOf(job.getMinutesRun()),
                        formatDateTime(job.getCancellationTime()),
                        formatOptionalText(job.getCancelledBy()),
                        formatOptionalText(job.getCancellationReason())
                );
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new Exception("Could not save " + jobsFile.getPath() + ".");
        }
    }

    public void loadJobs(List<Job> loadedJobs) throws Exception {
        loadedJobs.clear();
        if (!jobsFile.exists()) {
            return;
        }
        try (Scanner scanner = new Scanner(jobsFile)) {
            int lineNum = 0;
            while (scanner.hasNextLine()) {
                lineNum++;
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length != 15) {
                    throw new Exception("Could not read " + jobsFile.getPath()
                            + " (line " + lineNum + ": expected 15 columns).");
                }

                String jobId = parts[0].trim();
                String studentId = parts[1].trim();
                String title = parts[2].trim();
                String appName = parts[3].trim();
                String description = parts[4].trim();
                int duration = parseIntField(parts[5].trim(), lineNum, jobsFile, "duration");
                LocalDateTime submissionTime = parseDateTimeField(parts[6].trim(), lineNum, jobsFile, "submissionTime");
                String status = parts[7].trim();
                String gpuId = parseOptionalText(parts[8].trim());
                LocalDateTime startTime = parseOptionalDateTime(parts[9].trim(), lineNum, jobsFile, "startTime");
                LocalDateTime finishTime = parseOptionalDateTime(parts[10].trim(), lineNum, jobsFile, "finishTime");
                int minutesRun = parseIntField(parts[11].trim(), lineNum, jobsFile, "minutesRun");
                LocalDateTime cancelTime = parseOptionalDateTime(parts[12].trim(), lineNum, jobsFile, "cancellationTime");
                String cancelledBy = parseOptionalText(parts[13].trim());
                String cancelReason = parseOptionalText(parts[14].trim());

                if (submissionTime == null) {
                    throw new Exception("Could not read " + jobsFile.getPath()
                            + " (line " + lineNum + ": missing submissionTime).");
                }
                if (!isValidStatus(status)) {
                    throw new Exception("Could not read " + jobsFile.getPath()
                            + " (line " + lineNum + ": invalid status " + status + ").");
                }

                Job job = new Job(
                        jobId, studentId, title, appName, description,
                        duration, submissionTime, status, gpuId,
                        startTime, finishTime, minutesRun,
                        cancelTime, cancelledBy, cancelReason
                );
                loadedJobs.add(job);
            }
        } catch (IllegalArgumentException e) {
            throw new Exception("Could not read " + jobsFile.getPath()
                    + " (invalid job data: " + e.getMessage() + ").");
        } catch (IOException e) {
            throw new Exception("Could not read " + jobsFile.getPath() + ".");
        }
    }

    public void saveAll(LocalDateTime currentTime, List<String> studentIds,
                        List<TimeAllowance> allowances, List<Job> jobs) throws Exception {
        saveClock(currentTime);
        saveAllowances(studentIds, allowances);
        saveJobs(jobs);
        // save reqs
    }

    public void loadAll(List<String> studentIds, List<TimeAllowance> allowances,
                        List<Job> jobs) throws Exception {
        if (!folder.exists()) {
            return;
        }
        loadAllowances(studentIds, allowances);
        loadJobs(jobs);
        // load reqs
    }

    private void ensureFolderExists() {
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private boolean isValidStatus(String status) {
        return "QUEUED".equals(status) || "RUNNING".equals(status) || "COMPLETED".equals(status) || "CANCELLED".equals(status);
    }

    private int parseIntField(String raw, int lineNum, File file, String field) throws Exception {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new Exception("Could not read " + file.getPath()
                    + " (line " + lineNum + ": invalid number in " + field + ").");
        }
    }

    private LocalDateTime parseDateTimeField(String raw, int lineNum, File file, String field) throws Exception {
        if (raw.isEmpty() || EMPTY_TOKEN.equals(raw)) {
            return null;
        }
        try {
            return LocalDateTime.parse(raw, FORMAT);
        } catch (DateTimeParseException e) {
            throw new Exception("Could not read " + file.getPath()
                    + " (line " + lineNum + ": bad date format in " + field + ").");
        }
    }

    private LocalDateTime parseOptionalDateTime(String raw, int lineNum, File file, String field) throws Exception {
        return parseDateTimeField(raw, lineNum, file, field);
    }

    private String formatDateTime(LocalDateTime dt) {
        return dt == null ? EMPTY_TOKEN : dt.format(FORMAT);
    }

    private String formatOptionalText(String text) {
        String cleaned = cleanText(text);
        return cleaned.isEmpty() ? EMPTY_TOKEN : cleaned;
    }

    private String parseOptionalText(String raw) {
        return (raw.isEmpty() || EMPTY_TOKEN.equals(raw)) ? null : raw;
    }

    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        return text.replace(",", " ").replace("\r", " ").replace("\n", " ").trim();
    }
    }
