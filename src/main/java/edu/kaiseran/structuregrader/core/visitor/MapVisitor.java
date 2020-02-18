package edu.kaiseran.structuregrader.core.visitor;

import edu.kaiseran.structuregrader.core.NamedMap;

import javax.annotation.CheckForNull;

/**
 * A visitor for a NamedCollection of type ITEM.
 *
 * @param <ITEM> The type of item in the collection.
 */
public interface MapVisitor<ITEM> {
	void visit(@CheckForNull final NamedMap<ITEM> collection);
}
