package hong.gom.springbootsecuritypractice4.config;

import hong.gom.springbootsecuritypractice4.service.SpUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SpOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final SpUserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException
    {
        Object principal = authentication.getPrincipal();
        if(principal instanceof OidcUser){

        }else if(principal instanceof OAuth2User){

        }
    }
}
