package fr.sqli.Cantine.security.exceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sqli.Cantine.dto.out.ExceptionDtout;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class StoneAuthenticationFailureHandler implements AuthenticationFailureHandler, AuthenticationEntryPoint,AccessDeniedHandler {

    private static final Logger LOG = LogManager.getLogger();

    // Could be injected by Spring
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Will handle the response.
     *
     * @param request    the request
     * @param response   the response
     * @param pException the exception
     * @param httpStatus the http status
     * @throws IOException if an error occurred
     */
    private void handle(HttpServletRequest request, HttpServletResponse response, Exception pException, int httpStatus)
            throws IOException, JsonProcessingException {
        var out = new ExceptionDtout(pException.getMessage());
       // out.setStatus(Integer.valueOf(httpStatus));
        var expToJson = this.objectMapper.writeValueAsString(out);
        var pw = response.getWriter();
        pw.write(expToJson);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException authException) throws IOException, ServletException {
        StoneAuthenticationFailureHandler.LOG.error(
                "--> 401 <--- From AuthenticationFailureHandler.onAuthenticationFailure for '{}'",
                request.getRequestURL(), authException);
        System.out.println("  Here 401  ");
        this.handle(request, response, authException, HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        StoneAuthenticationFailureHandler.LOG.error("--> 401 <--- From AuthenticationEntryPoint.commence for '{}'",
                request.getRequestURL(), authException);




        this.handle(request, response, authException, HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
            throws IOException, ServletException {
        StoneAuthenticationFailureHandler.LOG.error("--> 403 <--- From AccessDeniedHandler.handle for '{}'",
                request.getRequestURL(), exception);

        this.handle(request, response, exception, HttpServletResponse.SC_FORBIDDEN);
    }




}
