package edu.kaiseran.structuregrader.visitors;

import lombok.NonNull;

import javax.annotation.CheckForNull;

/**
 * A visitor for an object of a specific type.
 *
 * @param <ITEM> The type of the object this visitor can visit.
 */
public interface ItemVisitor<ITEM> {
	/**
	 * Visits the provided item.
	 *
	 * @param item The item to visit.
	 */
	void visit(@CheckForNull final ITEM item);

	/**
	 * Returns the name of the parent element of the visitee.
	 *
	 * @return the name of the parent element of the visitee.
	 */
	String getParentName();
}
