/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.spring.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for scheduled services.
 * Used to disable scheduling in some tests.
 */
@Configuration

@ConditionalOnProperty(value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true)
@EnableScheduling
public class ScheduleConfig {
}
