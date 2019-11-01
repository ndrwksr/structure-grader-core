package edu.kaiseran.structuregrader.visitors;

import javax.annotation.CheckForNull;

public interface ItemVisitor<ITEM> {
	void visit(@CheckForNull final ITEM item);
}
