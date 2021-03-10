package com.amazing.support;

/**
 * Enumeration of tree loader strategies.
 * 
 * @author hp
 */
public enum TreeLoaderStrategy {

	/** Uses lazy loading, which is a memory saving but slow strategy */
	LAZY,
	/** Uses level loading, which is a memory saving yet quite fast strategy */
	LEVEL,
	/** Uses greedy loading, which may be more memory consuming, but it's the fastest strategy */
	GREEDY
}
