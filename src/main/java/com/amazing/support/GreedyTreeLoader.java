package com.amazing.support;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.amazing.vo.OrganizationNodeRelationship;

/**
 * Greedy tree loader. Loads descendants using a sane superset of the organization node candidates. Expected query count is O(1).
 * 
 * @author hp
 */
@Component
@ConditionalOnProperty(prefix = "organization", name = "tree-loader-strategy", havingValue = "greedy", matchIfMissing = false)
public class GreedyTreeLoader implements TreeLoader {

	/** SLF4J Logger */
	private final Logger log = LoggerFactory.getLogger(GreedyTreeLoader.class);

	@Autowired
	private OrganizationNodeRepository organizationNodeRepository;

	/**
	 * Collect descendant IDs to load recursively.
	 * 
	 * @param id the id
	 * @param relationshipCache the relationshipcache
	 * @param idsToLoad the set of IDs to load, which is populated by this method
	 */
	private void collectDescendantIdsToLoadRecursively(long id, MultiValueMap<Long, Long> relationshipCache, Set<Long> idsToLoad) {

		idsToLoad.add(id);

		List<Long> childIds = relationshipCache.get(id);
		if (!CollectionUtils.isEmpty(childIds)) {
			for (Long childId : childIds) {
				collectDescendantIdsToLoadRecursively(childId, relationshipCache, idsToLoad);
			}
		}
	}

	@Override
	public OrganizationNode loadDescendants(OrganizationNode organizationNode) {

		log.info("Loading descendants with greedy strategy");

		// First query fetches all relationship candidates

		List<OrganizationNodeRelationship> relationships = organizationNodeRepository
				.findAllRelationshipsByHeightGreaterThan(organizationNode.getHeight());

		log.debug("Greedy tree loader has loaded relationships: count={}", relationships.size());

		MultiValueMap<Long, Long> relationshipCache = new LinkedMultiValueMap<>();
		relationships.stream().forEach(r -> relationshipCache.add(r.getParentId(), r.getChildId()));

		Set<Long> idsToLoad = new HashSet<>();
		collectDescendantIdsToLoadRecursively(organizationNode.getId(), relationshipCache, idsToLoad);

		// Second query fetches all subtree nodes

		Map<Long, OrganizationNode> subtreeNodeCache = organizationNodeRepository.findAllById(idsToLoad).stream()
				.collect(Collectors.toMap(OrganizationNode::getId, Function.identity()));

		return rebuildSubtreeRecursively(organizationNode.getId(), relationshipCache, subtreeNodeCache);
	}

	/**
	 * Rebuild subtree recursively to avoid lazy loading problems outside the transaction.
	 * 
	 * @param organizationNodeId the organization node ID
	 * @param relationshipCache the relationship cache
	 * @param subtreeNodeCache the subtree node cache
	 * @return the rebuilt organization node
	 */
	private OrganizationNode rebuildSubtreeRecursively(long organizationNodeId, MultiValueMap<Long, Long> relationshipCache,
			Map<Long, OrganizationNode> subtreeNodeCache) {

		OrganizationNode organizationNode = subtreeNodeCache.get(organizationNodeId);

		OrganizationNode clone = new OrganizationNode();
		clone.setId(organizationNodeId);
		clone.setHeight(organizationNode.getHeight());
		clone.setName(organizationNode.getName());

		List<Long> childIds = relationshipCache.get(organizationNodeId);
		if (!CollectionUtils.isEmpty(childIds)) {
			for (Long childId : childIds) {
				clone.addChild(rebuildSubtreeRecursively(childId, relationshipCache, subtreeNodeCache));
			}
		}

		return clone;
	}
}
