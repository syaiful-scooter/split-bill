package com.syaiful.split.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syaiful.split.exceptions.InvalidPayloadException;
import com.syaiful.split.exceptions.UserIdAlreadyExistException;
import com.syaiful.split.service.PersonService;
import com.syaiful.split.dto.UserDto;

import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor

public class RegisterController {

    private PersonService personService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto) {
        if (Objects.isNull(userDto)) {
            throw new InvalidPayloadException("Payload cannot be Null");
        }
        if(personService.findByUsername(userDto.getEmail())){
            throw new UserIdAlreadyExistException("Username is already taken");
        }

        return personService.saveUser(userDto);
    }
}
