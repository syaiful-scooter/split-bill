package com.syaiful.split.controller;

import com.syaiful.split.dto.PersonsDto;
import com.syaiful.split.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/master/person")
public class PersonController {

    @Autowired
    PersonService srv ;

    @GetMapping("/")
    public List<PersonsDto> personsData(){
        List<PersonsDto> data = srv.get();
        return data;
    }
}
