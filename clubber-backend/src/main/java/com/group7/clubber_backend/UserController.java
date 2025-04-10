package com.group7.clubber_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group7.clubber_backend.Responses.Ping;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public Ping getUser(@PathVariable String id) {
        return new Ping(id);
    }
}
