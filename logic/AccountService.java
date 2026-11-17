public class AccountService{
private Administrator admin = new Administrator();
private ArrayList<Student> students = new ArrayList<Student>();
private ArrayList<AccessRequest> requests = new ArrayList<AccessRequest>();
private ArrayList<TimeGrant> grants - new ArrayList<TimeGrant>();
private String message = "";

public String getMessage(){ 
  return message;}
public ArrayList<Student> getStudents(){
  return students;}
public ArrayList<AccessRequest> getRequest(){
  return requests;}
public ArrayList<TimeGrant> getGrants(){
  return grants;}

public Student findStudent(String id){
  for (int i=0; i<students.size(); i++){
  student s = students.get(i);
  if(s.getuniversityId().equals(id))
    return s;
  }
return null;
}

public AccessRequest findLatestRequest(String id){
  AccessRequest found = null;
  for(int i=0; requests.size();i++){
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
  
  if (!allDigits(phone) || phone.length()<7 || phone,length()>15){
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
  if (!by.getRole().equals("Administrator"){
    message= "Only the administrator can approve requests.";
    return false;
  }
  if (minutes<=0){
    message= "Minutes must be a positive whole number.":
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


}

