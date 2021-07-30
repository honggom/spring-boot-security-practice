package hong.gom.springbootsecuritypractice4;

import hong.gom.springbootsecuritypractice4.config.UserLoginForm;
import hong.gom.springbootsecuritypractice4.dto.SpUser;
import hong.gom.springbootsecuritypractice4.repository.SpUserRepository;
import hong.gom.springbootsecuritypractice4.service.SpUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class JWTRequestTest{

    @Autowired
    private SpUserRepository userRepository;

    @Autowired
    private SpUserService userService;

    @BeforeEach
    void before(){
        userRepository.deleteAll();
        SpUser user = userService.save(SpUser.builder().email("hong").password("1111").enabled(true).build());

        userService.addAuthority(user.getUserId(), "ROLE_USER");

    }


    @DisplayName("1.hello 메시지를 받아온다")
    @Test
    void test() {

        RestTemplate client = new RestTemplate();
        HttpEntity<UserLoginForm> body = new HttpEntity<>(
                UserLoginForm.builder().username("hong").password("1111").build());

        ResponseEntity<SpUser> response = client.exchange("/login", HttpMethod.POST, body, SpUser.class);
        System.out.println(response.getBody());
    }
}
