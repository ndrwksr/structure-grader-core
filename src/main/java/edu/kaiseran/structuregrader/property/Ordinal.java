package edu.kaiseran.structuregrader.property;

/**
 * Interface that allows wrappers to return their index in an ordered collection.
 */
public interface Ordinal extends Named {
	/**
	 * @return the index of the source element in its respective collection.
	 */
	int getIndex();
}
