package com.amazing.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amazing.domain.OrganizationNode;
import com.amazing.vo.OrganizationNodeRelationship;

/**
 * Interface of the node repository.
 * 
 * @author hp
 */
public interface OrganizationNodeRepository extends JpaRepository<OrganizationNode, Long> {

	Optional<OrganizationNode> findByName(String name);

	List<OrganizationNode> findAllByParentIn(Collection<OrganizationNode> parents);

	@Query("select new com.amazing.vo.OrganizationNodeRelationship(o.parent.id, o.id) from OrganizationNode o where ?1 < o.height")
	List<OrganizationNodeRelationship> findAllRelationshipsByHeightGreaterThan(int height);
}
