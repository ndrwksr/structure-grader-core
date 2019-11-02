package edu.kaiseran.structuregrader.visitors;

import edu.kaiseran.structuregrader.wrappers.ClassWrapper;

/**
 * Type alias for an ItemVisitor which only visits ClassWrappers.
 */
public interface ClassVisitor extends ItemVisitor<ClassWrapper> {
}
