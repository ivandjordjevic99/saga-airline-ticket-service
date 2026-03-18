package com.saga.airlinesystem.airlineticketservice.saga.simulations;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimulationsUtil {

    private final Environment env;

    public void simulateDelay() {
        boolean enableDelay = Boolean.parseBoolean(env.getProperty("saga.delay.enabled", "false"));
        long delayMs = Long.parseLong(env.getProperty("saga.delay.ms", "3000"));
        if (!enableDelay) {
            return;
        }
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
