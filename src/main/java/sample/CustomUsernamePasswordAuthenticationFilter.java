package sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private String jsonUsername;
    private String jsonPassword;

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String password = null;

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            password = this.jsonPassword;
        } else {
            password = super.obtainPassword(request);
        }

        return password;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String username = null;

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            username = this.jsonUsername;
        } else {
            username = super.obtainUsername(request);
        }

        return username;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("........................................................");
        System.out.println("attemptAuthentication");
        if ("application/json".equals(request.getHeader("Content-Type"))) {
            try {
                /*
                 * HttpServletRequest can be read only once
                 */
                StringBuffer sb = new StringBuffer();
                String line = null;

                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                System.out.println("......................sb.toString..................................");
                System.out.println(sb.toString());
                //json transformation
                ObjectMapper mapper = new ObjectMapper();
                RegistrationDto loginRequest = mapper.readValue(sb.toString(), RegistrationDto.class);

                System.out.println("...............Data...................");
                System.out.println(loginRequest.getUsername());
                System.out.println(loginRequest.getPassword());

                this.jsonUsername = loginRequest.getUsername();
                this.jsonPassword = loginRequest.getPassword();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        System.out.println(".....................authRequest.....................................");
        System.out.println(authRequest.getCredentials());
        System.out.println(authRequest.isAuthenticated());
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
