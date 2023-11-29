package fr.sqli.cantine.dto.in.users;

public class Login {
    private  String email ;
    private  String password  ;



   public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }
  public  Login(){}



    //  Les getteers et les setters   ...

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}