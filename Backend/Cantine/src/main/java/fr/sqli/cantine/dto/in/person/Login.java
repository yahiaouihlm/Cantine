package fr.sqli.Cantine.dto.in.person;

public class Login {
    private  String email ;
    private  String password  ;








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