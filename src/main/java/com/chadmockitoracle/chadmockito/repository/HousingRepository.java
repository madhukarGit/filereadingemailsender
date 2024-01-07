package com.chadmockitoracle.chadmockito.repository;

import com.chadmockitoracle.chadmockito.entity.HousingProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HousingRepository extends JpaRepository<HousingProduction,Long> {
}
