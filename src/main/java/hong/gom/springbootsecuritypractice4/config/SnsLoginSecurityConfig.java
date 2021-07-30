package hong.gom.springbootsecuritypractice4.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SnsLoginSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SpOAuth2UserService oAuth2UserService;
    private final SpOidcUserService oidcUserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.oauth2Login(
                oauth2 -> oauth2.userInfoEndpoint(
                        userInfo -> userInfo.userService(oAuth2UserService)
                                .oidcUserService(oidcUserService)
                ).successHandler()
        )

        ;
    }
}
