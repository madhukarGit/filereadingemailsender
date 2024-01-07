package com.chadmockitoracle.chadmockito.repository;

import com.chadmockitoracle.chadmockito.entity.BankCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BankCustomerRepository extends JpaRepository<BankCustomer,Long> {

}
