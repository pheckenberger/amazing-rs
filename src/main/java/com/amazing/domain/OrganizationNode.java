package com.amazing.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Organization node.
 * 
 * @author hp
 */
@Entity
@Table(indexes = @Index(name = "ix_organization_node_parent", columnList = "parent_id"), uniqueConstraints = @UniqueConstraint(name = "ux_organization_node_name", columnNames = "name"))
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class OrganizationNode implements Serializable {

	/** Serial */
	private static final long serialVersionUID = -3685530654121593864L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;
	@Column(length = 200, nullable = false)
	@ToString.Include
	private String name;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_organization_root"))
	@JsonIgnore
	private OrganizationNode root;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_organization_parent"))
	@JsonIgnore
	private OrganizationNode parent;
	@Column(nullable = false)
	@ToString.Include
	private Integer height = 0;
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	private List<OrganizationNode> children;

	/**
	 * Add child organization.
	 * 
	 * @param child the child to add, not <code>null</code>
	 */
	public void addChild(OrganizationNode child) {

		Assert.notNull(child, "Child is null");

		if (child.getParent() == this) {
			return;
		}

		Assert.isNull(child.getParent(), "Child already belongs to another node");

		if (children == null) {
			children = new ArrayList<>();
		}

		child.setRoot(root);
		child.setParent(this);
		child.setHeight(height + 1);
		children.add(child);
	}

	/**
	 * Remove child organization.
	 * 
	 * @param child the child to remove, not <code>null</code>
	 */
	public void removeChild(OrganizationNode child) {

		Assert.notNull(child, "Child is null");
		Assert.isTrue(equals(child.getParent()), "Child doesn't belong to this node");

		child.setRoot(child);
		child.setParent(null);
		child.setHeight(0);
		children.remove(child);
	}
}
