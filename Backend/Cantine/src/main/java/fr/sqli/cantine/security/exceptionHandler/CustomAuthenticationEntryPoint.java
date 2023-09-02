package fr.sqli.cantine.security.exceptionHandler;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component("customAuthenticationEntryPoint")
public class CustomAuthenticationEntryPoint   implements  org.springframework.security.web.AuthenticationEntryPoint{

    private  final HandlerExceptionResolver handlerExceptionResolver;


    public CustomAuthenticationEntryPoint( @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
           response.addHeader("WWW-Authenticate","Basic realm=\""+request.getServletContext().getServletContextName()+"\"");
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           response.sendError(HttpServletResponse.SC_UNAUTHORIZED,authException.getMessage());
          this.handlerExceptionResolver.resolveException(request,response,null,authException);
    }
}
