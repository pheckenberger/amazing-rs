package com.amazing.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazing.service.OrganizationService;

import lombok.RequiredArgsConstructor;

/**
 * Organization initializer.
 * 
 * @author hp
 */
@Component
@RequiredArgsConstructor
public class OrganizationInitializer {

	/** SLF4J Logger */
	private final Logger log = LoggerFactory.getLogger(OrganizationInitializer.class);

	private final OrganizationProperties organizationProperties;
	private final OrganizationService organizationService;

	@PostConstruct
	public void init() {

		log.info("Initializing application: initOrganizationNodes={}", organizationProperties.isInitOrganizationNodes());
		if (organizationProperties.isInitOrganizationNodes()) {
			organizationService.initOrganizationNodes();
		}
	}
}
