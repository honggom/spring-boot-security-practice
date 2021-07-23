package hong.gom.springbootsecuritypractice.config;

import hong.gom.springbootsecuritypractice.guest.GuestManager;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfigGuest extends WebSecurityConfigurerAdapter {

    private final GuestManager guestManager;
    //private final HostManager hostManager;
    //이런식으로 manager를 계속 추가할 수 있음

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.authenticationProvider(guestManager);
        //auth.authenticationProvider(hostManager);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomLoginFilter filter = new CustomLoginFilter(authenticationManager());
        http.authorizeRequests(
                request -> request.antMatchers("/").permitAll()
                                .anyRequest().authenticated()
        )
                //.formLogin(
                //login -> login.loginPage("/login")
                //.permitAll()
                .addFilterAt(filter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .exceptionHandling(e -> e.accessDeniedPage("/access-denied"));

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers( "/static/css/**", "/static/js/**");
    }
}
