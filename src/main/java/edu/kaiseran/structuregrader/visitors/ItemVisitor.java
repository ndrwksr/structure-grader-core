package edu.kaiseran.structuregrader.visitors;

import javax.annotation.CheckForNull;

/**
 * @param <ITEM>
 */
public interface ItemVisitor<ITEM> {
	void visit(@CheckForNull final ITEM item);
}
