package logic;

import java.util.ArrayList;

import model.AccessRequest;
import model.Administrator;
import model.SimTime;
import model.Student;
import model.TimeGrant;
import model.User;

public class AccountService{
  private Administrator admin = new Administrator();
  private ArrayList<Student> students = new ArrayList<Student>();
  private ArrayList<AccessRequest> requests = new ArrayList<AccessRequest>();
  private ArrayList<TimeGrant> grants = new ArrayList<TimeGrant>();
  private String message = "";

  public String getMessage(){ 
    return message;}
  public ArrayList<Student> getStudents(){
    return students;}
  public ArrayList<AccessRequest> getRequests(){
    return requests;}
  public ArrayList<TimeGrant> getGrants(){
    return grants;}

  public Student findStudent(String id){
    for (int i=0; i<students.size(); i++){
        Student s = students.get(i);
        if(s.getUniversityId().equals(id))
          return s;
    }
    return null;
  }

  public AccessRequest findLatestRequest(String id){
    AccessRequest found = null;
    for(int i=0; i<requests.size();i++){
      if(requests.get(i).getUniversityId().equals(id))
        found= requests.get(i);
    }
    return found;}

  private boolean allDigits(String s){
    for (int i=0; i<s.length(); i++){
    char c = s.charAt(i);
    if ( c<'0' || c > '9')
      return false;
    }
  return true;
  }

  public boolean register(String id, String name, String email, String phone, String password){
    id= id.trim();
    name = name.trim();
    email= email.trim();
    phone= phone.trim();
  
    if (id.length() ==0 || name.length()==0 || email.length()==0 || phone.length()==0 || password.length()==0){
      message = "Fill in all the fields.";
      return false;
    }
  
    if (name.contains(";") || email.contains(";") || password.contains(";")){
      message = "The character ; is not allowed.";
      return false;
    }
    
    if (!allDigits(id)){
      message = "University ID must contain digits only.";
      return false;
    }
  
    int at = email.indexOf("@");
    boolean emailOk = true;
    if(at <=0 || email.contains(" "))
      emailOk= false;
    else{
      String after = email.substring(at +1);
      int dot= after.indexOf(".");
      if(dot<=0 || dot == after.length()-1 || after.contains("@"))
      emailOk=false;
    }
    
    if(emailOk== false){
      message = "Enter a valid email address.";
      return false;
    }
  
    if (!allDigits(phone) || phone.length()<7 || phone.length()>15){
      message = "Enter a valid phone number (7 to 15 digits)";
      return false;
    }
  
    if(findStudent(id) !=null){
      message = "This university ID is already registered";
      return false;
    }
    AccessRequest old = findLatestRequest(id);
    if (old !=null && old.isPending()){
      message= "A request for this university ID is already pending.";
      return false;
    }
    AccessRequest r = new AccessRequest(id, name, email, phone, password);
    requests.add(r);
    message ="Request sent. Status: Pending.";
    return true;
  }
  
  
  public boolean approve(AccessRequest r, int minutes, User by){
    if (!by.getRole().equals("Administrator")){
      message= "Only the administrator can approve requests.";
      return false;
    }
    if (minutes<=0){
      message= "Minutes must be a positive whole number.";
        return false;      
    }
    if (findStudent(r.getUniversityId())!=null){
      message = "An account for this university ID already exists";
      return false;
    }

    if (r.approve() == false){
      message = "This request was already processed.";
      return false;
    }
    Student s = new Student(r.getUniversityId(), r.getPassword(), r.getFullName(), r.getEmail(), r.getPhone(), minutes);
    students.add(s);
    message= "Approved";
    return true;
  }


  public boolean reject(AccessRequest r, User by){
    if (!by.getRole().equals("Administrator")){
      message = "Only the administrator can reject requests.";
      return false;
    }
    if (!r.reject()){
      message= "This request was already processed.";
      return false;
    }
    message = "Rejected.";
    return true;
  }


  public User login(String username, String password){
    username= username.trim();
    if (username.length() ==0 || password.length() == 0){
      message= "Enter the username and the password.";
      return null;
    }

    if (username.equals(admin.getUsername())){
      if(admin.checkPassword(password))
        return admin;
      message= "Wrong password.";
      return null;
    }

    Student s = findStudent(username);
    if(s!= null){
      if(s.checkPassword(password))
        return s;
      message="Wrong password.";
      return null;
    }

    AccessRequest r = findLatestRequest(username);
    if(r==null)
      message= "No account with this username.";
    else if(r.isPending())
      message= "Your registration is still pending approval.";
    else
      message= "Your registration request was rejected.";
    return null; 
  }

  public boolean grantTime(String id, int minutes, String reason, User by, SimTime now){
    if(!by.getRole().equals("Administrator")){
      message = "Only the administrator can grant time.";
      return false;
    }
    Student s = findStudent(id);
    if(s ==null){
      message= "No student with this university ID.";
      return false;
    }
    if(minutes<=0){
      message= "Minutes must be a positive whole number.";
      return false;
    }
    if(reason.trim().length() ==0){
      message= "A reason is required.";
      return false;
    }

    s.getAllowance().addAllocated(minutes);
    reason= reason.trim().replace(";", ",");
    grants.add(new TimeGrant(id, minutes, reason, by.getUsername(), now));
    message= "Granted.";
    return true;
  }
}
