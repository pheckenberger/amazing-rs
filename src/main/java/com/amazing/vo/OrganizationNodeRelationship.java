package com.amazing.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Organization node relationship.
 * 
 * @author hp
 */
@RequiredArgsConstructor
@Getter
public class OrganizationNodeRelationship {

	private final long parentId;
	private final long childId;
}
