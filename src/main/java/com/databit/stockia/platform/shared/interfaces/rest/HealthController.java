package com.databit.stockia.platform.shared.interfaces.rest;

import com.databit.stockia.platform.shared.interfaces.rest.resources.HealthResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Liveness check used by the deployment platform and the web application (public).
 */
@RestController
@RequestMapping(value = "/api/v1/health", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Health", description = "Liveness check")
public class HealthController {
    private final String applicationName;
    private final String version;

    public HealthController(@Value("${spring.application.name}") String applicationName,
                            @Value("${stockia.version:1.0.0}") String version) {
        this.applicationName = applicationName;
        this.version = version;
    }

    @Operation(summary = "Check that the API is running")
    @GetMapping
    public HealthResource getHealth() {
        return new HealthResource("UP", applicationName, version, Instant.now());
    }
}
