package hong.gom.springbootsecuritypractice4.config;

import hong.gom.springbootsecuritypractice4.dto.SpUser;
import hong.gom.springbootsecuritypractice4.service.SpUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbInit implements InitializingBean {

    private final SpUserService spUserService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(!spUserService.findUser("user1").isPresent()){
            SpUser user = spUserService.save(SpUser.builder()
                    .email("user1")
                    .password("1")
                    .enabled(true)
                    .build());
            spUserService.addAuthority(user.getUserId(), "ROLE_USER");
        }
        if(!spUserService.findUser("user2").isPresent()){
            SpUser user = spUserService.save(SpUser.builder()
                    .email("user2")
                    .password("1")
                    .enabled(true)
                    .build());
            spUserService.addAuthority(user.getUserId(), "ROLE_USER");
        }
        if(!spUserService.findUser("admin").isPresent()){
            SpUser user = spUserService.save(SpUser.builder()
                    .email("admin")
                    .password("1")
                    .enabled(true)
                    .build());
            spUserService.addAuthority(user.getUserId(), "ROLE_ADMIN");
        }
    }
}
