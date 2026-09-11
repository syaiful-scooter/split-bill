package com.syaiful.split.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/test")
public class HomeController
{
    @GetMapping("/")
    public String index() {
        return "Hello spring boot";
    }

}
