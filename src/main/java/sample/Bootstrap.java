package sample;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements InitializingBean {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            if (userRepository.count() == 0) {
                User user = new User();
                user.setUserName("Jeevesh");
                user.setEmail("user");
                user.setPassword("user");
                userRepository.save(user);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
