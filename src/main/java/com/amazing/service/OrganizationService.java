package com.amazing.service;

import java.util.Optional;

import com.amazing.domain.OrganizationNode;

/**
 * Interface of the organization service.
 * 
 * @author hp
 */
public interface OrganizationService {

	/**
	 * Find organization node with all descendants.
	 * 
	 * @param organizationNodeId the organization node ID
	 * @return the optional organization node, not <code>null</code>
	 */
	Optional<OrganizationNode> findOrganizationNode(long organizationNodeId);

	/**
	 * Initialize default organization nodes.
	 */
	void initOrganizationNodes();

	/**
	 * Move organization node.
	 * 
	 * @param organizationNodeId the organization node ID
	 * @param newParentNodeId the new parent node ID
	 * @return the optional organization node, not <code>null</code>
	 */
	Optional<OrganizationNode> moveOrganizationNode(long organizationNodeId, long newParentNodeId);
}
