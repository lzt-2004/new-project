package com.tt.fulfillflow.order;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class OrderNumberGenerator {

    public String nextOrderNo() {
        return "FF" + UUID.randomUUID().toString().replace("-", "");
    }
}
