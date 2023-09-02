package fr.sqli.cantine.dto.out.person;

import java.time.LocalDate;

public abstract  class AbstractPersonDtout {

    private Integer  id  ;
    private  String firstname;
    private  String lastname;
    private  String email;

    private  String birthdateAsString;
    private LocalDate birthdate;
    private  String town;
    private  String address;
    private  String phone;
    private String image;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getBirthdateAsString() {
        return birthdateAsString;
    }

    public void setBirthdateAsString(String birthdateAsString) {
        this.birthdateAsString = birthdateAsString;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
