package com.chadmockitoracle.chadmockito.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "housing_production")
@Data
@NoArgsConstructor
public class HousingProduction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = "seq", initialValue = 1)
    private Long id;
    private String buildingPermitApplicationNumber;
    private String permitAddress;
    private String permitDescription;
    private String existingUnitsInPTSDatabase;
    private String proposedUnitsInPTSDatabase;
    private String actualProposedUnits;
}
