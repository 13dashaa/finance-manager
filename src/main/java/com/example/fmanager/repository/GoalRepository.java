package com.example.fmanager.repository;

import com.example.fmanager.models.Goals;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<Goals, Integer> {

    @Query("SELECT g FROM Goals g JOIN g.client c WHERE c.id = :clientId")
    List<Goals> findByClientId(@Param("clientId") int clientId);
}
