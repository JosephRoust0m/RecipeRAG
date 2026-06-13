package com.ecse428.WaitForDinner.repository;

import com.ecse428.WaitForDinner.model.DietType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietRepository extends JpaRepository<DietType, Integer> {
    Optional<DietType> findByName(String name);
}