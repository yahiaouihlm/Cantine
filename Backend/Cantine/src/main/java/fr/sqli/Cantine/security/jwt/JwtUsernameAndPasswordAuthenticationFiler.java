package fr.sqli.Cantine.security.jwt;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sqli.Cantine.dto.in.person.Login;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtUsernameAndPasswordAuthenticationFiler extends  UsernamePasswordAuthenticationFilter {
    private  static final Logger LOG = LogManager.getLogger();
    private AuthenticationManager authenticationManager ;
    public  JwtUsernameAndPasswordAuthenticationFiler (AuthenticationManager authenticationManager){
        this.authenticationManager= authenticationManager ;
    }

    @Override
    public Authentication  attemptAuthentication(HttpServletRequest request, HttpServletResponse response)  throws AuthenticationException {
          Map<String, String> idToken = new HashMap<>();
           try  {
               var  username =  request.getParameter("email");
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


                   } catch (IOException lExp) {
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

               return  result ;


           }catch (DisabledException e ) {

               System.out.println(e.getMessage());
               response.setContentType("application/json");
               idToken.put("status" , HttpStatus.FORBIDDEN.name() );
               idToken.put("message" ,  "DISABLED ACCOUNT");
               response.setStatus(HttpServletResponse.SC_FORBIDDEN);
               try {
                   new ObjectMapper().writeValue( response.getOutputStream(), idToken);
               } catch (IOException ex) {
                   throw new RuntimeException(ex);
               }

               //  custom  exception  for  disabled  account

           }  catch (AuthenticationException e) {
               response.setContentType("application/json");
               idToken.put("status" , HttpStatus.UNAUTHORIZED.name());
               idToken.put("message" ,  "WRONG CREDENTIALS");
               response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
               try {
                   new ObjectMapper().writeValue( response.getOutputStream(), idToken);
               } catch (IOException ex) {
                   throw new RuntimeException(ex);
               }
           }
           catch (Exception e ) {

               System.out.println(e.getMessage());
               response.setContentType("application/json");
               idToken.put("status" , HttpStatus.NOT_FOUND.name() );
               idToken.put("message" ,  " An error has occurred");
               try {
                   new ObjectMapper().writeValue( response.getOutputStream(), idToken);
               } catch (IOException ex) {
                   throw new RuntimeException(ex);
               }
           }


             return  null ;
     }





    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String  key  =  "secret key"  ;
        Algorithm algorithm =  Algorithm.HMAC256(key.getBytes());

        String jwtAccessToken  = JWT.create()
                .withSubject(authResult.getName())   //  600=> 60 (in first of application)     ('+5 * 6000 *1000')
                .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 6000 *1000))
                .withIssuer(request.getRequestURI())
                .withClaim("roles" , authResult.getAuthorities().stream().map(GrantedAuthority:: getAuthority).collect(Collectors.toList()))
                .sign(algorithm);


        var  context  =  SecurityContextHolder.getContext() ;
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        /*String refreshToken  =  JWT .create()
                .withSubject(authResult.getName())
                .withExpiresAt(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);*/

        var username = context.getAuthentication().getName();
        var  role  =  context.getAuthentication().getAuthorities().toArray();


        Map<String, String> idToken = new HashMap<>();
        idToken.put("Authorization", "Bearer " + jwtAccessToken);
        response.setContentType("application/json");
        idToken.put("status" , HttpStatus.OK.name());
        idToken.put("message" ,  "you are authenticated");
        idToken.put("user" , username);
        idToken.put("role", role[0].toString());
        new ObjectMapper().writeValue(response.getOutputStream(), idToken);

    }


}
