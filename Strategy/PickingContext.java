package com.usfq.logistpulse.strategy;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class PickingContext {
    private final Map<String, PickingPolicy> policies;

    // Spring Boot automatically injects all PickingPolicy beans keyed by @Component name ("FEFO", "FIFO", "LIFO")
    public PickingContext(Map<String, PickingPolicy> policies) {
        this.policies = policies;
    }

    public InventoryBatch executePicking(String policyName, List<InventoryBatch> batches, int quantity) {
        PickingPolicy activePolicy = policies.get(policyName.toUpperCase());
        if (activePolicy == null) {
            throw new IllegalArgumentException("Política de picking no soportada: " + policyName);
        }
        return activePolicy.selectBatch(batches, quantity);
    }
}
