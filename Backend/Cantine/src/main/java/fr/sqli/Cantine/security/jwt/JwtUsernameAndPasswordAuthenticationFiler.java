package fr.sqli.Cantine.security.jwt;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sqli.Cantine.dto.in.person.Login;
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

import java.io.IOException;
import java.util.stream.Collectors;

public class JwtUsernameAndPasswordAuthenticationFiler extends  org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter{
    private  static final Logger LOG = LogManager.getLogger();
    private AuthenticationManager authenticationManager ;
    public  JwtUsernameAndPasswordAuthenticationFiler (AuthenticationManager authenticationManager){
        this.authenticationManager= authenticationManager ;
    }

    @Override
    public Authentication  attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        var  username =  request.getParameter("username");
        var passsword  = request.getParameter("password");
        if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(passsword) ) {
            JwtUsernameAndPasswordAuthenticationFiler.LOG
                    .debug("--> JwtAuthenticationFilter.attemptAuthentication(email, password) as Json in Body");
            String body  = null ;
            try {
                body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                var mapper = new ObjectMapper();
                var login  = mapper.readValue(body ,  Login.class);
                username =  login.getEmail();
                passsword =  login.getPassword() ;
            } catch (IOException | IOException lExp) {
                JwtUsernameAndPasswordAuthenticationFiler.LOG.error(
                        "--> JwtAuthenticationFilter.attemptAuthentication - Error, your JSon is not right!, found {}, should be something like {\"email\":\"toto@gmail.com\",\"password\":\"bonjour\"}. DO NOT use simple quote!",
                        body, lExp);
            }

        } else {
            JwtUsernameAndPasswordAuthenticationFiler.LOG
                    .debug("--> JwtAuthenticationFilter.attemptAuthentication(email, password) as parameter");

        }
        JwtUsernameAndPasswordAuthenticationFiler.LOG.debug("--> JwtAuthenticationFilter.attemptAuthentication({}, [PROTECTED])",
                username);

        Authentication  authentication =  new UsernamePasswordAuthenticationToken(username , passsword );
        var  result  =  this.authenticationManager.authenticate(authentication) ;
        System.out.println( "username   =  " + username   +  "password   =  " + passsword  +  "  authentication  " +  result.getPrincipal()   + "  <  " + result.getCredentials()  );
        return   result ;
    }

}
