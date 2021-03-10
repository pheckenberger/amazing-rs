package com.amazing.support;

import com.amazing.domain.OrganizationNode;

/**
 * Interface of the tree loader, which is responsible for loading descendant nodes.
 * 
 * @author hp
 */
public interface TreeLoader {

	/**
	 * Load descendants.
	 * 
	 * @param organizationNode the organization node, not <code>null</code>
	 * @return the organization node, not <code>null</code>
	 */
	OrganizationNode loadDescendants(OrganizationNode organizationNode);
}
