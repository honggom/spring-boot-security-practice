package hong.gom.springbootsecuritypractice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Page {

    /*
    @GetMapping("/")
    public String home(){
        return "home";
    }
     */

    @GetMapping("/main")
    public String main() {
        return "main";
    }

    @GetMapping("/access-denied")
    public String accessDenied(){
        return "access-denied";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/admin-page")
    public String adminPage() {
        return "admin-page";
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/user-page")
    public String userPage() {
        return "user-page";
    }
}
