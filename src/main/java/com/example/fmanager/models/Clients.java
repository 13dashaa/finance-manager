package com.example.fmanager.models;

import java.time.LocalDateTime;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
public class Clients {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Accounts> accounts;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Goals> goals;
    @ManyToMany(mappedBy = "clients")
    private Set<Budgets> budgets;

    @PreRemove
    private void removeUserFromBudgets() {
        for (Budgets budget : budgets) {
            budget.getClients().remove(this);
        }
    }
}
