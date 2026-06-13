package com.ecse428.WaitForDinner.repository;

import com.ecse428.WaitForDinner.model.Pantry;
import com.ecse428.WaitForDinner.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PantryRepository extends JpaRepository<Pantry, Integer> {
    List<Pantry> findAllByUsers(User user);
}