package com.stratio.bawag.domain;

import java.util.Arrays;

public enum EntityEnum {

    BAWAG("16000"),
    PSK("60000"),
    SPARDA("10000"),
    EXTERNAL("");

    private String entityNumValue;

    EntityEnum(String entityNumValue) {
        this.entityNumValue = entityNumValue;
    }

    public static EntityEnum getEntityfromString(String entityNumValue) {
        return Arrays.stream(EntityEnum.values())
                .filter(value -> value.getEntityNumValue().equalsIgnoreCase(entityNumValue)).findFirst()
                .orElse(EntityEnum.EXTERNAL);
    }

    public String getEntityNumValue() {
        return entityNumValue;
    }
}
