package com.amazing.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Organization configuration.
 * 
 * @author hp
 */
@Configuration
@EnableConfigurationProperties(OrganizationProperties.class)
public class OrganizationConfig {
}
