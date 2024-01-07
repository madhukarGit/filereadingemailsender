package com.chadmockitoracle.chadmockito.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Housing {
    private String buildingPermitApplicationNumber;
    private String permitAddress;
    private String permitDescription;
    private String existingUnitsInPTSDatabase;
    private String proposedUnitsInPTSDatabase;
    private String actualProposedUnits;
}

