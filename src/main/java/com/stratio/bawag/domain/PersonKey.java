package com.stratio.bawag.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PersonKey {

    private final String personNumber;

    private final EntityEnum entity;
}
