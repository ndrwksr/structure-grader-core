package edu.kaiseran.structuregrader.visitor;

import edu.kaiseran.structuregrader.wrapper.ClassWrapper;

/**
 * Type alias for an ItemVisitor which only visits ClassWrappers.
 */
public interface ClassVisitor extends ItemVisitor<ClassWrapper> {
}
