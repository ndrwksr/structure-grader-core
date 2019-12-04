package edu.kaiseran.structuregrader.visitor;

import edu.kaiseran.structuregrader.NamedSet;

import javax.annotation.CheckForNull;

/**
 * A visitor of a NamedList of type ITEM.
 *
 * @param <ITEM> The type of the NamedList that implementors can visit.
 */
public interface SetVisitor<ITEM> {
	/**
	 * @param collection The NamedList of type ITEM being visited.
	 */
	void visit(@CheckForNull final NamedSet<ITEM> collection);
}