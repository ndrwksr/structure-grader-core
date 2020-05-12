package com.github.ndrwksr.structuregrader.core.visitor;

import javax.annotation.CheckForNull;

/**
 * A visitor for an object of a specific type.
 *
 * @param <ITEM> The type of the object this visitor can visit.
 */
public interface ItemVisitor<ITEM> extends Spec {
	/**
	 * Visits the provided item.
	 *
	 * @param item The item to visit.
	 */
	void visit(@CheckForNull final ITEM item);
}
