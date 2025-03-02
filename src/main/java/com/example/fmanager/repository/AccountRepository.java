package com.example.fmanager.repository;

import com.example.fmanager.models.Accounts;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, Integer> {
    @Query("SELECT a FROM Accounts a JOIN a.client c WHERE c.id = :clientId")
    List<Accounts> findAllByClientId(@Param("clientId") int clientId);

}
