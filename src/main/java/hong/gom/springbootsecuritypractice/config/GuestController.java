package hong.gom.springbootsecuritypractice.config;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guest")
public class GuestController {

    @PreAuthorize("hasAnyAuthority('ROLE_GUEST')")
    @GetMapping("/main")
    public String main() {
        return "guest-main";
    }
}
