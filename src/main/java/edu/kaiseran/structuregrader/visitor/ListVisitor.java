package edu.kaiseran.structuregrader.visitor;

import edu.kaiseran.structuregrader.NamedList;

import javax.annotation.CheckForNull;

/**
 * A visitor of a NamedList of type ITEM.
 *
 * @param <ITEM> The type of the NamedList that implementors can visit.
 */
public interface ListVisitor<ITEM> {
	/**
	 * @param collection The NamedList of type ITEM being visited.
	 */
	void visit(@CheckForNull final NamedList<ITEM> collection);
}
