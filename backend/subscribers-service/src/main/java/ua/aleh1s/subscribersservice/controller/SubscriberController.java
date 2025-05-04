package ua.aleh1s.subscribersservice.controller;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional(readOnly = true)
@RequestMapping("/api/v1/subscribers")
public class SubscriberController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from subscribers service";
    }
}
