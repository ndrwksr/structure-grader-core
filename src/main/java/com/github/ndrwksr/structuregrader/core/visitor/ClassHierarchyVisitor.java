package com.github.ndrwksr.structuregrader.core.visitor;

/**
 * Type alias for a visitor which can visit ClassWrappers and NamedCollections of ClassWrappers.
 * See type alias section in README.md for further explanation of type aliases.
 */
public interface ClassHierarchyVisitor extends ClassVisitor, ClassMapVisitor {
}
