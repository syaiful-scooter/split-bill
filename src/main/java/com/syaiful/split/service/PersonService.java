package com.syaiful.split.service;

import com.syaiful.split.dto.PersonsDto;
import com.syaiful.split.dto.UserDto;
import com.syaiful.split.model.Persons;
import com.syaiful.split.repository.PersonsRepo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService implements UserDetailsService{

    @Autowired
    private PersonsRepo repo ;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Persons user = repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

//        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
//                List.of(new SimpleGrantedAuthority(user.getRole())));
        return new User(
                user.getEmail(),
                user.getPassword(),
                List.of(
                        new SimpleGrantedAuthority(user.getRole())
                )
        );
    }

    public List<PersonsDto> get(){
        List<Persons> org = repo.findAll();
        List<PersonsDto> orgDto = new ArrayList<PersonsDto>();

        for (Persons orang : org){
           PersonsDto personDto = new PersonsDto();
            personDto.setId(orang.getId());
            personDto.setFirstName(orang.getFirstName());
            personDto.setLastName(orang.getLastName());

            orgDto.add(personDto);
        }

        return orgDto;
    }



    public boolean findByUsername(String email) {
        Optional<Persons> byUsername = repo.findByEmail(email);
        if(byUsername.isPresent()) {
            return true;
        }
        return false;
    }

    public ResponseEntity<UserDto> saveUser(UserDto dto){
        Persons entity = new Persons();
        dto.setPassword(passwordEncoder	.encode(dto.getPassword()));
        BeanUtils.copyProperties(dto, entity);

        Persons savedUser = repo.save(entity);

        dto.setPassword("***");
        dto.setEmail(savedUser.getEmail());

        return ResponseEntity.ok(dto);

    }
}
