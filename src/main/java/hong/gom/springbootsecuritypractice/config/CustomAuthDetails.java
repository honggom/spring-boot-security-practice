package hong.gom.springbootsecuritypractice.config;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Component
public class CustomAuthDetails implements AuthenticationDetailsSource<HttpServletRequest, RequestInformation> {

    @Override
    public RequestInformation buildDetails(HttpServletRequest request) {
        return RequestInformation.builder()
                .remoteIp(request.getRemoteAddr())
                .sessionId(request.getSession().getId())
                .loginTime(LocalDateTime.now())
                .build();

    }
}
