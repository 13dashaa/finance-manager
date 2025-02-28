package com.example.fmanager.models;

import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private Set<Budgets> budgets;
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Transactions> transactions;
}
