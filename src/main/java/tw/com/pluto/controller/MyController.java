package tw.com.pluto.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/")
    public String index() {
        return "Index";
    }

    @GetMapping("/user")
    public String user() {
        return "User";
    }

    @GetMapping("admin")
    public String admin() {
        return "Admin";
    }
}
