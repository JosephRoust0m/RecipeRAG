package com.ecse428.WaitForDinner.repository;

import com.ecse428.WaitForDinner.model.PreferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreferenceTypeRepository extends JpaRepository<PreferenceType, Integer> {
    Optional<PreferenceType> findByName(String name);
}
