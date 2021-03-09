package com.amazing.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.amazing.config.OrganizationProperties;
import com.amazing.domain.OrganizationNode;
import com.amazing.repository.OrganizationNodeRepository;
import com.amazing.service.OrganizationService;
import com.amazing.util.TreeLoader;

import lombok.RequiredArgsConstructor;

/**
 * Default implemetation of the organization service.
 * 
 * @author hp
 */
@RequiredArgsConstructor
@Service
public class OrganizationServiceImpl implements OrganizationService {

	@Autowired
	private OrganizationProperties organizationProperties;
	@Autowired
	private OrganizationNodeRepository organizationNodeRepository;
	@Autowired
	private TreeLoader treeLoader;

	@Override
	@Transactional(readOnly = true)
	public Optional<OrganizationNode> findOrganizationNode(long organizationNodeId) {
		return organizationNodeRepository.findById(organizationNodeId).map(treeLoader::loadDescendants);
	}

	/**
	 * Init (internal method).
	 * 
	 * @param name the name, not blank
	 * @param height the height
	 * @param parent the parent or <code>null</code>
	 */
	private void initInternal(String name, int height, OrganizationNode parent) {

		OrganizationNode organizationNode = organizationNodeRepository.findByName(name).orElseGet(() -> {

			OrganizationNode newOrganizationNode = new OrganizationNode();
			newOrganizationNode.setName(name);
			newOrganizationNode.setHeight(height);

			if (parent == null) {
				newOrganizationNode.setRoot(newOrganizationNode);
			} else {
				newOrganizationNode.setRoot(parent.getRoot());
				parent.addChild(newOrganizationNode);
			}

			organizationNodeRepository.save(newOrganizationNode);
			return newOrganizationNode;
		});

		List<String> childNames = organizationProperties.getOrganizationNodes().get(name);
		if (!CollectionUtils.isEmpty(childNames)) {
			for (String childName : childNames) {
				initInternal(childName, height + 1, organizationNode);
			}
		}
	}

	@Override
	@Transactional
	public void initOrganizationNodes() {

		initInternal("root", 0, null);
	}

	@Override
	@Transactional
	public void moveOrganizationNode(long organizationId, long newParentNodeId) {

		// TODO Auto-generated method stub
	}
}
