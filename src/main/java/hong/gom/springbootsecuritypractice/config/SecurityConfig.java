package hong.gom.springbootsecuritypractice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthDetails customAuthDetails;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.inMemoryAuthentication()
                .withUser(User.builder()
                        .username("hong2").password(passwordEncoder().encode("1")).roles("USER"))
                .withUser(User.builder()
                        .username("admin").password(passwordEncoder().encode("1")).roles("ADMIN"));
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER"); //ADMIN은 USER의 권한을 다 가지고 있다.
        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(request -> {
            request.antMatchers("/").permitAll()      // "/" 요청에 대하여 접근권한 필요없음
                    .antMatchers("/auth").permitAll() // "/auth" 요청에 대하여 접근권한 필요없음
                    .anyRequest().authenticated();               // 그외 요청은 접근권한 필요함
        })
                .formLogin(                                        //formLogin()에 파라미터를 넘기지 않으면 기본 시큐리티 login form을 사용함
                        login -> login.loginPage("/login")
                                .permitAll()
                                .defaultSuccessUrl("/main", false) // 로그인 성공시 요청 URL
                                .failureUrl("/login-error") //로그인 실패시 진입 페이지
                                .authenticationDetailsSource(customAuthDetails)
                )
                .logout(logout -> logout.logoutSuccessUrl("/login")) // 로그아웃 성공 진입 페이지
                .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"));

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers( "/static/css/**", "/static/js/**");
        /*
        web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations()
                );
         */
    }
}
