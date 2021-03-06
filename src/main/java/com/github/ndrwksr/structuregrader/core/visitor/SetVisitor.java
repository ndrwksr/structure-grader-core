package com.github.ndrwksr.structuregrader.core.visitor;

import com.github.ndrwksr.structuregrader.core.NamedSet;

import javax.annotation.CheckForNull;

/**
 * A visitor of a NamedList of type ITEM.
 *
 * @param <ITEM> The type of the NamedList that implementors can visit.
 */
public interface SetVisitor<ITEM> extends Spec {
	/**
	 * @param collection The NamedList of type ITEM being visited.
	 */
	void visit(@CheckForNull final NamedSet<ITEM> collection);
}
