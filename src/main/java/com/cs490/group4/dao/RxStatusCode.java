package com.cs490.group4.dao;

import com.cs490.group4.security.RoleDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RoleDeserializer.class)
public enum RxStatusCode {

    NEW_PRESCRIPTION,
    READY_FOR_PICKUP,
    FULFILLED

}