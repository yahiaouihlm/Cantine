package fr.sqli.cantine.security;


import fr.sqli.cantine.constants.ConstCantine;
import fr.sqli.cantine.entity.UserEntity;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;


public class myUserDetails implements  org.springframework.security.core.userdetails.UserDetails{

    private final UserEntity user ;
    private final Environment environment;

    public myUserDetails(UserEntity user, Environment environment){
        this.user =  user ;
        this.environment = environment;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getLabel())).toList();
    }

    @Override
    public String getPassword() {
       return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
          return true;
     }

    @Override
    public boolean isAccountNonLocked() {
        if (this.user.getRoles().stream().anyMatch(role -> role.getLabel().equals(ConstCantine.ADMIN_ROLE_LABEL))) {
            return this.user.getValidation() != 0;
        }
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.getStatus() == 1;
    }

    public String getFirstname() {
        return this.user.getFirstname();
    }

    public String getLastname() {
        return this.user.getLastname();
    }

    public String  getImage() {
        if  (this.user.getRoles().stream().anyMatch(role -> role.getLabel().equals(ConstCantine.ADMIN_ROLE_LABEL))){
            return  this.environment.getProperty("sqli.cantine.images.url.admin")+ this.user.getImage().getName();
        }

        return  this.environment.getProperty("sqli.cantine.images.url.student")+ this.user.getImage().getName();

    }

    public String getId() {
       return this.user.getId();
    }
}
