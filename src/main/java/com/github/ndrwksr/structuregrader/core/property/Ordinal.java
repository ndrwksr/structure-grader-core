package com.github.ndrwksr.structuregrader.core.property;

/**
 * Interface that allows wrappers to return the index of their source element in an ordered collection.
 */
public interface Ordinal extends Named {
	/**
	 * @return the index of the source element in its respective collection.
	 */
	int getIndex();
}
