package com.cydeo.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargeRequest {

    public enum Currency {
        EUR, USD;
    }
    private String description;
    private BigDecimal amount;
    private Currency currency;
    private String stripeEmail;
    private String stripeToken;
}
