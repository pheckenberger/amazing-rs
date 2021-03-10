package com.amazing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.MultiValueMap;

import com.amazing.support.TreeLoaderStrategy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Organization properties.
 * 
 * @author hp
 */
@ConfigurationProperties(prefix = "organization")
@RequiredArgsConstructor
@Getter
@Setter
public class OrganizationProperties {

	private String apiReaderUser;
	private String apiReaderPassword;
	private String apiWriterUser;
	private String apiWriterPassword;

	private TreeLoaderStrategy treeLoaderStrategy = TreeLoaderStrategy.LAZY;
	private boolean initOrganizationNodes = false;
	private MultiValueMap<String, String> organizationNodes;
}
