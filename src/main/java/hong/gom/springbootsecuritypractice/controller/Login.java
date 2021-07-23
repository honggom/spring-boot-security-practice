package hong.gom.springbootsecuritypractice.controller;


import hong.gom.springbootsecuritypractice.dto.SecurityMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Login {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/auth")
    public Authentication auth() {
        return SecurityContextHolder.getContext()
                .getAuthentication();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/user")
    public SecurityMessage user(){
        logger.info("user() start ...");
        logger.info("user() start ...");

        return SecurityMessage.builder()
                .auth(SecurityContextHolder.getContext().getAuthentication())
                .message("User Info")
                .build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public SecurityMessage admin(){
        logger.info("admin() start ...");
        logger.info("admin() start ...");

        return SecurityMessage.builder()
                .auth(SecurityContextHolder.getContext().getAuthentication())
                .message("Admin Info")
                .build();
    }

}
