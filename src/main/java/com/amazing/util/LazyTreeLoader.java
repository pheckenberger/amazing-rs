package com.amazing.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.amazing.domain.OrganizationNode;

/**
 * Lazy tree loader. Loads children node by node, which is the slowest strategy using the most database queries: O(n)
 * 
 * @author hp
 */
@Component
@ConditionalOnProperty(prefix = "organization", name = "tree-loader-strategy", havingValue = "lazy", matchIfMissing = false)
public class LazyTreeLoader implements TreeLoader {

	/** SLF4J Logger */
	private final Logger log = LoggerFactory.getLogger(LazyTreeLoader.class);

	@Override
	public OrganizationNode loadDescendants(OrganizationNode organizationNode) {

		log.info("Loading descendants with the lazy strategy");
		loadChildrenRecursively(organizationNode);

		return organizationNode;
	}

	/**
	 * Load children recursively.
	 * 
	 * @param organizationNode the organization node.
	 */
	private void loadChildrenRecursively(OrganizationNode organizationNode) {

		if (!CollectionUtils.isEmpty(organizationNode.getChildren())) {
			for (OrganizationNode child : organizationNode.getChildren()) {
				loadChildrenRecursively(child);
			}
		}
	}
}
