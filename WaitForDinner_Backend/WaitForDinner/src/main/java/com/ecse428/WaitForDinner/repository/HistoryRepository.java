package com.ecse428.WaitForDinner.repository;

import com.ecse428.WaitForDinner.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer> {
}
