package com.github.ndrwksr.structuregrader.core.visitor;

import com.github.ndrwksr.structuregrader.core.NamedMap;

import javax.annotation.CheckForNull;

/**
 * A visitor for a NamedCollection of type ITEM.
 *
 * @param <ITEM> The type of item in the collection.
 */
public interface MapVisitor<ITEM> extends Spec {
	void visit(@CheckForNull final NamedMap<ITEM> collection);
}
