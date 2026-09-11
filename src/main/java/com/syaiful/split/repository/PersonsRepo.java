package com.syaiful.split.repository;

import com.syaiful.split.model.Persons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonsRepo extends JpaRepository<Persons, Integer> {

    List<Persons> findByFirstName(String firstName);
    List<Persons> findByFirstNameAndLastName(String firstName, String lastName);

    Optional<Persons> findByEmail(String email);
}
