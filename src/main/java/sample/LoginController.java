package sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    @Qualifier("customUserDetailsService")
    private CustomUserDetailsService customUserDetailsService;

    @RequestMapping("/login")
    public ResponseEntity<RestApiController.ResponseDto> login(@RequestBody RegistrationDto principal) {

        System.out.println("........................Hello......................................");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            System.out.println("hello");
        } else {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(principal.getUsername());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return new ResponseEntity<RestApiController.ResponseDto>(
                new RestApiController.ResponseDto("Happy posting, " + principal.getUsername() + "!"), HttpStatus.OK);
    }
}
