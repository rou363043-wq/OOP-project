package model;
public class Student extends User implements Storable {
private String email;
private String phone;
private TimeAllowance allowance;

public Student( String id, String password, String name, String email, String phone, int minutes) {
  super(id, name, password);
  this.email = email;
  this.phone = phone;
  allowance = new TimeAllowance(minutes);
}
  public String getUniversityId(){
    return getUsename();
  }
  public String getEmail() { return email; }
  public String getPhone() { return phone; }
  public TimeAllowance getAllowance() { return allowance; }
  
  public String getRole() {
    return "Student";}

  public String toLine() {
    String Line = getUsername() + ";" + getPassword() + ";" + getFullName() + ";" + email + ";" + phone;
    line = line + ";" + allowance.getAllowcated() + ";" + allowance.getUsed() + ";" + allowance.getReversed();
    return line;
  }

  public Static Student fromLine(String line) {
    String[] p = line.split(";");
    if (p.length() !=8)
      return null;
    int allocated = Integer.parseInt(p[5]);
    int used = Integer.parseInt(p[6]);
    int reserved = Integer.parseInt(p[7]);
    if ( allocated < 0 || used < 0 || reversed < 0 || allocated - used - reversed < 0 )
      return null;
    Studend s = new Student (p[0], p[1], p[2], p[3], p[4], allowcated);
    s.allowance = new TimeAllowance( allocated, used, reversed);
    return s;
  }
}
