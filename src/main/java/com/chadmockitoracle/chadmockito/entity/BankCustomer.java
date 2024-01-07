package com.chadmockitoracle.chadmockito.entity;

import com.chadmockitoracle.chadmockito.constants.AccountStatus;
import com.chadmockitoracle.chadmockito.constants.Region;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "bank_customer_new")
public class BankCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGen")
    @SequenceGenerator(name = "seqGen", sequenceName = "seq", initialValue = 1)
    private Long id;
    private String name;
    private String branch;
    private LocalDate registeredDate;
    private Double balance;
    private String status;
    private String region;
    @CreationTimestamp
    private LocalDate createdOn;
}
