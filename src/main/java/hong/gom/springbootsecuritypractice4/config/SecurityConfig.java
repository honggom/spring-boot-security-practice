package hong.gom.springbootsecuritypractice4.config;

import hong.gom.springbootsecuritypractice4.service.SpUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpSessionEvent;
import javax.sql.DataSource;
import java.time.LocalDateTime;


@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final SpUserService userService;

    private final DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher(){
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher(){
            @Override
            public void sessionCreated(HttpSessionEvent event){
                super.sessionCreated(event);
                LOGGER.info("=======세션 이벤트=======");
                LOGGER.info("===>> [{}] 세션 생성됨 {} \n", LocalDateTime.now(), event.getSession().getId());
                LOGGER.info("=======세션 이벤트=======");
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent event){
                super.sessionDestroyed(event);
                LOGGER.info("=======세션 이벤트=======");
                LOGGER.info("===>> [{}] 세션 만료됨 {} \n", LocalDateTime.now(), event.getSession().getId());
                LOGGER.info("=======세션 이벤트=======");
            }

            @Override
            public void sessionIdChanged(HttpSessionEvent event, String oldSessionoId){
                super.sessionIdChanged(event, oldSessionoId);
                LOGGER.info("=======세션 이벤트=======");
                LOGGER.info("===>> [{}] 세션 아이디 변경 {}:{} \n", LocalDateTime.now(), oldSessionoId, event.getSession().getId());
                LOGGER.info("=======세션 이벤트=======");
            }
        });
    }

    @Bean
    PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        try {
            repository.removeUserTokens("1");
        }catch (Exception e){
            repository.setCreateTableOnStartup(true);
        }
        return repository;
    }


    @Bean
    PersistentTokenBasedRememberMeServices rememberMeServices(){
        PersistentTokenBasedRememberMeServices service =
                new PersistentTokenBasedRememberMeServices("hello",
                        userService,
                        tokenRepository()
                        );
        return service;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(request->
                        request.antMatchers("/").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(login->
                        login.loginPage("/login")
                                .loginProcessingUrl("/loginprocess")
                                .permitAll()
                                .defaultSuccessUrl("/", false)
                                .failureUrl("/login-error")
                )
                .logout(logout->
                        logout.logoutSuccessUrl("/"))
                .exceptionHandling(error->
                        error.accessDeniedPage("/access-denied")
                )
                //토큰을 검증하는 방법이 PersistentTokenBasedRememberMeServices로 변경되어 적용된다.
                .rememberMe(r -> r.rememberMeServices(rememberMeServices()));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations(),
                        PathRequest.toH2Console()
                );
    }

    /*
    PersistentTokenBasedRememberMeServices 동작 방식
    1. 세션이 있으면 세션을 기준으로 로그인 절차 생략
    2. 세션이 없으면 토큰 값을 디비 값과 비교 이때 db에는 토큰의 시리즈 값이 저장되어 있음
    3. 새로운 토큰을 발행하면 클라이언트 쪽의 토큰과 db의 토큰을 update한다.
     */

}