package sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequestMapping("/rest")
@RestController()
public class RestApiController {

    @Autowired
    @Qualifier("customUserDetailsService")
    private CustomUserDetailsService customUserDetailsService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> hello(Principal principal) {

        return new ResponseEntity<ResponseDto>(
                new ResponseDto("hello, " + principal.getName() + "!"), HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> helloPost(Principal principal) {

        return new ResponseEntity<ResponseDto>(
                new ResponseDto("Saved successfully, " + principal.getName() + "!"), HttpStatus.OK);
    }

    public static class ResponseDto {
        private String message;

        public ResponseDto(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}