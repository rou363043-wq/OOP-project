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
    line = line + ";" + allowance.getA
