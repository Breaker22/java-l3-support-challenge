package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.enums.CustomerSegment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Customer {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private CustomerSegment segment;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private List<Transaction> transactions = new ArrayList<>();

}
