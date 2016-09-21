package sample;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class RESTAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public RESTAuthenticationSuccessHandler() {
        super("/success");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println(".............Inside RESTAuthenticationSuccessHandler................");
        response.setStatus(HttpServletResponse.SC_OK);
        if ("application/json".equals(request.getHeader("Content-Type"))) {
            System.out.println("........>Inside if.........");
//              USED if you want to AVOID redirect to LoginSuccessful.htm in JSON authentication
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            response.getWriter().print("{\"responseCode\":\"SUCCESS\"}");
            response.getWriter().flush();
        } else {
            System.out.println("......................Inside else..........");
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    @Override
    protected void handle(HttpServletRequest request,
                          HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(authentication);

        System.out.println(".................Inside target url..........");
        System.out.println(targetUrl);
        if (response.isCommitted()) {
            System.out.println("Can't redirect");
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        String url = "";

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> roles = new ArrayList<String>();

        for (GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }

        if (isDba(roles)) {
            url = "/db";
        } else if (isAdmin(roles)) {
            url = "/admin";
        } else if (isUser(roles)) {
            url = "/home";
        } else {
            url = "/accessDenied";
        }

        return url;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    private boolean isUser(List<String> roles) {
        if (roles.contains("ROLE_USER")) {
            return true;
        }
        return false;
    }

    private boolean isAdmin(List<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            return true;
        }
        return false;
    }

    private boolean isDba(List<String> roles) {
        if (roles.contains("ROLE_DBA")) {
            return true;
        }
        return false;
    }


}