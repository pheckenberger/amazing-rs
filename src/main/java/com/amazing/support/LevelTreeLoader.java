package com.amazing.support;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.amazing.domain.OrganizationNode;
import com.amazing.repository.OrganizationNodeRepository;

/**
 * Level tree loader, which reads each level once. The query count expectation is O(log(n)).
 * 
 * @author hp
 */
@Component
@ConditionalOnProperty(prefix = "organization", name = "tree-loader-strategy", havingValue = "level", matchIfMissing = true)
public class LevelTreeLoader implements TreeLoader {

	/** SLF4J Logger */
	private final Logger log = LoggerFactory.getLogger(LevelTreeLoader.class);

	@Autowired
	private OrganizationNodeRepository organizationNodeRepository;

	@Override
	public OrganizationNode loadDescendants(OrganizationNode organizationNode) {

		log.info("Loading descendants with the level strategy");

		List<OrganizationNode> currentLevel = Collections.singletonList(organizationNode);
		MultiValueMap<OrganizationNode, OrganizationNode> relationships = new LinkedMultiValueMap<>();

		loadLevelRecursively(currentLevel, 1, relationships);

		return rebuildSubtreeRecursively(organizationNode, relationships);
	}

	/**
	 * Load level.
	 * 
	 * @param currentLevel the currentt level
	 * @param relativeLevel the relative level
	 * @param relationships the map of organization node relationships
	 */
	private void loadLevelRecursively(List<OrganizationNode> currentLevel, int relativeLevel,
			MultiValueMap<OrganizationNode, OrganizationNode> relationships) {

		if (CollectionUtils.isEmpty(currentLevel)) {
			return;
		}

		List<OrganizationNode> nextLevel = organizationNodeRepository.findAllByParentIn(currentLevel);
		log.debug("Level tree loader has loaded level: relativeLevel={}, nextLevelNodeCount={}", relativeLevel, nextLevel.size());

		nextLevel.stream().forEach(o -> relationships.add(o.getParent(), o));

		loadLevelRecursively(nextLevel, relativeLevel + 1, relationships);
	}

	/**
	 * Rebuild subtree recursively.
	 * 
	 * @param organizationNode the organization node
	 * @return the rebuilt organization node
	 */
	private OrganizationNode rebuildSubtreeRecursively(OrganizationNode organizationNode,
			MultiValueMap<OrganizationNode, OrganizationNode> relationships) {

		OrganizationNode clone = new OrganizationNode();
		clone.setId(organizationNode.getId());
		clone.setHeight(organizationNode.getHeight());
		clone.setName(organizationNode.getName());

		List<OrganizationNode> childNodes = relationships.get(organizationNode);
		if (!CollectionUtils.isEmpty(childNodes)) {
			for (OrganizationNode childNode : childNodes) {
				clone.addChild(rebuildSubtreeRecursively(childNode, relationships));
			}
		}

		return clone;
	}
}
