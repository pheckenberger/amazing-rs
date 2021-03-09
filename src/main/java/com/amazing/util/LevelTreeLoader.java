package com.amazing.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.amazing.domain.OrganizationNode;

/**
 * Level tree loader, which reads each level once.
 * 
 * @author hp
 */
@Component
@ConditionalOnProperty(prefix = "organization", name = "tree-loader-strategy", havingValue = "level", matchIfMissing = true)
public class LevelTreeLoader implements TreeLoader {

	/** SLF4J Logger */
	private final Logger log = LoggerFactory.getLogger(LevelTreeLoader.class);

	@Override
	public OrganizationNode loadDescendants(OrganizationNode organizationNode) {

		log.info("Loading descendants with the level strategy");

		// TODO Auto-generated method stub

		return organizationNode;
	}
}
