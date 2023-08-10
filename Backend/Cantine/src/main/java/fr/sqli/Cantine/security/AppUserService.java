package fr.sqli.Cantine.security;

import fr.sqli.Cantine.dao.IAdminDao;
import fr.sqli.Cantine.dao.IStudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AppUserService  implements UserDetailsService {


    private IAdminDao  iAdminDao ;

    private IStudentDao iStudentDao ;


    @Autowired
    public  AppUserService  ( IAdminDao iAdminDao , IStudentDao iStudentDao ){
        this.iAdminDao = iAdminDao ;
        this.iStudentDao = iStudentDao ;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (iAdminDao.findByEmail(username).isPresent()){
            return  new MyUserDaitls(iAdminDao.findByEmail(username).get());
        }
        if (iStudentDao.findByEmail(username).isPresent()){
            return  new MyUserDaitls(iStudentDao.findByEmail(username).get());
        }
        throw  new UsernameNotFoundException("user not found");
    }
}
