package fr.sqli.Cantine.security.jwt;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;

public class JwtUsernameAndPasswordAuthenticationFiler extends  org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter{
    private  static final Logger LOG = LogManager.getLogger();
    private AuthenticationManager authenticationManager ;
    public  JwtUsernameAndPasswordAuthenticationFiler (AuthenticationManager authenticationManager){
        this.authenticationManager= authenticationManager ;
    }

    @Override
    public Authentication  attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    }

}
