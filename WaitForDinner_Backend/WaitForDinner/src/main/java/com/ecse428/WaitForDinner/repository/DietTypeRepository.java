package com.ecse428.WaitForDinner.repository;

import com.ecse428.WaitForDinner.model.DietType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DietTypeRepository extends JpaRepository<DietType, Integer> {
    Optional<DietType> findByName(String name);
}
