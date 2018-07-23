package com.stratio.bawag.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Customer {

    private final PersonKey personKey;

    private final String birthDate;

    private final String identFlag;

    private final String address;

    private final String hints;

    private final String oenbId;

    private final String registerNumber;

    private final boolean naturalPerson;
}
