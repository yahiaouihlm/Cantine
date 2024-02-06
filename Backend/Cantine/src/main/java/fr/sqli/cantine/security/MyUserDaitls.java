package fr.sqli.cantine.security;

import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.StudentEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public class MyUserDaitls  implements  org.springframework.security.core.userdetails.UserDetails{


    AdminEntity  admin ;
    public  MyUserDaitls (AdminEntity admin){
        this.admin = admin ;
    }

    StudentEntity student ;
    public  MyUserDaitls (StudentEntity student){
        this.student = student ;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if  (this.admin !=  null ) return  List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return  List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }

    @Override
    public String getPassword() {
        if  (this.admin !=  null ) return  this.admin.getPassword();
        return  this.student.getPassword();
    }

    @Override
    public String getUsername() {
        if  (this.admin !=  null ) return  this.admin.getEmail();
        return  this.student.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
          return true;
     }

    @Override
    public boolean isAccountNonLocked() {
        if (this.admin != null) {
            return admin.getValidation() != 0;
        }
        return   true ;

    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (this.admin != null) {
            return admin.getStatus() == 1;
        }
        return student.getStatus() == 1;
    }

    public String getFirstname() {
        if (this.admin != null) {
            return admin.getFirstname();
        }
        return student.getFirstname();
    }

    public String getLastname() {
        if (this.admin != null) {
            return admin.getLastname();
        }
        return student.getLastname();
    }

    public String  getImage() {
        if (this.admin != null) {
            return  "http://localhost:8080/cantine/download/images/persons/admin/"+ admin.getImage().getImagename();
        }
        return  "http://localhost:8080/cantine/download/images/persons/students/"+ student.getImage().getImagename();

    }

    public String getUuid() {
        if (this.admin != null) {
            return admin.getUuid();
        }
        return student.getUuid();
    }
}
