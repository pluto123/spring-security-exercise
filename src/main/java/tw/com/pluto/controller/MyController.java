package tw.com.pluto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tw.com.pluto.service.UserService;

@RestController
public class MyController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "Index";
    }

    @GetMapping("/user")
    public String user() {
        return "User";
    }

    @GetMapping("/user/{id}")
    public String user(@PathVariable Integer id) {
        return userService.getUser(id).getUsername();
    }

    @GetMapping("admin")
    public String admin() {
        return "Admin";
    }
}
