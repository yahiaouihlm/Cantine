package fr.sqli.cantine.security;

import fr.sqli.cantine.dao.IUserDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserService  implements UserDetailsService {

    private static final Logger LOG = LogManager.getLogger();
    private final  IUserDao iUserDao ;
    private final Environment environment;

    @Autowired
    public  AppUserService  (IUserDao iUserDao ,  Environment environment){
        this.iUserDao = iUserDao;
        this.environment = environment;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isEmpty())
            throw new UsernameNotFoundException("USER NOT  FOUND");
        var user = this.iUserDao.findUserByEmail(username).orElseThrow(() -> {
            AppUserService.LOG.error("USER NOT  FOUND {} WHILE TRYING TO CONNECT IN AppUserService.loadUserByUsername", username);
            return new UsernameNotFoundException("USER NOT  FOUND");
        });
        return new myUserDetails(user,this.environment);
    }
}
