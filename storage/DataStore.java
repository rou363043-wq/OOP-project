package storage

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import model.TimeAllowance;

public class DataStore{
  public static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
  
  private File folder;
  private File clockFile;
  private File studentsFile;

  public DataStore(String folderName){
    this.folder= new File(folderName);
    this.clockFile= new File(this.folder, "clock.txt");
    this.studentsFile= new File(this.folder, "students.csv");
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
