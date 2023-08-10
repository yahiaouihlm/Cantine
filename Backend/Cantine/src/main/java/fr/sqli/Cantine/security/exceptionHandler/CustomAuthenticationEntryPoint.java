package fr.sqli.Cantine.security.exceptionHandler;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("customAuthenticationEntryPoint")
public class CustomAuthenticationEntryPoint   implements  org.springframework.security.web.AuthenticationEntryPoint{
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
          response.sendError(401,"unauthorized");
    }
}
