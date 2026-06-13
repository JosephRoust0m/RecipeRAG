package com.ecse428.WaitForDinner.repository;

import com.ecse428.WaitForDinner.model.PreferenceType;
import com.ecse428.WaitForDinner.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceRepository extends JpaRepository<PreferenceType, Integer> {
    Optional<PreferenceType> findByName(String name);
    List<PreferenceType> findAllByUsers(User user);
}
