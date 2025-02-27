package com.example.fmanager.repository;

import com.example.fmanager.models.Budgets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budgets, Integer> {
}

