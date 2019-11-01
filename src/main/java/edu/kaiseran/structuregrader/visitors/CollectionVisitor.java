package edu.kaiseran.structuregrader.visitors;

import edu.kaiseran.structuregrader.NamedCollection;

import javax.annotation.CheckForNull;

public interface CollectionVisitor<ITEM> {
	void visit(@CheckForNull final NamedCollection<ITEM> collection);
}
