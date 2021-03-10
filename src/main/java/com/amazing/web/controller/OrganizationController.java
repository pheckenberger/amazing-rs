package com.amazing.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazing.domain.OrganizationNode;
import com.amazing.service.OrganizationService;

/**
 * Organization controller.
 * 
 * @author hp
 */
@RequestMapping("/v1/organizations")
@RestController
public class OrganizationController {

	/** SLF4J Logger */
	private final Logger log = LoggerFactory.getLogger(OrganizationController.class);

	@Autowired
	private OrganizationService organizationService;

	@GetMapping(path = "/{organizationNodeId}")
	ResponseEntity<OrganizationNode> fetchOrganizationNode(@PathVariable long organizationNodeId, @AuthenticationPrincipal User user) {

		log.info("Handling fetch organization node request: organizationNodeId={}, user={}", organizationNodeId, user.getUsername());
		return ResponseEntity.of(organizationService.findOrganizationNode(organizationNodeId));
	}

	@PatchMapping("/{organizationNodeId}/move")
	ResponseEntity<OrganizationNode> moveOrganizationNode(@PathVariable long organizationNodeId, @RequestParam long newParentNodeId,
			@AuthenticationPrincipal User user) {

		log.info("Handling move organization node request: organizationNodeId={}, newParentNodeId={}, user={}", organizationNodeId,
				newParentNodeId, user.getUsername());

		organizationService.moveOrganizationNode(organizationNodeId, newParentNodeId);
		return ResponseEntity.ok(null);
	}
}