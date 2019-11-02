package edu.kaiseran.structuregrader.visitors;

import edu.kaiseran.structuregrader.NamedCollection;

import javax.annotation.CheckForNull;

/**
 * A visitor for a NamedCollection of type ITEM.
 *
 * @param <ITEM> The type of item in the collection.
 */
public interface CollectionVisitor<ITEM> {
	void visit(@CheckForNull final NamedCollection<ITEM> collection);
}
