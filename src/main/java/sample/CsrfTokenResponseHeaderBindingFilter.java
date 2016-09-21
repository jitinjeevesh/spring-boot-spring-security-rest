package sample;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

public class CsrfTokenResponseHeaderBindingFilter extends OncePerRequestFilter {
    protected static final String REQUEST_ATTRIBUTE_NAME = "_csrf";
    protected static final String RESPONSE_HEADER_NAME = "X-CSRF-HEADER";
    protected static final String RESPONSE_PARAM_NAME = "X-CSRF-PARAM";
    protected static final String RESPONSE_TOKEN_NAME = "X-CSRF-TOKEN";

    private static final String CSRF_TOKEN = "JSESSIONID";
    private static final String X_CSRF_TOKEN = "X-CSRF-TOKEN";
    private final RequestMatcher requireCsrfProtectionMatcher = new DefaultRequiresCsrfMatcher();
    private final AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, javax.servlet.FilterChain filterChain) throws ServletException, IOException {
//        System.out.println(".........>Inside CsrfTokenResponseHeaderBindingFilter....................");
//
//        CsrfToken token = (CsrfToken) request.getAttribute(REQUEST_ATTRIBUTE_NAME);
//
//        if (token != null) {
//            System.out.println(token.getToken());
//            response.setHeader(RESPONSE_HEADER_NAME, token.getHeaderName());
//            response.setHeader(RESPONSE_PARAM_NAME, token.getParameterName());
//            response.setHeader(RESPONSE_TOKEN_NAME, token.getToken());
//        }
//
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (requireCsrfProtectionMatcher.matches(request)) {
            final String csrfTokenValue = request.getHeader(X_CSRF_TOKEN);
            final Cookie[] cookies = request.getCookies();

            String csrfCookieValue = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(CSRF_TOKEN)) {
                        csrfCookieValue = cookie.getValue();
                    }
                }
            }

            System.out.println("..............................................");
            System.out.println(csrfTokenValue);
            System.out.println(csrfCookieValue);
            if (csrfTokenValue == null || !csrfTokenValue.equals(csrfCookieValue)) {
                accessDeniedHandler.handle(request, response, new AccessDeniedException(
                        "Missing or non-matching CSRF-token"));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    public static final class DefaultRequiresCsrfMatcher implements RequestMatcher {
        private final Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

        @Override
        public boolean matches(HttpServletRequest request) {
            return !allowedMethods.matcher(request.getMethod()).matches();
        }
    }
}