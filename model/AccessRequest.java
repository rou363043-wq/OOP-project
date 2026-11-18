package model;
private String universityID;
private String fullName;
private String email;
private String phone;
private string password;
private String status;

public AccessRequest( String unversityID, String fullname, String email, String phone, String password) {
  this.unversityID=unversityID;
  this.email=email;
  this.phone;
  this.password;
  status="Pending";

  public String getUniversityId(){
    return universityId;}
  public String getFullName(){
    return fullName;}
  public String getEmail(){
    return email;}
  public String getPhone(){
    return phone;}
  public String getPassword(){
    return password;}
  public String getStatus(){
    return status;}

  public boolean isPending(){
    return status.equals("Pending");}
  
  public boolean aprove(){
    if (!isPending())
        return false;
    status= "Approved";
        return true;
  }
  public boolean reject() {
    if (status.equals("Pending"))
      status= "Rejected";
    return true;
    return false;}
@override
  public String toString(){
    return universityId + ";"+ fullName + ";" + email + ";" + phone + ";" + password + ";" status;
}


