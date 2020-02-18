package edu.kaiseran.structuregrader.core.visitor;

import edu.kaiseran.structuregrader.core.wrapper.ClassWrapper;

/**
 * Type alias for an ItemVisitor which only visits ClassWrappers.
 */
public interface ClassVisitor extends ItemVisitor<ClassWrapper> {
}
