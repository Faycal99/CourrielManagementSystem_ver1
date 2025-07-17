package dgb.Mp.controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final PasswordEncoder passwordEncoder;

    public TestController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/hash")
    public String hashPassword() {
        return passwordEncoder.encode("password123");
    }
}
