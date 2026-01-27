package com.leads.microcube.base;


import lombok.Synchronized;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGeneratorHelper {

    @Synchronized
    public static String generateUUid() {
        return UUID.randomUUID().toString();
    }
}
