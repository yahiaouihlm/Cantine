package fr.sqli.Cantine.security.exceptionHandler;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler   implements  org.springframework.security.web.access.AccessDeniedHandler{




    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        System.out.println(accessDeniedException.getMessage());

        response.sendError(500,"ACCESS DENIED");
    }
}
