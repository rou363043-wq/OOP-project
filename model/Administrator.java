package model;
public class Administrator extends User {
public Administrator() {
super( "admin", "gpu2026", "Administrator");
}
public String getRole() {
return "Administrator";
}
}
