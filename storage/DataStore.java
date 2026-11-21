package storage

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class DataStore{
  public static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
  
  private File folder;
  private File clockFile;

  public DataStore(String folderName){
    this.folder= new File(folderName);
    this.clockFile= new File(this.folder, "clock.txt");
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

  public void saveAll(LocalDateTime currentTime) throws Exception {
    saveClock(currentTime);
  }
  public void loadAll() throws Exception{
    if(!folder.exists()){
      return;
    }
  }
}
