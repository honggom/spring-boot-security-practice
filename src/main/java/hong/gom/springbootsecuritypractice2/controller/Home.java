package hong.gom.springbootsecuritypractice2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {

    @GetMapping("/greeting")
    public String greeting(){
        return "hello";
    }

    @PostMapping("/greeting")
    public String greeting(@RequestBody String name){
        return "hello "+name;
    }
}
