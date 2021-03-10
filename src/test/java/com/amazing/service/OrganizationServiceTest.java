package com.amazing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import com.amazing.config.OrganizationProperties;
import com.amazing.domain.OrganizationNode;
import com.amazing.repository.OrganizationNodeRepository;
import com.amazing.service.impl.OrganizationServiceImpl;
import com.amazing.support.TreeLoader;

/**
 * Organization service test.
 * 
 * @author hp
 */
@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTest {

	@Mock
	private OrganizationNodeRepository organizationNodeRepository;
	@Mock
	private TreeLoader treeLoader;

	/**
	 * Build tree.
	 * 
	 * @return the tree
	 */
	private OrganizationNode buildTree() {

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Nodes

		long id = 1;
		OrganizationNode rootNode = createNode(id++, "root");

		String[] names = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n" };
		OrganizationNode[] organizationNodes = new OrganizationNode[names.length];

		for (int i = 0; i < names.length; i++) {
			organizationNodes[i] = createNode(id++, names[i]);
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Relationships

		// root
		rootNode.addChild(organizationNodes[0]);
		rootNode.addChild(organizationNodes[1]);
		rootNode.addChild(organizationNodes[2]);
		rootNode.addChild(organizationNodes[3]);

		// a
		organizationNodes[0].addChild(organizationNodes[4]);
		organizationNodes[0].addChild(organizationNodes[5]);

		// b
		organizationNodes[1].addChild(organizationNodes[6]);
		organizationNodes[1].addChild(organizationNodes[7]);
		organizationNodes[1].addChild(organizationNodes[8]);

		// c
		organizationNodes[2].addChild(organizationNodes[9]);

		// e
		organizationNodes[4].addChild(organizationNodes[10]);
		organizationNodes[4].addChild(organizationNodes[11]);
		organizationNodes[4].addChild(organizationNodes[12]);
		organizationNodes[4].addChild(organizationNodes[13]);

		return rootNode;
	}

	/**
	 * Create node.
	 * 
	 * @param id the id
	 * @param name the name
	 * @return the organization node
	 */
	private OrganizationNode createNode(long id, String name) {

		OrganizationNode organizationNode = new OrganizationNode();
		organizationNode.setId(id);
		organizationNode.setName(name);

		return organizationNode;
	}

	/**
	 * Find node by in tree by predicate.
	 * 
	 * @param organizationNode the organization node, not <code>null</code>
	 * @param predicate the predicate
	 * @return the optional organization node, not <code>null</code>
	 */
	private Optional<OrganizationNode> findByPredicate(OrganizationNode organizationNode, Predicate<OrganizationNode> predicate) {

		if (predicate.test(organizationNode)) {
			return Optional.of(organizationNode);
		}

		if (!CollectionUtils.isEmpty(organizationNode.getChildren())) {
			for (OrganizationNode childNode : organizationNode.getChildren()) {
				Optional<OrganizationNode> optionalMatch = findByPredicate(childNode, predicate);
				if (optionalMatch.isPresent()) {
					return optionalMatch;
				}
			}
		}

		return Optional.empty();
	}

	@Test
	public void testMoveOrganizationNode_moveRoot() {

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Arrange

		OrganizationProperties organizationProperties = new OrganizationProperties();
		OrganizationService organizationService = new OrganizationServiceImpl(organizationProperties, organizationNodeRepository,
				treeLoader);

		OrganizationNode rootNode = buildTree();

		when(organizationNodeRepository.findById(1L)).thenReturn(Optional.of(rootNode));

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Act + assert

		UnsupportedOperationException e = assertThrows(UnsupportedOperationException.class,
				() -> organizationService.moveOrganizationNode(1L, 6L));

		assertEquals("Unable to move root node", e.getMessage());
	}

	@Test
	public void testMoveOrganizationNode_moveUnderDescendant() {

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Arrange

		OrganizationProperties organizationProperties = new OrganizationProperties();
		OrganizationService organizationService = new OrganizationServiceImpl(organizationProperties, organizationNodeRepository,
				treeLoader);

		OrganizationNode rootNode = buildTree();

		when(organizationNodeRepository.findById(2L)).thenReturn(findByPredicate(rootNode, o -> o.getId() == 2L));
		when(organizationNodeRepository.findById(12L)).thenReturn(findByPredicate(rootNode, o -> o.getId() == 12L));

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Act + assert

		UnsupportedOperationException e = assertThrows(UnsupportedOperationException.class,
				() -> organizationService.moveOrganizationNode(2L, 12L));

		assertEquals("Unable to move under own descendant: 2", e.getMessage());
	}

	@Test
	public void testMoveOrganizationNode_valid() {

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Arrange

		OrganizationProperties organizationProperties = new OrganizationProperties();
		OrganizationService organizationService = new OrganizationServiceImpl(organizationProperties, organizationNodeRepository,
				treeLoader);

		OrganizationNode rootNode = buildTree();

		when(organizationNodeRepository.findById(15L)).thenReturn(findByPredicate(rootNode, o -> o.getId() == 15L));
		when(organizationNodeRepository.findById(12L)).thenReturn(findByPredicate(rootNode, o -> o.getId() == 12L));

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Act

		organizationService.moveOrganizationNode(15L, 12L);

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Assert

		ArgumentCaptor<OrganizationNode> captor = ArgumentCaptor.forClass(OrganizationNode.class);
		verify(organizationNodeRepository).save(captor.capture());

		OrganizationNode savedOrganizationNode = captor.getValue();
		assertEquals(15L, savedOrganizationNode.getId());
		assertEquals(12L, savedOrganizationNode.getParent().getId());
	}
}
