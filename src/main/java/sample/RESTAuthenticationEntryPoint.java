package sample;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
public class RESTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        System.out.println(".....................RESTAuthenticationEntryPoint...........................");
        try {
            System.out.println(request.getAuthType());
            System.out.println(request.getParts());
            System.out.println(Arrays.toString(request.getCookies()));
            System.out.println(request.getQueryString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,authException.getMessage());
    }
}